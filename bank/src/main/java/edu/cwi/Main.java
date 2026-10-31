package edu.cwi;

import io.javalin.Javalin;
import static io.javalin.apibuilder.ApiBuilder.crud;
import static io.javalin.apibuilder.ApiBuilder.path;
import io.javalin.http.staticfiles.Location;

public class Main {
    public static void main(String[] args) {

        TransactionsController transactionsController =
                new TransactionsController();

        Javalin app = Javalin.create(config -> {
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