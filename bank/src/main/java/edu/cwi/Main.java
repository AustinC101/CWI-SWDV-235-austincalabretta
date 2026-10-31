package edu.cwi;

import io.javalin.Javalin;
import static io.javalin.apibuilder.ApiBuilder.crud;
import static io.javalin.apibuilder.ApiBuilder.path;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinJte;

public class Main {
    public static void main(String[] args) {

        TransactionsController transactionsController =
                new TransactionsController();

        Javalin app = Javalin.create(config -> {
            JavalinJte jte = new JavalinJte(
                JavalinJte.Companion.directoryTemplateEngine("src/main/resources/jte")
            );

            config.fileRenderer((filePath, model, ctx) -> {
                String templatePath = filePath.startsWith("/")
                        ? filePath.substring(1)
                        : filePath;

                return jte.render(templatePath, model, ctx);
            });

            config.staticFiles.add("/public", Location.CLASSPATH);

            config.routes.apiBuilder(() -> {
                path("api/transactions", () -> {
                    crud("/{transaction-id}", transactionsController);
                });
            });

            config.routes.post("/account", ctx -> {
                ctx.redirect("/account.html");
            });

            config.routes.get("/logout", ctx -> {
                ctx.redirect("/");
            });
        });

        app.start(7070);
    }
}