@param java.util.Collection<edu.cwi.TransactionsController.Transaction> transactions

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Transactions</title>
</head>
<body>
    <h1>Transactions</h1>

    <div>
        <h2>Add a Transaction</h2>

        <form action="/api/transactions" method="post">
            <p>
                Description:
                <input type="text" name="description" required>
            </p>

            <p>
                Amount:
                <input type="number" name="amount" step="0.01" required>
            </p>

            <button type="submit">Add Transaction</button>
        </form>

        <h2>Transaction List</h2>

        @for (edu.cwi.TransactionsController.Transaction transaction : transactions)
            <div>
                <p>ID: ${transaction.id}</p>
                <p>Description: ${transaction.description}</p>
                <p>Amount: $${transaction.amount}</p>

                <form method="post"
                      onsubmit="return updateTransaction(event, ${transaction.id});">

                    <input type="text"
                           name="description"
                           value="${transaction.description}">

                    <input type="number"
                           name="amount"
                           value="${transaction.amount}"
                           step="0.01">

                    <button type="submit">Update</button>
                </form>

                <form method="post"
                      onsubmit="return deleteTransaction(event, ${transaction.id});">

                    <button type="submit">Delete</button>
                </form>

                <hr>
            </div>
        @endfor
    </div>

    <script>
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
    </script>
</body>
</html>