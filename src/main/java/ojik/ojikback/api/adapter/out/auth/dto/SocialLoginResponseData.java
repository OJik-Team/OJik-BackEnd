package ojik.ojikback.api.adapter.out.auth.dto;

import ojik.ojikback.domain.port.in.auth.result.SocialLoginResult;

public record SocialLoginResponseData(
        boolean isNew,
        String signupToken,
        String accessToken,
        String refreshToken,
        MemberPayload member
) {
    public static SocialLoginResponseData from(SocialLoginResult result) {
        return new SocialLoginResponseData(
                result.isNew(),
                result.signupToken(),
                result.accessToken(),
                result.refreshToken(),
                MemberPayload.from(result.member())
        );
    }
}
