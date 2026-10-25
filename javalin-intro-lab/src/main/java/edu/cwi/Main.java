
package edu.cwi;

import io.javalin.Javalin;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.routes.get("/hello/{name}", ctx -> {
                String name = ctx.pathParam("name");
                ctx.result("Hello, " + name);
            });

            config.routes.get("/hello/{name}/html", ctx -> {
                String name = ctx.pathParam("name");

                String html = """
                    <html>
                        <head>
                            <title>Javalin Intro</title>
                        </head>
                        <body>
                            <h1>Hello, %s</h1>
                        </body>
                    </html>
                    """.formatted(name);

                ctx.contentType("text/html");
                ctx.result(html);
            });
        });

        app.start(7070);
    }
}
