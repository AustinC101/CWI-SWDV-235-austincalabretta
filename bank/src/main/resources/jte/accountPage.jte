@param String title
@param edu.cwi.UserAccount account

@template.accountLayout(
    title = title,
    account = @`
        @template.userAccount(
            account.name(),
            account.formatBalance()
        )
    `,
    detail = @`
        @template.transactionsList(
            account.transactions()
        )
    `
)
