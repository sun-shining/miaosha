package com.kaffa.miaosha.dao;

import com.kaffa.miaosha.domain.MiaoshaUser;
import com.kaffa.miaosha.domain.User;
import org.apache.ibatis.annotations.*;

/**
 * Created by Administrator on 2018/1/17.
 */
@Mapper
public interface UserDao {

    @Select("select * from miaosha_user where id = #{id}")
    public MiaoshaUser getUserById(@Param("id") Long id);

    @Insert("insert into user(id, name) values(#{id}, #{name})")
    public boolean insertUser(MiaoshaUser user);

    @Update("update miaosha_user t set t.password=#{password} where t.id=#{id}")
    public void update(MiaoshaUser toBeUpdate);
}
