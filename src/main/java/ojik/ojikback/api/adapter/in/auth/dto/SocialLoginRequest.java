package ojik.ojikback.api.adapter.in.auth.dto;

public record SocialLoginRequest(
        String provider,
        String authCode
) {
}
