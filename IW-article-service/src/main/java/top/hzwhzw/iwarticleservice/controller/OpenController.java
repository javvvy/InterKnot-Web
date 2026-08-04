package top.hzwhzw.iwarticleservice.controller;

import dto.CoverDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pojo.Result;
import top.hzwhzw.iwarticleservice.service.OpenService;
import vo.CoverVO;

@Slf4j
@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
public class OpenController {
    private final OpenService openService;
    //插入封面
    @PostMapping("/insertCover")
    public CoverVO insertCover(@RequestBody CoverDTO coverDTO) {
        return openService.insertCover(coverDTO);
    }
}
