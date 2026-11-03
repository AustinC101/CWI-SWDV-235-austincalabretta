package edu.cwi;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public", Location.CLASSPATH);

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
