package ojik.ojikback.infrastructure.adapter.out.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import ojik.ojikback.domain.entity.enums.SocialProvider;
import ojik.ojikback.domain.port.in.auth.AuthErrorCode;
import ojik.ojikback.domain.port.in.auth.AuthException;
import ojik.ojikback.domain.port.out.auth.SocialAuthClient;
import ojik.ojikback.domain.port.out.auth.model.SocialUserInfo;
import ojik.ojikback.infrastructure.adapter.in.config.SocialKakaoProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Component
public class KakaoSocialAuthClient implements SocialAuthClient {

    private final RestClient restClient;
    private final SocialKakaoProperties properties;

    public KakaoSocialAuthClient(RestClient.Builder restClientBuilder, SocialKakaoProperties properties) {
        this.restClient = restClientBuilder.build();
        this.properties = properties;
    }

    @Override
    public SocialProvider provider() {
        return SocialProvider.KAKAO;
    }

    @Override
    public SocialUserInfo fetchUserInfo(String authCode) {
        try {
            String accessToken = requestAccessToken(authCode);
            String providerUserId = requestProviderUserId(accessToken);
            return new SocialUserInfo(SocialProvider.KAKAO, providerUserId);
        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthException(AuthErrorCode.SOCIAL_AUTH_FAILED, "카카오 인증에 실패했습니다.", e);
        }
    }

    private String requestAccessToken(String authCode) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("client_id", properties.getClientId());
        form.add("redirect_uri", properties.getRedirectUri());
        form.add("code", authCode);

        if (StringUtils.hasText(properties.getClientSecret())) {
            form.add("client_secret", properties.getClientSecret());
        }

        TokenResponse tokenResponse = restClient.post()
                .uri(properties.getTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(TokenResponse.class);

        if (tokenResponse == null || !StringUtils.hasText(tokenResponse.accessToken())) {
            throw new AuthException(AuthErrorCode.SOCIAL_AUTH_FAILED, "카카오 토큰 응답이 올바르지 않습니다.");
        }
        return tokenResponse.accessToken();
    }

    private String requestProviderUserId(String accessToken) {
        UserInfoResponse userInfoResponse = restClient.get()
                .uri(properties.getUserInfoUri())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(UserInfoResponse.class);

        if (userInfoResponse == null || userInfoResponse.id() == null) {
            throw new AuthException(AuthErrorCode.SOCIAL_AUTH_FAILED, "카카오 사용자 정보 응답이 올바르지 않습니다.");
        }
        return String.valueOf(userInfoResponse.id());
    }

    private record TokenResponse(
            @JsonProperty("access_token") String accessToken
    ) {
    }

    private record UserInfoResponse(
            @JsonProperty("id") Long id
    ) {
    }
}
