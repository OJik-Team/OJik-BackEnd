package ojik.ojikback.api.adapter.in.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import ojik.ojikback.api.adapter.in.auth.dto.SocialLoginRequest;
import ojik.ojikback.api.adapter.in.auth.dto.SocialSignupRequest;
import ojik.ojikback.api.adapter.out.auth.dto.SocialLoginResponseData;
import ojik.ojikback.api.adapter.out.auth.dto.SocialSignupResponseData;
import ojik.ojikback.api.adapter.out.common.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Social Auth", description = "소셜 로그인/회원가입 API 계약")
@RequestMapping("/api/auth/social")
public interface SocialAuthApiSpec {

    @Operation(
            summary = "소셜 로그인",
            description = "기존 회원이면 JWT 발급, 신규 회원이면 signupToken 발급"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인/신규분기 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "INVALID_PROVIDER",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "SOCIAL_AUTH_FAILED",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/login")
    ApiResponse<SocialLoginResponseData> login(@RequestBody SocialLoginRequest request);

    @Operation(
            summary = "소셜 회원가입 완료",
            description = "signupToken 검증 후 회원/소셜계정을 생성하고 JWT를 발급"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "INVALID_SIGNUP_TOKEN or SIGNUP_TOKEN_EXPIRED",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "DUPLICATE_NICKNAME or DUPLICATE_SOCIAL_ACCOUNT",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/signup")
    ApiResponse<SocialSignupResponseData> signup(@RequestBody SocialSignupRequest request);
}
