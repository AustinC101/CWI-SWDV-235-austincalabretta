@param String title
@param edu.cwi.models.User user
@param edu.cwi.models.Account account
@param java.util.List<edu.cwi.models.Transaction> transactions

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
</head>
<body>
    <h1>Javalin Bank</h1>

    <h2>Welcome, ${user.getFirstName()} ${user.getLastName()}!</h2>

    <h2>User Accounts</h2>

    <p>Account Number: ${account.getAccountId()}</p>

    <p>Account Type: ${account.getAccountType()}</p>

    <p>Balance:
        ${String.format("$%.2f", account.getBalance())}
    </p>

    <h2>Transaction History</h2>

    @if (transactions.isEmpty())
        <p>No transactions found.</p>
    @else
        <ul>
            @for (var transaction : transactions)
                <li>
                    <strong>${transaction.getTransactionType()}</strong>
                    - ${String.format("$%.2f", transaction.getAmount())}

                    @if (transaction.getDescription() != null)
                        - ${transaction.getDescription()}
                    @endif

                    @if (transaction.getCreatedAt() != null)
                        <br>
                        <small>${transaction.getCreatedAt().toString()}</small>
                    @endif
                </li>
            @endfor
        </ul>
    @endif

    <p>
        <a href="/accounts">Back to Accounts</a>
    </p>

    <p>© 2026 Javalin Bank</p>

    <p>
        <a href="/logout">Log Out</a>
    </p>
</body>
</html>