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
import ojik.ojikback.infrastructure.adapter.in.config.SocialNaverProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class NaverSocialAuthClientTest {

    @Test
    @DisplayName("네이버 authCode로 providerUserId를 조회한다")
    void fetchUserInfoSuccess() {
        // given
        RestClient.Builder restClientBuilder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restClientBuilder).build();
        SocialNaverProperties properties = naverProperties();
        NaverSocialAuthClient client = new NaverSocialAuthClient(restClientBuilder, properties);

        server.expect(requestTo("https://nid.naver.com/oauth2.0/token"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"access_token\":\"naver-access-token\"}", MediaType.APPLICATION_JSON));

        server.expect(requestTo("https://openapi.naver.com/v1/nid/me"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer naver-access-token"))
                .andRespond(withSuccess("""
                        {
                          "resultcode": "00",
                          "message": "success",
                          "response": {
                            "id": "naver-user-id"
                          }
                        }
                        """, MediaType.APPLICATION_JSON));

        // when
        SocialUserInfo userInfo = client.fetchUserInfo("auth-code");

        // then
        assertThat(userInfo.provider()).isEqualTo(SocialProvider.NAVER);
        assertThat(userInfo.providerUserId()).isEqualTo("naver-user-id");
        server.verify();
    }

    @Test
    @DisplayName("네이버 사용자 정보 응답에 id가 없으면 SOCIAL_AUTH_FAILED 예외를 던진다")
    void fetchUserInfoFailWhenIdMissing() {
        // given
        RestClient.Builder restClientBuilder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restClientBuilder).build();
        SocialNaverProperties properties = naverProperties();
        NaverSocialAuthClient client = new NaverSocialAuthClient(restClientBuilder, properties);

        server.expect(requestTo("https://nid.naver.com/oauth2.0/token"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"access_token\":\"naver-access-token\"}", MediaType.APPLICATION_JSON));

        server.expect(requestTo("https://openapi.naver.com/v1/nid/me"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "resultcode": "00",
                          "message": "success",
                          "response": {}
                        }
                        """, MediaType.APPLICATION_JSON));

        // when // then
        assertThatThrownBy(() -> client.fetchUserInfo("auth-code"))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.SOCIAL_AUTH_FAILED);
    }

    private SocialNaverProperties naverProperties() {
        SocialNaverProperties properties = new SocialNaverProperties();
        properties.setClientId("client-id");
        properties.setClientSecret("client-secret");
        properties.setRedirectUri("http://localhost:8080/oauth/naver/callback");
        properties.setState("ojik-local-naver-state");
        properties.setTokenUri("https://nid.naver.com/oauth2.0/token");
        properties.setUserInfoUri("https://openapi.naver.com/v1/nid/me");
        return properties;
    }
}
