package org.wisestar.lottery.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.cache.annotation.Cacheable;
import org.wisestar.lottery.entity.User;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author zhangxu
 * @date 2017/10/23
 */
public interface UserMapper extends Mapper<User> {

    @Cacheable(key = "#p0", value = "users")
    @Select("select * from users where openId = #{openId}")
    User getByOpenId(String openId);

    @Update("update users set sessionKey = #{sessionKey} where openId = #{openId}")
    int updateSessionKey(@Param("openId") String openId,
                         @Param("sessionKey") String sessionKey);
}
