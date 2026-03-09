package ojik.ojikback.domain.port.out.auth.model;

public record AuthTokens(
        String accessToken,
        String refreshToken
) {
}
