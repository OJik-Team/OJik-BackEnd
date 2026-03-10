package ojik.ojikback.api.adapter.in.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record SocialLoginRequest(
        @NotBlank(message = "provider는 필수입니다.")
        String provider,
        @NotBlank(message = "authCode는 필수입니다.")
        String authCode
) {
}
