package ojik.ojikback.api.adapter.in.auth;

import java.util.stream.Collectors;
import ojik.ojikback.api.adapter.out.common.ApiResponse;
import ojik.ojikback.domain.port.in.auth.AuthErrorCode;
import ojik.ojikback.domain.port.in.auth.AuthException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;

@RestControllerAdvice
public class AuthExceptionHandler {
    private static final String INVALID_REQUEST_CODE = "INVALID_REQUEST";

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthException(AuthException exception) {
        return ResponseEntity.status(resolveStatus(exception.getErrorCode()))
                .body(ApiResponse.failure(exception.getErrorCode().name(), exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest()
                .body(ApiResponse.failure(INVALID_REQUEST_CODE, message));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiResponse<Void>> handleRequestParseException(Exception exception) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.failure(INVALID_REQUEST_CODE, "요청 형식이 올바르지 않습니다."));
    }

    private HttpStatus resolveStatus(AuthErrorCode errorCode) {
        return switch (errorCode) {
            case INVALID_PROVIDER -> HttpStatus.BAD_REQUEST;
            case SOCIAL_AUTH_FAILED, INVALID_SIGNUP_TOKEN, SIGNUP_TOKEN_EXPIRED -> HttpStatus.UNAUTHORIZED;
            case TEAM_NOT_FOUND, MEMBER_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case DUPLICATE_NICKNAME, DUPLICATE_SOCIAL_ACCOUNT -> HttpStatus.CONFLICT;
        };
    }
}
