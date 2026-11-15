@param java.util.List<edu.cwi.models.Transaction> transactions

<div>
    <h2>Transaction History</h2>
    <ul>
        @for (var transaction : transactions)
            <li>
                <strong>${transaction.getTransactionType()}</strong>
                - ${String.format("$%.2f", transaction.getAmount())}

                @if (transaction.getDescription() != null)
                    (${transaction.getDescription()})
                @endif

                <br>
                <small>${transaction.getCreatedAt().toString()}</small>
            </li>
        @endfor
    </ul>
</div>
