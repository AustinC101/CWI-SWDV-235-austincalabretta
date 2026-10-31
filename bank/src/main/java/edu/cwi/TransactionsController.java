package edu.cwi;

import java.util.LinkedHashMap;
import java.util.Map;

import io.javalin.apibuilder.CrudHandler;
import io.javalin.http.Context;
import static j2html.TagCreator.a;
import static j2html.TagCreator.body;
import static j2html.TagCreator.div;
import static j2html.TagCreator.h1;
import static j2html.TagCreator.head;
import static j2html.TagCreator.html;
import static j2html.TagCreator.p;
import static j2html.TagCreator.script;
import static j2html.TagCreator.title;

public class TransactionsController implements CrudHandler {

    private final Map<Integer, Transaction> transactions = new LinkedHashMap<>();
    private int nextId = 1;

    public static class Transaction {
        public int id;
        public String description;
        public String amount;

        Transaction(int id, String description, String amount) {
            this.id = id;
            this.description = description;
            this.amount = amount;
        }
    }

    private static final String SCRIPT = """
        async function updateTransaction(event, id) {
            event.preventDefault();
            const form = event.target;
            const response = await fetch('/api/transactions/' + id, {
                method: 'PATCH',
                body: new URLSearchParams(new FormData(form))
            });
            if (response.ok) {
                window.location.href = '/api/transactions';
            } else {
                alert('Could not update transaction.');
            }
            return false;
        }

        async function deleteTransaction(event, id) {
            event.preventDefault();
            const response = await fetch('/api/transactions/' + id, {
                method: 'DELETE'
            });
            if (response.ok) {
                window.location.href = '/api/transactions';
            } else {
                alert('Could not delete transaction.');
            }
            return false;
        }
        """;

    private String page(String heading, j2html.tags.DomContent content) {
        return html(
            head(title(heading)),
            body(
                h1(heading),
                content,
                script(SCRIPT)
            )
        ).render();
    }

    @Override
    public void getAll(Context ctx) {
        ctx.render("/transactionsPage.jte",
            Map.of("transactions", transactions.values()));
    }

    @Override
    public void getOne(Context ctx, String resourceId) {
        int id = Integer.parseInt(resourceId);
        Transaction transaction = transactions.get(id);

        if (transaction == null) {
            ctx.status(404).result("Transaction not found");
            return;
        }

        ctx.html(page("Transaction Details",
            div(
                p("ID: " + transaction.id),
                p("Description: " + transaction.description),
                p("Amount: $" + transaction.amount),
                a("/api/transactions").withText("Back to Transactions")
            )
        ));
    }

    @Override
    public void create(Context ctx) {
        String description = ctx.formParam("description");
        String amount = ctx.formParam("amount");

        if (description == null || description.isBlank()
                || amount == null || amount.isBlank()) {
            ctx.status(400).result("Description and amount are required");
            return;
        }

        int id = nextId++;
        transactions.put(id, new Transaction(id, description, amount));

        ctx.redirect("/api/transactions");
    }

    @Override
    public void update(Context ctx, String resourceId) {
        int id = Integer.parseInt(resourceId);
        Transaction transaction = transactions.get(id);

        if (transaction == null) {
            ctx.status(404).result("Transaction not found");
            return;
        }

        String description = ctx.formParam("description");
        String amount = ctx.formParam("amount");

        if (description == null || description.isBlank()
                || amount == null || amount.isBlank()) {
            ctx.status(400).result("Description and amount are required");
            return;
        }

        transaction.description = description;
        transaction.amount = amount;

        ctx.status(200).result("Transaction updated");
    }

    @Override
    public void delete(Context ctx, String resourceId) {
        int id = Integer.parseInt(resourceId);

        if (transactions.remove(id) == null) {
            ctx.status(404).result("Transaction not found");
            return;
        }

        ctx.status(200).result("Transaction deleted");
    }
}
