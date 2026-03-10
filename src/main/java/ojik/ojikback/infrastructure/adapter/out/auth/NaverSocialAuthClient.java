package ojik.ojikback.infrastructure.adapter.out.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import ojik.ojikback.domain.entity.enums.SocialProvider;
import ojik.ojikback.domain.port.in.auth.AuthErrorCode;
import ojik.ojikback.domain.port.in.auth.AuthException;
import ojik.ojikback.infrastructure.adapter.in.config.SocialNaverProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Component
public class NaverSocialAuthClient extends AbstractOAuthSocialAuthClient<NaverSocialAuthClient.UserInfoResponse> {
    private final SocialNaverProperties properties;

    public NaverSocialAuthClient(RestClient.Builder restClientBuilder, SocialNaverProperties properties) {
        super(restClientBuilder);
        this.properties = properties;
    }

    @Override
    public SocialProvider provider() {
        return SocialProvider.NAVER;
    }

    @Override
    protected String providerName() {
        return "네이버";
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
        form.add("client_secret", properties.getClientSecret());
        form.add("redirect_uri", properties.getRedirectUri());
        form.add("code", authCode);

        if (StringUtils.hasText(properties.getState())) {
            form.add("state", properties.getState());
        }
    }

    @Override
    protected Class<UserInfoResponse> userInfoResponseType() {
        return UserInfoResponse.class;
    }

    @Override
    protected String extractProviderUserId(UserInfoResponse userInfoResponse) {
        if (userInfoResponse == null
                || userInfoResponse.response() == null
                || !StringUtils.hasText(userInfoResponse.response().id())) {
            throw new AuthException(AuthErrorCode.SOCIAL_AUTH_FAILED, "네이버 사용자 정보 응답이 올바르지 않습니다.");
        }
        return userInfoResponse.response().id();
    }

    protected record UserInfoResponse(
            @JsonProperty("resultcode") String resultCode,
            @JsonProperty("message") String message,
            @JsonProperty("response") UserInfoData response
    ) {
    }

    protected record UserInfoData(
            @JsonProperty("id") String id
    ) {
    }
}
