package ojik.ojikback.api.adapter.in.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SocialSignupRequest(
        @NotBlank(message = "signupToken은 필수입니다.")
        String signupToken,
        @NotBlank(message = "nickname은 필수입니다.")
        String nickname,
        @NotNull(message = "favoriteTeamId는 필수입니다.")
        Long favoriteTeamId
) {
}
