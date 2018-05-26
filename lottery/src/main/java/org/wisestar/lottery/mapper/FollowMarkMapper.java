package org.wisestar.lottery.mapper;

import org.apache.ibatis.annotations.Param;
import org.wisestar.lottery.entity.FollowMark;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author zhangxu
 * @date 2017/11/15
 */
public interface FollowMarkMapper extends Mapper<FollowMark> {

    List<FollowMark> findCurrent(@Param("types") List<Integer> types);

}
