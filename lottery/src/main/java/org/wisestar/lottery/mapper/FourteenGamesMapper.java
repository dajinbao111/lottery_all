package org.wisestar.lottery.mapper;

import org.apache.ibatis.annotations.Select;
import org.springframework.cache.annotation.Cacheable;
import org.wisestar.lottery.entity.FourteenGames;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author zhangxu
 */
public interface FourteenGamesMapper extends Mapper<FourteenGames> {

    @Select("select * from fourteenGames where gameId = #{gameId}")
    FourteenGames getByGameId(String gameId);

    @Select("select distinct phaseId from fourteenGames where endPostTime > now()")
    List<String> findCurrentPhaseId();

    @Cacheable(key = "#p0", value = "fourteenGames")
    @Select("select * from fourteenGames where phaseId = #{phaseId} order by serialId")
    List<FourteenGames> findListByCache(String phaseId);

    @Select("select * from fourteenGames where phaseId = #{phaseId} order by serialId")
    List<FourteenGames> findByPhaseId(String phaseId);
}
