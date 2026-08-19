package top.hzwhzw.iwuserservice.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailUtil {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 发送验证码邮件
     * @param toEmail 收件邮箱
     * @param code 验证码
     * @throws MessagingException 邮件发送异常
     */
    public void sendCodeEmail(String toEmail, String code) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("注册验证码");

            String content = "<h3>您的注册验证码：" + code + "</h3>"
                    + "<p>5分钟内有效，请不要泄露给他人</p>";
            helper.setText(content, true);

            mailSender.send(message);
            // 发送成功打印日志，方便确认是否执行到发送
            log.info("验证码邮件发送成功，收件邮箱：{}，验证码：{}", toEmail, code);
        } catch (MessagingException e) {
            // 打印完整异常堆栈，服务器必能看到报错
            log.error("验证码邮件发送失败！收件邮箱：{}", toEmail, e);
            // 重新抛出异常，交给全局异常处理器捕获返回前端提示
            throw e;
        }
    }
}
