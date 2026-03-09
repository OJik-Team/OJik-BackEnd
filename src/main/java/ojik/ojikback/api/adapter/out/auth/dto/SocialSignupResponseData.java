package ojik.ojikback.api.adapter.out.auth.dto;

import ojik.ojikback.domain.port.in.auth.result.SocialSignupResult;

public record SocialSignupResponseData(
        boolean isNew,
        String accessToken,
        String refreshToken,
        MemberPayload member
) {
    public static SocialSignupResponseData from(SocialSignupResult result) {
        return new SocialSignupResponseData(
                true,
                result.accessToken(),
                result.refreshToken(),
                MemberPayload.from(result.member())
        );
    }
}
