package ojik.ojikback.infrastructure.adapter.in.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "social.naver")
public class SocialNaverProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String state;
    private String tokenUri = "https://nid.naver.com/oauth2.0/token";
    private String userInfoUri = "https://openapi.naver.com/v1/nid/me";

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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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
