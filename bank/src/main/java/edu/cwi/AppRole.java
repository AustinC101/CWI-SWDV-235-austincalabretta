package edu.cwi;

import io.javalin.security.RouteRole;

public enum AppRole implements RouteRole {
    NONE,
    CUSTOMER,
    ADMIN
}