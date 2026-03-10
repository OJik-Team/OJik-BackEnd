package ojik.ojikback.infrastructure.adapter.out.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import ojik.ojikback.domain.port.in.auth.AuthErrorCode;
import ojik.ojikback.domain.port.in.auth.AuthException;
import ojik.ojikback.domain.port.out.auth.SocialAuthClient;
import ojik.ojikback.domain.port.out.auth.model.SocialUserInfo;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

public abstract class AbstractOAuthSocialAuthClient<T> implements SocialAuthClient {

    private final RestClient restClient;

    protected AbstractOAuthSocialAuthClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    @Override
    public final SocialUserInfo fetchUserInfo(String authCode) {
        try {
            String accessToken = requestAccessToken(authCode);
            T userInfoResponse = requestUserInfo(accessToken);
            String providerUserId = extractProviderUserId(userInfoResponse);
            return new SocialUserInfo(provider(), providerUserId);
        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthException(AuthErrorCode.SOCIAL_AUTH_FAILED, providerName() + " 인증에 실패했습니다.", e);
        }
    }

    protected final String requestAccessToken(String authCode) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        addTokenRequestParameters(form, authCode);

        AccessTokenResponse tokenResponse = restClient.post()
                .uri(tokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(AccessTokenResponse.class);

        if (tokenResponse == null || !StringUtils.hasText(tokenResponse.accessToken())) {
            throw new AuthException(AuthErrorCode.SOCIAL_AUTH_FAILED, providerName() + " 토큰 응답이 올바르지 않습니다.");
        }
        return tokenResponse.accessToken();
    }

    protected final T requestUserInfo(String accessToken) {
        return restClient.get()
                .uri(userInfoUri())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(userInfoResponseType());
    }

    protected abstract String providerName();

    protected abstract String tokenUri();

    protected abstract String userInfoUri();

    protected abstract void addTokenRequestParameters(MultiValueMap<String, String> form, String authCode);

    protected abstract Class<T> userInfoResponseType();

    protected abstract String extractProviderUserId(T userInfoResponse);

    protected record AccessTokenResponse(
            @JsonProperty("access_token") String accessToken
    ) {
    }
}
