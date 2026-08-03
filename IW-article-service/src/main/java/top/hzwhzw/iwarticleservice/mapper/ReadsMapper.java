package top.hzwhzw.iwarticleservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.hzwhzw.iwarticleservice.pojo.Reads;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReadsMapper extends BaseMapper<Reads> {
    // 批量插入方法声明
    int insertBatchSomeColumn(List<Reads> entityList);
}
