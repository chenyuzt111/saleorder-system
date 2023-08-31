package com.benewake.saleordersystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.saleordersystem.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {


    @Update("UPDATE fim_users_table SET FIM_user_YC = #{ycValue}, FIM_user_XD = #{xdValue}, FIM_user_PR = #{prValue} WHERE FIM_user_id = #{userId}")
    int updateUserValues(@Param("userId") Long id, @Param("ycValue") String ycvalue,
                          @Param("xdValue") String xdvalue, @Param("prValue") String prvalue);
}
