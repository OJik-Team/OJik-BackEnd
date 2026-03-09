package ojik.ojikback.api.adapter.in.auth;

import ojik.ojikback.api.adapter.out.common.ApiResponse;
import ojik.ojikback.domain.port.in.auth.AuthErrorCode;
import ojik.ojikback.domain.port.in.auth.AuthException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthException(AuthException exception) {
        return ResponseEntity.status(resolveStatus(exception.getErrorCode()))
                .body(ApiResponse.failure(exception.getErrorCode().name(), exception.getMessage()));
    }

    private HttpStatus resolveStatus(AuthErrorCode errorCode) {
        return switch (errorCode) {
            case INVALID_PROVIDER -> HttpStatus.BAD_REQUEST;
            case SOCIAL_AUTH_FAILED, INVALID_SIGNUP_TOKEN, SIGNUP_TOKEN_EXPIRED -> HttpStatus.UNAUTHORIZED;
            case TEAM_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case DUPLICATE_NICKNAME, DUPLICATE_SOCIAL_ACCOUNT -> HttpStatus.CONFLICT;
        };
    }
}
