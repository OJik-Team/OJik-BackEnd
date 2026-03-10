package ojik.ojikback.infrastructure.adapter.in.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "social.kakao")
public class SocialKakaoProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String tokenUri = "https://kauth.kakao.com/oauth/token";
    private String userInfoUri = "https://kapi.kakao.com/v2/user/me";

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getTokenUri() {
        return tokenUri;
    }

    public void setTokenUri(String tokenUri) {
        this.tokenUri = tokenUri;
    }

    public String getUserInfoUri() {
        return userInfoUri;
    }

    public void setUserInfoUri(String userInfoUri) {
        this.userInfoUri = userInfoUri;
    }
}
