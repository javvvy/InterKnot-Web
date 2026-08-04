package top.hzwhzw.iwapi.client;

import dto.CoverDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import vo.CoverVO;

// value指定服务在注册中心的名字
@FeignClient(value = "interknot-article", path = "/article")
public interface ArticleClient {
    // 插入封面
    @PostMapping("/insertCover")
    CoverVO insertCover(CoverDTO cover);
}
