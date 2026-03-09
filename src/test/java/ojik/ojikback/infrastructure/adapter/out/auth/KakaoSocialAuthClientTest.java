package ojik.ojikback.infrastructure.adapter.out.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import ojik.ojikback.domain.entity.enums.SocialProvider;
import ojik.ojikback.domain.port.in.auth.AuthErrorCode;
import ojik.ojikback.domain.port.in.auth.AuthException;
import ojik.ojikback.domain.port.out.auth.model.SocialUserInfo;
import ojik.ojikback.infrastructure.adapter.in.config.SocialKakaoProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class KakaoSocialAuthClientTest {

    @Test
    @DisplayName("카카오 authCode로 providerUserId를 조회한다")
    void fetchUserInfoSuccess() {
        // given
        RestClient.Builder restClientBuilder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restClientBuilder).build();
        SocialKakaoProperties properties = kakaoProperties();
        KakaoSocialAuthClient client = new KakaoSocialAuthClient(restClientBuilder, properties);

        server.expect(requestTo("https://kauth.kakao.com/oauth/token"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"access_token\":\"kakao-access-token\"}", MediaType.APPLICATION_JSON));

        server.expect(requestTo("https://kapi.kakao.com/v2/user/me"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer kakao-access-token"))
                .andRespond(withSuccess("{\"id\":123456}", MediaType.APPLICATION_JSON));

        // when
        SocialUserInfo userInfo = client.fetchUserInfo("auth-code");

        // then
        assertThat(userInfo.provider()).isEqualTo(SocialProvider.KAKAO);
        assertThat(userInfo.providerUserId()).isEqualTo("123456");
        server.verify();
    }

    @Test
    @DisplayName("카카오 사용자 정보 응답에 id가 없으면 SOCIAL_AUTH_FAILED 예외를 던진다")
    void fetchUserInfoFailWhenIdMissing() {
        // given
        RestClient.Builder restClientBuilder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restClientBuilder).build();
        SocialKakaoProperties properties = kakaoProperties();
        KakaoSocialAuthClient client = new KakaoSocialAuthClient(restClientBuilder, properties);

        server.expect(requestTo("https://kauth.kakao.com/oauth/token"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"access_token\":\"kakao-access-token\"}", MediaType.APPLICATION_JSON));

        server.expect(requestTo("https://kapi.kakao.com/v2/user/me"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        // when // then
        assertThatThrownBy(() -> client.fetchUserInfo("auth-code"))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.SOCIAL_AUTH_FAILED);
    }

    private SocialKakaoProperties kakaoProperties() {
        SocialKakaoProperties properties = new SocialKakaoProperties();
        properties.setClientId("client-id");
        properties.setClientSecret("client-secret");
        properties.setRedirectUri("http://localhost:8080/login/oauth2/code/kakao");
        properties.setTokenUri("https://kauth.kakao.com/oauth/token");
        properties.setUserInfoUri("https://kapi.kakao.com/v2/user/me");
        return properties;
    }
}
