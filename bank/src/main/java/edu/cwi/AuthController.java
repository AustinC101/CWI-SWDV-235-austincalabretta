package edu.cwi;

import org.apache.ibatis.session.SqlSession;

import edu.cwi.database.MyBatisConfig;
import edu.cwi.mappers.UserMapper;
import edu.cwi.models.User;
import io.javalin.http.Context;

public class AuthController {

    public static void authenticate(Context ctx) {

        UsersController usersController = new UsersController();

        String username = ctx.formParam("username");
        String password = ctx.formParam("password");

        User user = usersController.authenticateUser(
                ctx,
                username,
                password
        );

        if (user != null) {

            ctx.cookieStore().set(
                    "user-id",
                    user.getUserId()
            );

            ctx.cookieStore().set(
                    "user-role",
                    user.getRole()
            );

            ctx.redirect("/accounts");

        } else {

            ctx.cookieStore().clear();

            ctx.redirect("/unauthorized.html");
        }
    }

    public static void logout(Context ctx) {

        ctx.cookieStore().clear();

        ctx.redirect("/");
    }

    public static User getUser(Context ctx) {

        Object userIdValue = ctx.cookieStore().get("user-id");

        if (userIdValue == null) {
            return null;
        }

        try {
            Long userId = Long.valueOf(userIdValue.toString());

            try (SqlSession session =
                    MyBatisConfig.getSqlSessionFactory().openSession()) {

                UserMapper userMapper =
                        session.getMapper(UserMapper.class);

                return userMapper.selectById(userId);
            }

        } catch (NumberFormatException e) {
            return null;
        }
    }
}