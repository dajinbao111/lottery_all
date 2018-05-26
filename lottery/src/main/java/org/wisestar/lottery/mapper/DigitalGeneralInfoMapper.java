package org.wisestar.lottery.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.wisestar.lottery.entity.DigitalGeneralInfo;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author zhangxu
 */
public interface DigitalGeneralInfoMapper extends Mapper<DigitalGeneralInfo> {

    @Select("select * from digitalGeneralInfo where typeId = #{typeId} and phaseId = #{phaseId} limit 1")
    DigitalGeneralInfo getByTypeIdAndPhaseId(@Param("typeId") Integer typeId, @Param("phaseId") String phaseId);
}
