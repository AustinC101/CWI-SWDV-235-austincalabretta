@param java.util.Map<String, String> transactions

<div>
    <h2>Transaction History</h2>

    <ul>
        @for (var transaction : transactions.entrySet())
            <li>
                ${transaction.getKey()}: $${transaction.getValue()}
            </li>
        @endfor
    </ul>
</div>
