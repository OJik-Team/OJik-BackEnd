package ojik.ojikback.infrastructure.adapter.in.security;

import org.springframework.security.core.AuthenticationException;

public class AccessTokenAuthenticationException extends AuthenticationException {
    private final String errorCode;

    public AccessTokenAuthenticationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
