package org.wisestar.lottery.mapper;

import org.apache.ibatis.annotations.Select;
import org.springframework.cache.annotation.Cacheable;
import org.wisestar.lottery.entity.FourteenGamesResult;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author zhangxu
 * @date 2017/10/27
 */
public interface FourteenGamesResultMapper extends Mapper<FourteenGamesResult> {

    @Select("select * from fourteenGamesResult where phaseId = #{phaseId} limit 1")
    FourteenGamesResult getByPhaseId(String phaseId);

    @Select("select * from fourteenGamesResult where prizeTime < concat(current_date(),' 23:59:59') and result is null")
    List<FourteenGamesResult> findResultIsNull();

    @Cacheable(key = "#p0", value = "fourteenGamesWithResult")
    @Select("select * from fourteenGamesResult where phaseId = #{phaseId} and result is not null limit 1")
    FourteenGamesResult getResultByPhaseId(String phaseId);
}
