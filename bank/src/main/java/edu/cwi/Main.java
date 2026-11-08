package edu.cwi;

import java.util.List;
import java.util.Map;

import io.javalin.Javalin;
import static io.javalin.apibuilder.ApiBuilder.crud;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinJte;

public class Main {
    public static void main(String[] args) {

        TransactionsController transactionsController =
                new TransactionsController();

        Javalin app = Javalin.create(config -> {
            JavalinJte jte = new JavalinJte(
                JavalinJte.Companion.directoryTemplateEngine("bank/src/main/resources/jte")
            );

            config.fileRenderer((filePath, model, ctx) -> {
                String templatePath = filePath.startsWith("/")
                        ? filePath.substring(1)
                        : filePath;

                return jte.render(templatePath, model, ctx);
            });

            config.staticFiles.add("/public", Location.CLASSPATH);

            config.routes.apiBuilder(() -> {

                path("account", List.of(AppRole.CUSTOMER), () -> {
                    get(ctx -> {
                        String userId = ctx.cookieStore().get("user-id");

                        UserAccount account = AuthController.getUser(userId)
                            .orElseThrow(UnauthorizedResponse::new);

                        ctx.render("/accountPage.jte", Map.of(
                            "title", "Community Bank - Account",
                            "account", account
                        ));
                    });
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