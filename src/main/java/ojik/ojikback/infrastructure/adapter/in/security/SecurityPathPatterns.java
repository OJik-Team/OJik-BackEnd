package ojik.ojikback.infrastructure.adapter.in.security;

public final class SecurityPathPatterns {
    public static final String[] PUBLIC_ENDPOINTS = {
            "/api/auth/social/**",
            "/api/health",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/error"
    };

    private SecurityPathPatterns() {
    }
}
