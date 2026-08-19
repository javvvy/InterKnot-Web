package top.hzwhzw.iwchatservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import top.hzwhzw.iwchatservice.config.DeepSeekConfig;
import top.hzwhzw.iwchatservice.mapper.CharacterMapper;
import top.hzwhzw.iwchatservice.mapper.KKCallConversationMapper;
import top.hzwhzw.iwchatservice.mapper.KKCallMessageMapper;
import top.hzwhzw.iwchatservice.pojo.Character;
import top.hzwhzw.iwchatservice.pojo.KKCallConversation;
import top.hzwhzw.iwchatservice.pojo.KKCallMessage;
import top.hzwhzw.iwchatservice.pojo.KkCallDeltaEvent;
import top.hzwhzw.iwchatservice.pojo.KkCallDoneEvent;
import top.hzwhzw.iwchatservice.pojo.KkCallErrorEvent;
import top.hzwhzw.iwchatservice.pojo.KkCallSessionMaterializedEvent;
import top.hzwhzw.iwchatservice.service.KKCallService;
import utils.UserContextHolder;
import vo.CharacterVO;
import vo.KKCallConversationVO;
import vo.KKCallMessageVO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KKCallServiceImpl implements KKCallService {
    private final CharacterMapper characterMapper;
    private final KKCallConversationMapper kkCallConversationMapper;
    private final KKCallMessageMapper kkCallMessageMapper;
    private final DeepSeekConfig deepSeekConfig;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String PSEUDO_PREFIX = "pseudo:char:";

    @Override
    public List<KKCallConversationVO> getConversations() {
        Long userId = UserContextHolder.getUserId();
        List<Character> allCharacters = characterMapper.selectList(new LambdaQueryWrapper<Character>()
                .orderByAsc(Character::getDisplayOrder));
        List<KKCallConversation> realConversations = kkCallConversationMapper.selectList(
                new LambdaQueryWrapper<KKCallConversation>()
                        .eq(KKCallConversation::getUserId, userId));

        Map<String, KKCallConversationVO> sessionByCharacterNo = new HashMap<>();
        for (KKCallConversation conv : realConversations) {
            sessionByCharacterNo.put(conv.getCharacterNo(), toConversationVO(conv));
        }

        List<KKCallConversationVO> result = new ArrayList<>();
        for (Character character : allCharacters) {
            KKCallConversationVO existing = sessionByCharacterNo.get(character.getCharacterNo());
            if (existing != null) {
                existing.setIsPseudo(false);
                result.add(existing);
            } else {
                KKCallConversationVO pseudo = new KKCallConversationVO();
                pseudo.setConversationNo(PSEUDO_PREFIX + character.getCharacterNo());
                pseudo.setIsPseudo(true);
                pseudo.setCharacter(toCharacterVO(character));
                pseudo.setLastMessageAt(null);
                pseudo.setLastPreview("");
                result.add(pseudo);
            }
        }

        // 有消息的会话按 lastMessageAt 降序在前，pseudo 会话按 displayOrder 升序在后
        result.sort((a, b) -> {
            if (a.getLastMessageAt() == null && b.getLastMessageAt() == null) {
                int ao = a.getCharacter() != null && a.getCharacter().getDisplayOrder() != null
                        ? a.getCharacter().getDisplayOrder() : 0;
                int bo = b.getCharacter() != null && b.getCharacter().getDisplayOrder() != null
                        ? b.getCharacter().getDisplayOrder() : 0;
                return Integer.compare(ao, bo);
            }
            if (a.getLastMessageAt() == null) return 1;
            if (b.getLastMessageAt() == null) return -1;
            return b.getLastMessageAt().compareTo(a.getLastMessageAt());
        });
        return result;
    }

    @Override
    public List<KKCallMessageVO> getSessionMessages(String conversationNo) {
        if (conversationNo.startsWith(PSEUDO_PREFIX)) {
            return Collections.emptyList();
        }
        List<KKCallMessage> messages = kkCallMessageMapper.selectList(new LambdaQueryWrapper<KKCallMessage>()
                .eq(KKCallMessage::getConversationNo, conversationNo)
                .orderByDesc(KKCallMessage::getCreatedAt));
        return messages.stream().map(this::toMessageVO).toList();
    }

    @Override
    public SseEmitter sendMessage(String conversationNo, String content) {
        Long userId = UserContextHolder.getUserId();
        SseEmitter emitter = new SseEmitter(5 * 60 * 1000L);

        new Thread(() -> {
            try {
                String realConversationNo = conversationNo;
                Character character = null;

                // 1. 处理虚拟会话实质化
                if (conversationNo.startsWith(PSEUDO_PREFIX)) {
                    String characterNo = conversationNo.substring(PSEUDO_PREFIX.length());
                    KKCallConversation existing = kkCallConversationMapper.selectOne(
                            new LambdaQueryWrapper<KKCallConversation>()
                                    .eq(KKCallConversation::getUserId, userId)
                                    .eq(KKCallConversation::getCharacterNo, characterNo));
                    if (existing != null) {
                        realConversationNo = existing.getConversationNo();
                    } else {
                        realConversationNo = IdUtil.getSnowflakeNextIdStr();
                        existing = new KKCallConversation();
                        existing.setConversationNo(realConversationNo);
                        existing.setUserId(userId);
                        existing.setCharacterNo(characterNo);
                        existing.setIsPseudo(false);
                        existing.setLastPreview("");
                        existing.setCreatedAt(LocalDateTime.now());
                        kkCallConversationMapper.insert(existing);
                    }
                    character = characterMapper.selectOne(new LambdaQueryWrapper<Character>()
                            .eq(Character::getCharacterNo, characterNo));

                    KkCallSessionMaterializedEvent materializedEvent = new KkCallSessionMaterializedEvent();
                    materializedEvent.setSessionId(realConversationNo);
                    materializedEvent.setCharacter(toCharacterVO(character));
                    sendSseEvent(emitter, "session.materialized", materializedEvent);
                }

                // 获取角色信息（用于 AI 调用）
                if (character == null) {
                    KKCallConversation conv = kkCallConversationMapper.selectOne(
                            new LambdaQueryWrapper<KKCallConversation>()
                                    .eq(KKCallConversation::getConversationNo, realConversationNo));
                    if (conv != null) {
                        character = characterMapper.selectOne(new LambdaQueryWrapper<Character>()
                                .eq(Character::getCharacterNo, conv.getCharacterNo()));
                    }
                }

                // 2. 持久化用户消息
                String userMessageNo = IdUtil.getSnowflakeNextIdStr();
                LocalDateTime now = LocalDateTime.now();
                KKCallMessage userMessage = new KKCallMessage();
                userMessage.setMessageNo(userMessageNo);
                userMessage.setConversationNo(realConversationNo);
                userMessage.setRole("user");
                userMessage.setContent(content);
                userMessage.setPending(false);
                userMessage.setCreatedAt(now);
                kkCallMessageMapper.insert(userMessage);
                updateConversationLastMessage(realConversationNo, now, preview(content));

                sendSseEvent(emitter, "message.user.created", toMessageVO(userMessage));

                // 3. 创建 assistant 消息占位
                String assistantMessageNo = IdUtil.getSnowflakeNextIdStr();
                LocalDateTime assistantCreatedAt = LocalDateTime.now();
                KKCallMessage assistantMessage = new KKCallMessage();
                assistantMessage.setMessageNo(assistantMessageNo);
                assistantMessage.setConversationNo(realConversationNo);
                assistantMessage.setRole("assistant");
                assistantMessage.setContent("");
                assistantMessage.setPending(true);
                assistantMessage.setCreatedAt(assistantCreatedAt);
                kkCallMessageMapper.insert(assistantMessage);

                sendSseEvent(emitter, "message.assistant.started", toMessageVO(assistantMessage));

                // 4. 调用 AI 流式生成
                StringBuilder fullResponse = new StringBuilder();
                try {
                    callDeepSeekStream(realConversationNo, content, character, delta -> {
                        fullResponse.append(delta);
                        KkCallDeltaEvent deltaEvent = new KkCallDeltaEvent();
                        deltaEvent.setDelta(delta);
                        sendSseEvent(emitter, "message.assistant.delta", deltaEvent);
                    });
                } catch (Exception e) {
                    log.error("AI 调用失败", e);
                    String errorCode = e.getMessage() != null && e.getMessage().contains("rate_limited")
                            ? "llm_rate_limited" : "stream_error";
                    updateMessage(assistantMessageNo, "", false, errorCode);
                    KkCallErrorEvent errorEvent = new KkCallErrorEvent();
                    errorEvent.setCode(errorCode);
                    sendSseEvent(emitter, "error", errorEvent);
                    emitter.complete();
                    return;
                }

                // 5. 完成 - 更新 assistant 消息
                String finalContent = fullResponse.toString();
                updateMessage(assistantMessageNo, finalContent, false, null);
                updateConversationLastMessage(realConversationNo, LocalDateTime.now(), preview(finalContent));

                KkCallDoneEvent doneEvent = new KkCallDoneEvent();
                doneEvent.setContent(finalContent);
                sendSseEvent(emitter, "message.assistant.done", doneEvent);

                emitter.complete();
            } catch (Exception e) {
                log.error("KKCall SSE 流式响应异常", e);
                try {
                    KkCallErrorEvent errorEvent = new KkCallErrorEvent();
                    errorEvent.setCode("stream_error");
                    sendSseEvent(emitter, "error", errorEvent);
                } catch (Exception ignored) {
                }
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

    private void callDeepSeekStream(String conversationNo, String userContent, Character character,
                                    Consumer<String> onDelta) throws IOException {
        List<KKCallMessage> history = kkCallMessageMapper.selectList(new LambdaQueryWrapper<KKCallMessage>()
                .eq(KKCallMessage::getConversationNo, conversationNo)
                .orderByDesc(KKCallMessage::getCreatedAt)
                .last("LIMIT 20"));
        Collections.reverse(history);

        List<Map<String, String>> messages = new ArrayList<>();

        String charName = character != null ? character.getName() : "角色";
        String charTags = "";
        if (character != null && character.getTags() != null && !character.getTags().isEmpty()) {
            charTags = character.getTags();
        }
        Map<String, String> sysMsg = new LinkedHashMap<>();
        sysMsg.put("role", "system");
        sysMsg.put("content", buildSystemPrompt(charName, charTags));
        messages.add(sysMsg);

        for (KKCallMessage msg : history) {
            if (msg.getContent() == null || msg.getContent().isEmpty()) {
                continue;
            }
            Map<String, String> m = new LinkedHashMap<>();
            m.put("role", msg.getRole());
            m.put("content", msg.getContent());
            messages.add(m);
        }

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("model", deepSeekConfig.getModel());
        requestBody.put("messages", messages);
        requestBody.put("stream", true);
        requestBody.put("temperature", 0.8);
        requestBody.put("max_tokens", 1024);

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        HttpURLConnection conn = (HttpURLConnection) URI.create(
                deepSeekConfig.getBaseUrl() + "/v1/chat/completions").toURL().openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + deepSeekConfig.getApiKey());
        conn.setRequestProperty("Accept", "text/event-stream");
        conn.setDoOutput(true);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(120000);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }

        int status = conn.getResponseCode();
        if (status != 200) {
            try (BufferedReader errReader = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                String errBody = errReader.lines().collect(Collectors.joining("\n"));
                log.error("DeepSeek API 返回错误 status={}, body={}", status, errBody);
            }
            if (status == 429) {
                throw new RuntimeException("llm_rate_limited");
            }
            throw new RuntimeException("llm_error_http_" + status);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("data: ")) {
                    String data = line.substring(6).trim();
                    if ("[DONE]".equals(data)) {
                        break;
                    }
                    try {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> chunk = objectMapper.readValue(data, Map.class);
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> choices = (List<Map<String, Object>>) chunk.get("choices");
                        if (choices != null && !choices.isEmpty()) {
                            Map<String, Object> choice = choices.get(0);
                            @SuppressWarnings("unchecked")
                            Map<String, Object> delta = (Map<String, Object>) choice.get("delta");
                            if (delta != null) {
                                Object deltaContent = delta.get("content");
                                if (deltaContent != null && !deltaContent.toString().isEmpty()) {
                                    onDelta.accept(deltaContent.toString());
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.debug("解析 SSE 块失败: {}", data, e);
                    }
                }
            }
        }
    }

    private String buildSystemPrompt(String charName, String charTags) {
        return String.format(
                "你现在严格扮演《绝区零》游戏内的NPC角色【%s】" +
                "（%s），全程沉浸角色扮演，绝对不能暴露自己是AI、不跳出人设、不解释规则、不回复技术相关内容。\n\n" +
                "【世界观硬性规则（必须遵守，不能篡改）】\n" +
                "1. 故事舞台：新艾利都，人类文明最后的城市，外界被「空洞」吞噬，空洞里存在怪物「以骸」；\n" +
                "2. 用户身份：法厄同兄妹（绳匠，狡兔屋店主），是你的长期搭档、信赖的伙伴，所有人设对话都基于你们熟悉、有羁绊的关系；\n" +
                "3. 职业设定：绳匠负责规划空洞探索路线、统筹代理人小队、经营狡兔屋；邦布是小型智能辅助机器人；\n" +
                "4. 城市势力：白祇重工、维多利亚家政、狡兔屋、治安局、HAND、TOPS财团、称颂会、怀斯塔学会；\n" +
                "5. 通用常识：以太适性、空洞调查、光映商场、引力影院、404Live、瀑汤谷面馆、热望角等地点都可以正常互动；\n" +
                "6. 禁止自创剧情：不能凭空编造不存在的角色、地点、势力、事件，所有内容贴合官方主线+信赖邀约剧情。\n\n" +
                "【用户（绳匠）交互规则】\n" +
                "1. 用户是和你朝夕相处的店主，你们一起执行空洞任务、日常闲聊、逛街、吃饭、看演出；\n" +
                "2. 根据角色性格区分称呼：亲近角色直接叫\"绳匠\"\"老板\"；疏离角色礼貌称\"代理人\"；\n" +
                "3. 支持多轮连续对话，记住之前聊天内容，不会遗忘你们的过往互动；\n" +
                "4. 用户可以邀约逛街、吃饭、看电影、吐槽任务、聊空洞危险、吐槽各势力、聊邦布；\n" +
                "5. 用户无论说什么，都站在该NPC自身立场回应，不强行中立、不强行说教。\n\n" +
                "【对话输出格式规范（强制）】\n" +
                "1. 回复分两部分：括号内动作神态描写 + 人物台词，贴合游戏对话演出风格；\n" +
                "   示例：（指尖卷了卷耳边发丝，语气慵懒）今天还要去空洞出任务吗？\n" +
                "2. 台词简短口语化，短句为主，拒绝大段书面文字；\n" +
                "3. 贴合角色语气：傲娇、爽朗、毒舌、温柔、腹黑、热血严格区分；\n" +
                "4. 不使用现代网络烂梗、不出现三次元词汇；\n" +
                "5. 不主动跳出对话，每轮结尾可以自然抛出一个小话题，维持聊天延续性；\n" +
                "6. 禁止出现\"我是AI\"\"我只是扮演角色\"\"需要我为你介绍\"这类破坏沉浸的句子。\n\n" +
                "【安全边界】\n" +
                "1. 拒绝低俗、暧昧过度、越界恋爱向内容，保持游戏原作健康羁绊氛围；\n" +
                "2. 涉及暴力空洞战斗仅客观描述，不细致描写血腥；\n" +
                "3. 若用户提问脱离绝区零世界观，委婉转移回新艾利都日常剧情，不生硬拒绝。",
                charName, charTags);
    }

    private void updateConversationLastMessage(String conversationNo, LocalDateTime lastMessageAt, String lastPreview) {
        kkCallConversationMapper.update(null, new LambdaUpdateWrapper<KKCallConversation>()
                .eq(KKCallConversation::getConversationNo, conversationNo)
                .set(KKCallConversation::getLastMessageAt, lastMessageAt)
                .set(KKCallConversation::getLastPreview, lastPreview));
    }

    private void updateMessage(String messageNo, String content, Boolean pending, String errorReason) {
        kkCallMessageMapper.update(null, new LambdaUpdateWrapper<KKCallMessage>()
                .eq(KKCallMessage::getMessageNo, messageNo)
                .set(KKCallMessage::getContent, content)
                .set(KKCallMessage::getPending, pending)
                .set(KKCallMessage::getErrorReason, errorReason));
    }

    private String preview(String content) {
        if (content == null) {
            return "";
        }
        return content.length() > 50 ? content.substring(0, 50) : content;
    }

    private KKCallConversationVO toConversationVO(KKCallConversation conv) {
        KKCallConversationVO vo = BeanUtil.copyProperties(conv, KKCallConversationVO.class);
        if (conv.getCharacterNo() != null) {
            Character character = characterMapper.selectOne(new LambdaQueryWrapper<Character>()
                    .eq(Character::getCharacterNo, conv.getCharacterNo()));
            if (character != null) {
                vo.setCharacter(toCharacterVO(character));
            }
        }
        return vo;
    }

    private CharacterVO toCharacterVO(Character character) {
        return character == null ? null : BeanUtil.copyProperties(character, CharacterVO.class);
    }

    private KKCallMessageVO toMessageVO(KKCallMessage message) {
        return BeanUtil.copyProperties(message, KKCallMessageVO.class);
    }

    private void sendSseEvent(SseEmitter emitter, String eventName, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .name(eventName)
                    .data(data, MediaType.APPLICATION_JSON));
        } catch (IOException e) {
            log.error("发送 SSE 事件失败 event={}", eventName, e);
        }
    }
}
