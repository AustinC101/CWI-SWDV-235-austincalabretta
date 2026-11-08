package edu.cwi;

import java.util.Map;

public record UserAccount(
    String name,
    Double balance,
    Map<String, String> transactions
) {

    public String formatBalance() {
        return String.format("$%,.2f", balance);
    }
}
