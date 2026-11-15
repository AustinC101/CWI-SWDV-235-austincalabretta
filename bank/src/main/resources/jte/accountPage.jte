@param String title
@param edu.cwi.models.User user
@param edu.cwi.models.Account account
@param java.util.List<edu.cwi.models.Transaction> transactions

@template.accountLayout(
    title = title,
    account = @`
        @template.userAccount(
            user.getFirstName() + " " + user.getLastName(),
            String.format("$%.2f", account.getBalance())
        )
    `,
    detail = @`
        @template.transactionsList(
            transactions
        )
    `
)
