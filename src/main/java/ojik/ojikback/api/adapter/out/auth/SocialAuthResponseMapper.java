package ojik.ojikback.api.adapter.out.auth;

import ojik.ojikback.api.adapter.out.auth.dto.SocialLoginResponseData;
import ojik.ojikback.api.adapter.out.auth.dto.SocialSignupResponseData;
import ojik.ojikback.api.adapter.out.common.ApiResponse;
import ojik.ojikback.domain.port.in.auth.result.SocialLoginResult;
import ojik.ojikback.domain.port.in.auth.result.SocialSignupResult;

public final class SocialAuthResponseMapper {

    private SocialAuthResponseMapper() {
    }

    public static ApiResponse<SocialLoginResponseData> toResponse(SocialLoginResult result) {
        return ApiResponse.success(SocialLoginResponseData.from(result));
    }

    public static ApiResponse<SocialSignupResponseData> toResponse(SocialSignupResult result) {
        return ApiResponse.success(SocialSignupResponseData.from(result));
    }
}
