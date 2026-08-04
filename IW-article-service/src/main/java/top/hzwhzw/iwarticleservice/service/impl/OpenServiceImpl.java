package top.hzwhzw.iwarticleservice.service.impl;

import cn.hutool.core.util.IdUtil;
import dto.CoverDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import top.hzwhzw.iwarticleservice.mapper.CoverMapper;
import top.hzwhzw.iwarticleservice.pojo.Cover;
import top.hzwhzw.iwarticleservice.service.OpenService;
import vo.CoverVO;

@Service
@RequiredArgsConstructor
public class OpenServiceImpl implements OpenService {
    private final CoverMapper coverMapper;
    @Override
    public CoverVO insertCover(CoverDTO coverDTO) {
        Cover cover = new Cover();
        BeanUtils.copyProperties(coverDTO, cover);
        //TODO 枚举???
        cover.setCoverNo("cover-"+ IdUtil.getSnowflakeNextIdStr());
        coverMapper.insert(cover);
        CoverVO coverVO = new CoverVO();
        BeanUtils.copyProperties(cover, coverVO);
        return coverVO;
    }
}
