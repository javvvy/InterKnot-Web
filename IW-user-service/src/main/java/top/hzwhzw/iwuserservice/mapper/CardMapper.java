package top.hzwhzw.iwuserservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.hzwhzw.iwuserservice.pojo.Card;

import java.util.List;

@Mapper
public interface CardMapper extends BaseMapper<Card> {
    int insertBatchSomeColumn(List<Card> cardList);
}
