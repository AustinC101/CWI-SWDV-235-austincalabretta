package edu.cwi.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import edu.cwi.models.Account;

public interface AccountMapper {

    @Insert("""
        INSERT INTO accounts (user_id, account_type, balance)
        VALUES (#{userId}, #{accountType}, #{balance})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "accountId",
             keyColumn = "account_id")
    int insert(Account account);

    @Select("""
        SELECT account_id, user_id, account_type, balance
        FROM accounts
        WHERE user_id = #{userId}
        """)
    List<Account> selectByUserId(Long userId);

    @Select("""
        SELECT account_id, user_id, account_type, balance
        FROM accounts
        WHERE account_id = #{accountId}
        """)
    Account selectById(Long accountId);
}