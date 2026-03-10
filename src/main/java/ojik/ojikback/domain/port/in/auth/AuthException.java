package ojik.ojikback.domain.port.in.auth;

public class AuthException extends RuntimeException {
    private final AuthErrorCode errorCode;

    public AuthException(AuthErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AuthException(AuthErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public AuthErrorCode getErrorCode() {
        return errorCode;
    }
}
