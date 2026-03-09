package ojik.ojikback.api.adapter.in.auth.dto;

public record SocialSignupRequest(
        String signupToken,
        String nickname,
        Long favoriteTeamId
) {
}
