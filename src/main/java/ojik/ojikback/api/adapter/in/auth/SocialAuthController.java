package ojik.ojikback.api.adapter.in.auth;

import ojik.ojikback.api.adapter.in.auth.dto.SocialLoginRequest;
import ojik.ojikback.api.adapter.in.auth.dto.SocialSignupRequest;
import ojik.ojikback.api.adapter.out.auth.SocialAuthResponseMapper;
import ojik.ojikback.api.adapter.out.auth.dto.SocialLoginResponseData;
import ojik.ojikback.api.adapter.out.auth.dto.SocialSignupResponseData;
import ojik.ojikback.api.adapter.out.common.ApiResponse;
import ojik.ojikback.domain.port.in.auth.SocialLoginUseCase;
import ojik.ojikback.domain.port.in.auth.SocialSignupUseCase;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SocialAuthController implements SocialAuthApiSpec {
    private final SocialLoginUseCase socialLoginUseCase;
    private final SocialSignupUseCase socialSignupUseCase;

    public SocialAuthController(SocialLoginUseCase socialLoginUseCase, SocialSignupUseCase socialSignupUseCase) {
        this.socialLoginUseCase = socialLoginUseCase;
        this.socialSignupUseCase = socialSignupUseCase;
    }

    @Override
    public ApiResponse<SocialLoginResponseData> login(SocialLoginRequest request) {
        return SocialAuthResponseMapper.toResponse(
                socialLoginUseCase.login(SocialAuthRequestMapper.toCommand(request))
        );
    }

    @Override
    public ApiResponse<SocialSignupResponseData> signup(SocialSignupRequest request) {
        return SocialAuthResponseMapper.toResponse(
                socialSignupUseCase.signup(SocialAuthRequestMapper.toCommand(request))
        );
    }
}
