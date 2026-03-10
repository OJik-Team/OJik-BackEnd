package ojik.ojikback.infrastructure.adapter.out.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import ojik.ojikback.domain.entity.enums.SocialProvider;
import ojik.ojikback.domain.port.in.auth.AuthErrorCode;
import ojik.ojikback.domain.port.in.auth.AuthException;
import ojik.ojikback.infrastructure.adapter.in.config.SocialKakaoProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Component
public class KakaoSocialAuthClient extends AbstractOAuthSocialAuthClient<KakaoSocialAuthClient.UserInfoResponse> {
    private final SocialKakaoProperties properties;

    public KakaoSocialAuthClient(RestClient.Builder restClientBuilder, SocialKakaoProperties properties) {
        super(restClientBuilder);
        this.properties = properties;
    }

    @Override
    public SocialProvider provider() {
        return SocialProvider.KAKAO;
    }

    @Override
    protected String providerName() {
        return "카카오";
    }

    @Override
    protected String tokenUri() {
        return properties.getTokenUri();
    }

    @Override
    protected String userInfoUri() {
        return properties.getUserInfoUri();
    }

    @Override
    protected void addTokenRequestParameters(MultiValueMap<String, String> form, String authCode) {
        form.add("client_id", properties.getClientId());
        form.add("redirect_uri", properties.getRedirectUri());
        form.add("code", authCode);

        if (StringUtils.hasText(properties.getClientSecret())) {
            form.add("client_secret", properties.getClientSecret());
        }
    }

    @Override
    protected Class<UserInfoResponse> userInfoResponseType() {
        return UserInfoResponse.class;
    }

    @Override
    protected String extractProviderUserId(UserInfoResponse userInfoResponse) {
        if (userInfoResponse == null || userInfoResponse.id() == null) {
            throw new AuthException(AuthErrorCode.SOCIAL_AUTH_FAILED, "카카오 사용자 정보 응답이 올바르지 않습니다.");
        }
        return String.valueOf(userInfoResponse.id());
    }

    protected record UserInfoResponse(
            @JsonProperty("id") Long id
    ) {
    }
}
