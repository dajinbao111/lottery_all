package org.wisestar.lottery.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.wisestar.lottery.entity.UserAccountRecord;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author zhangxu
 * @date 2017/11/2
 */
public interface UserAccountRecordMapper extends Mapper<UserAccountRecord> {

    List<UserAccountRecord> findByOpenIdAndAccountChange(@Param("openId") String openId,
                                                         @Param("accountChange") Integer accountChange);

    @Select("select * from userAccountRecord where accountChange = 3 and approval = 0")
    List<UserAccountRecord> findWithdraw();

    @Update("update userAccountRecord set approval = 1 where id = #{recordId} and approval = 0")
    int updateApproval(Long recordId);

}
