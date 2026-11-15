package edu.cwi.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import edu.cwi.models.Transaction;

public interface TransactionMapper {

    @Insert("""
        INSERT INTO transactions
            (account_id, transaction_type, amount, description)
        VALUES
            (#{accountId}, #{transactionType}, #{amount}, #{description})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "transactionId",
             keyColumn = "transaction_id")
    int insert(Transaction transaction);

    @Select("""
        SELECT
            transaction_id,
            account_id,
            transaction_type,
            amount,
            description,
            created_at
        FROM transactions
        WHERE account_id = #{accountId}
        ORDER BY created_at DESC
        """)
    List<Transaction> selectByAccountId(Long accountId);
}