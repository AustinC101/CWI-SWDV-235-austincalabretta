package edu.cwi.mappers;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import edu.cwi.models.User;

public interface UserMapper {

    @Select("""
        SELECT
            user_id,
            username,
            password,
            email,
            first_name,
            last_name,
            role
        FROM users
        WHERE username = #{username}
        """)
    User selectByUsername(String username);

    @Select("""
        SELECT
            user_id,
            username,
            password,
            email,
            first_name,
            last_name,
            role
        FROM users
        WHERE user_id = #{userId}
        """)
    User selectById(Long userId);

    @Select("""
        SELECT
            user_id,
            username,
            password,
            email,
            first_name,
            last_name,
            role
        FROM users
        """)
    java.util.List<User> selectAll();

    @Insert("""
        INSERT INTO users
            (username, password, email, first_name, last_name, role)
        VALUES
            (#{username}, #{password}, #{email},
             #{firstName}, #{lastName}, #{role})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "userId",
             keyColumn = "user_id")
    int insert(User user);
}