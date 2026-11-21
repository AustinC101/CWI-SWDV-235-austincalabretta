@param String title
@param edu.cwi.models.User user
@param java.util.List<edu.cwi.models.Account> accounts

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

    @if (accounts.isEmpty())
        <p>You don't have any accounts.</p>
    @else
        <ul>
            @for (var account : accounts)
                <li>
                    <strong>${account.getAccountType()}</strong>
                    - Balance:
                    ${String.format("$%.2f", account.getBalance())}

                    <a href="/account/${account.getAccountId()}">
                        Detail
                    </a>
                </li>
            @endfor
        </ul>
    @endif

    <p>© 2026 Javalin Bank</p>
    <a href="/logout">Log Out</a>
</body>
</html>