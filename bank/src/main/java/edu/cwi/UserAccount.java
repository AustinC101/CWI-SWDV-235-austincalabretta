package edu.cwi;

import java.util.Map;

public record UserAccount(
    String id,
    String name,
    AppRole role,
    Double balance,
    Map<String, String> transactions
) {

    public String formatBalance() {
        return String.format("$%,.2f", balance);
    }
}