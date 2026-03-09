package ojik.ojikback.domain.port.in.auth;

public enum AuthErrorCode {
    INVALID_PROVIDER,
    SOCIAL_AUTH_FAILED,
    INVALID_SIGNUP_TOKEN,
    SIGNUP_TOKEN_EXPIRED,
    TEAM_NOT_FOUND,
    DUPLICATE_NICKNAME,
    DUPLICATE_SOCIAL_ACCOUNT
}
