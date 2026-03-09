package ojik.ojikback.domain.port.in.auth.command;

public record SocialSignupCommand(
        String signupToken,
        String nickname,
        Long favoriteTeamId
) {
}
