package edu.cwi;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.mindrot.jbcrypt.BCrypt;

import edu.cwi.database.MyBatisConfig;
import edu.cwi.mappers.AccountMapper;
import edu.cwi.mappers.TransactionMapper;
import edu.cwi.mappers.UserMapper;
import edu.cwi.models.Account;
import edu.cwi.models.Transaction;
import edu.cwi.models.User;
import io.javalin.apibuilder.CrudHandler;
import io.javalin.http.Context;

public class UsersController implements CrudHandler {

    private static final BigDecimal INITIAL_BALANCE =
            new BigDecimal("20.00");

    private static final String INITIAL_DEPOSIT_DESCRIPTION =
            "Initial account deposit";

    private Map<String, Object> userResponse(User user) {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("userId", user.getUserId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("role", user.getRole());

        return response;
    }

    @Override
    public void getAll(Context ctx) {

        try (SqlSession session =
                MyBatisConfig.getSqlSessionFactory().openSession()) {

            UserMapper mapper = session.getMapper(UserMapper.class);

            List<Map<String, Object>> users =
                    mapper.selectAll()
                            .stream()
                            .map(this::userResponse)
                            .toList();

            ctx.json(users);

        } catch (Exception e) {

            ctx.status(500).json(Map.of(
                    "error", "Could not retrieve users"
            ));
        }
    }

    @Override
    public void getOne(Context ctx, String resourceId) {

        try {

            Long userId = Long.valueOf(resourceId);

            try (SqlSession session =
                    MyBatisConfig.getSqlSessionFactory().openSession()) {

                UserMapper mapper =
                        session.getMapper(UserMapper.class);

                User user = mapper.selectById(userId);

                if (user == null) {
                    ctx.status(404).json(Map.of(
                            "error", "User not found"
                    ));
                    return;
                }

                ctx.json(userResponse(user));
            }

        } catch (NumberFormatException e) {

            ctx.status(400).json(Map.of(
                    "error", "Invalid user ID"
            ));

        } catch (Exception e) {

            ctx.status(500).json(Map.of(
                    "error", "Could not retrieve user"
            ));
        }
    }

    @Override
    public void create(Context ctx) {

        User user;

        try {
            user = ctx.bodyAsClass(User.class);

        } catch (Exception e) {

            ctx.status(400).json(Map.of(
                    "error", "Invalid user data"
            ));
            return;
        }

        if (user.getUsername() == null
                || user.getUsername().isBlank()
                || user.getPassword() == null
                || user.getPassword().isBlank()
                || user.getEmail() == null
                || user.getEmail().isBlank()
                || user.getFirstName() == null
                || user.getFirstName().isBlank()
                || user.getLastName() == null
                || user.getLastName().isBlank()) {

            ctx.status(400).json(Map.of(
                    "error", "All user fields are required"
            ));
            return;
        }

        user.setRole("CUSTOMER");

        user.setPassword(
                BCrypt.hashpw(user.getPassword(), BCrypt.gensalt())
        );

        try (SqlSession session =
                MyBatisConfig.getSqlSessionFactory().openSession(false)) {

            UserMapper userMapper =
                    session.getMapper(UserMapper.class);

            AccountMapper accountMapper =
                    session.getMapper(AccountMapper.class);

            TransactionMapper transactionMapper =
                    session.getMapper(TransactionMapper.class);

            userMapper.insert(user);

            Account account = new Account();

            account.setUserId(user.getUserId());
            account.setAccountType("CHECKING");
            account.setBalance(INITIAL_BALANCE);

            accountMapper.insert(account);

            Transaction transaction = new Transaction();

            transaction.setAccountId(account.getAccountId());
            transaction.setTransactionType("DEPOSIT");
            transaction.setAmount(INITIAL_BALANCE);
            transaction.setDescription(
                    INITIAL_DEPOSIT_DESCRIPTION
            );

            transactionMapper.insert(transaction);

            session.commit();

            ctx.status(201).json(Map.of(
                    "message", "User created successfully",
                    "user", userResponse(user),
                    "account", Map.of(
                            "accountId", account.getAccountId(),
                            "accountType", account.getAccountType(),
                            "balance", account.getBalance()
                    ),
                    "transaction", Map.of(
                            "transactionId",
                            transaction.getTransactionId(),
                            "transactionType",
                            transaction.getTransactionType(),
                            "amount",
                            transaction.getAmount(),
                            "description",
                            transaction.getDescription()
                    )
            ));

        } catch (Exception e) {

            ctx.status(500).json(Map.of(
                    "error", "Could not create user"
            ));
        }
    }

    @Override
    public void update(Context ctx, String resourceId) {

        ctx.status(405).json(Map.of(
                "error", "User updates are not supported"
        ));
    }

    @Override
    public void delete(Context ctx, String resourceId) {

        ctx.status(405).json(Map.of(
                "error", "User deletion is not supported"
        ));
    }

    public User authenticateUser(
            Context ctx,
            String username,
            String password) {

        if (username == null || password == null) {
            return null;
        }

        try (SqlSession session =
                MyBatisConfig.getSqlSessionFactory().openSession()) {

            UserMapper mapper =
                    session.getMapper(UserMapper.class);

            User user = mapper.selectByUsername(username);

            if (user == null) {
                return null;
            }

            if (BCrypt.checkpw(password, user.getPassword())) {
                return user;
            }

            return null;

        } catch (Exception e) {

            return null;
        }
    }
}