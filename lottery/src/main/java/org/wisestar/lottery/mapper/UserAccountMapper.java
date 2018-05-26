package org.wisestar.lottery.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.wisestar.lottery.entity.UserAccount;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author zhangxu
 * @date 2017/11/2
 */
public interface UserAccountMapper extends Mapper<UserAccount> {

    @Select("select * from userAccount where openId = #{openId}")
    UserAccount getByOpenId(String openId);

    int updateBalance(@Param("openId") String openId,
                      @Param("balance") Double balance);

    int updateBankInfo(@Param("openId") String openId,
                       @Param("bankName") String bankName,
                       @Param("bankAccount") String bankAccount,
                       @Param("bankCard") String bankCard);
}
