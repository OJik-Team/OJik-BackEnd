package ojik.ojikback.domain.port.in.auth.result;

public record SocialLoginResult(
        boolean isNew,
        String signupToken,
        String accessToken,
        String refreshToken,
        MemberSummary member
) {
    public static SocialLoginResult existingMember(String accessToken, String refreshToken, MemberSummary member) {
        return new SocialLoginResult(false, null, accessToken, refreshToken, member);
    }

    public static SocialLoginResult newMember(String signupToken) {
        return new SocialLoginResult(true, signupToken, null, null, null);
    }
}
