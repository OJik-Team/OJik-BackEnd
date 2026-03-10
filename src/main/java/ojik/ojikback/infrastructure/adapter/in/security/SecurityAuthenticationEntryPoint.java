package ojik.ojikback.infrastructure.adapter.in.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import ojik.ojikback.api.adapter.out.common.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class SecurityAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final String UNAUTHORIZED = "UNAUTHORIZED";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String errorCode = authException instanceof AccessTokenAuthenticationException accessTokenException
                ? accessTokenException.getErrorCode()
                : UNAUTHORIZED;
        String message = authException instanceof AccessTokenAuthenticationException
                ? authException.getMessage()
                : "인증이 필요합니다.";

        objectMapper.writeValue(
                response.getWriter(),
                ApiResponse.failure(errorCode, message)
        );
    }
}
