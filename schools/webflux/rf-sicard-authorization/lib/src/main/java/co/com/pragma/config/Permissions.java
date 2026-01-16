package co.com.pragma.config;

public final class Permissions {

    public static final String REGISTER_USER = "hasRole('ADMIN') || hasRole('ASSESSOR')";

    private Permissions() {}
}
