package top.hzwhzw.iwfileservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pojo.Result;
import top.hzwhzw.iwfileservice.service.FileService;

import java.io.IOException;

@RestController
@Slf4j
@RequestMapping
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;
    @PostMapping("/media/upload")
    public Result upload(@RequestParam("file") MultipartFile fileMultipartFile,
                         @RequestParam("scene") String scene,
                         @RequestParam("no") String no) throws IOException {
        log.info("上传文件");
        //scene: 场景包括"avatar","article"等
        //no: 文章业务ID或草稿业务id或用户业务id等
        return Result.success(fileService.upload(fileMultipartFile, scene, no));
    }
}
