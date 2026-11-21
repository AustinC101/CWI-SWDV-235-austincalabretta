package edu.cwi;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import edu.cwi.database.MyBatisConfig;
import edu.cwi.mappers.AccountMapper;
import edu.cwi.mappers.TransactionMapper;
import edu.cwi.models.Account;
import edu.cwi.models.Transaction;
import edu.cwi.models.User;
import io.javalin.Javalin;
import static io.javalin.apibuilder.ApiBuilder.crud;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinJte;

public class Main {
    public static void main(String[] args) {
        Thread.currentThread().setContextClassLoader(
            Main.class.getClassLoader()
        );

        UsersController usersController = new UsersController();

        TransactionsController transactionsController =
                new TransactionsController();

        Javalin app = Javalin.create(config -> {
            JavalinJte jte = new JavalinJte(
                JavalinJte.Companion.directoryTemplateEngine("src/main/resources/jte")
            );

            config.fileRenderer((filePath, model, ctx) -> {
                System.out.println("Main classloader: " +
                    Main.class.getClassLoader().getResource("gg/jte/Content.class"));

                System.out.println("Request thread classloader: " +
                    Thread.currentThread().getContextClassLoader().getResource("gg/jte/Content.class"));

                ClassLoader originalClassLoader =
                        Thread.currentThread().getContextClassLoader();

                try {
                    Thread.currentThread().setContextClassLoader(
                            Main.class.getClassLoader()
                    );

                    String templatePath = filePath.startsWith("/")
                            ? filePath.substring(1)
                            : filePath;

                    return jte.render(templatePath, model, ctx);

                } finally {
                    Thread.currentThread().setContextClassLoader(
                            originalClassLoader
                    );
                }
            });

            config.staticFiles.add("/public", Location.CLASSPATH);

            config.routes.apiBuilder(() -> {

                path("accounts", List.of(AppRole.CUSTOMER), () -> {
                    get(ctx -> {

                    User user = AuthController.getUser(ctx);

                    if (user == null) {
                        throw new UnauthorizedResponse();
                    }

                        try (SqlSession session =
                            MyBatisConfig.getSqlSessionFactory().openSession()) {

                            AccountMapper accountMapper =
                                session.getMapper(AccountMapper.class);

                            List<Account> accounts =
                                accountMapper.selectByUserId(user.getUserId());

                            ctx.render("/accountsPage.jte", Map.of(
                                "title", "Community Bank - Accounts",
                                "user", user,
                                "accounts", accounts
                            ));
                        }
                    });
                });

                path("transaction", List.of(AppRole.CUSTOMER), () -> {
                    post(transactionsController::createAccountTransaction);
                });

                path("account", List.of(AppRole.CUSTOMER), () -> {
                    get("/{id}", ctx -> {

                        User user = AuthController.getUser(ctx);

                        if (user == null) {
                            throw new UnauthorizedResponse();
                        }

                        Long accountId;

                        try {
                            accountId = Long.valueOf(ctx.pathParam("id"));
                        } catch (NumberFormatException e) {
                            ctx.status(400).result("Invalid account ID");
                            return;
                        }

                        try (SqlSession session =
                                MyBatisConfig.getSqlSessionFactory().openSession()) {

                            AccountMapper accountMapper =
                                    session.getMapper(AccountMapper.class);

                            TransactionMapper transactionMapper =
                                    session.getMapper(TransactionMapper.class);

                            Account account = accountMapper.selectById(accountId);

                            if (account == null ||
                                    !account.getUserId().equals(user.getUserId())) {

                                ctx.status(404).result("Account not found");
                                return;
                            }

                            List<Transaction> transactions =
                                    transactionMapper.selectByAccountId(accountId);

                            ctx.render("/accountDetailPage.jte", Map.of(
                                    "title", "Community Bank - Account Details",
                                    "user", user,
                                    "account", account,
                                    "transactions", transactions
                            ));
                        }
                    });
                });

                path("users", () -> {
                    crud("/{user-id}", usersController);
                });

                path("api/transactions", () -> {
                    crud("/{transaction-id}", transactionsController);
                });
            });

            config.routes.post("/login", ctx -> {
                AuthController.authenticate(ctx);
            });

            config.routes.beforeMatched(ctx -> {

                if (ctx.routeRoles().isEmpty()) {
                    return;
                }

                String role = ctx.cookieStore().get("user-role");

                if (role == null) {
                    throw new UnauthorizedResponse();
                }

                AppRole appRole = AppRole.valueOf(role);

                if (!ctx.routeRoles().contains(appRole)) {
                    throw new UnauthorizedResponse();
                }
            });

            config.routes.get("/logout", ctx -> {
                AuthController.logout(ctx);
            });
        });

        app.start(7070);
    }
}