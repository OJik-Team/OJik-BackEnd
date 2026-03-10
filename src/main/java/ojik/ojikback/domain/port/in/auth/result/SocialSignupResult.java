package ojik.ojikback.domain.port.in.auth.result;

public record SocialSignupResult(
        String accessToken,
        String refreshToken,
        MemberSummary member
) {
}
