package ojik.ojikback.infrastructure.adapter.in.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import ojik.ojikback.infrastructure.adapter.in.config.AuthJwtProperties;
import ojik.ojikback.infrastructure.adapter.out.auth.JwtTokenSupport;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtAccessTokenParser {
    private static final String INVALID_ACCESS_TOKEN = "INVALID_ACCESS_TOKEN";
    private static final String ACCESS_TOKEN_EXPIRED = "ACCESS_TOKEN_EXPIRED";

    private final SecretKey secretKey;

    public JwtAccessTokenParser(AuthJwtProperties properties) {
        this.secretKey = JwtTokenSupport.createSecretKey(properties);
    }

    public AuthenticatedMember parse(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            validateTokenType(claims.get(JwtTokenSupport.TOKEN_TYPE_CLAIM, String.class));

            String subject = claims.getSubject();
            String nickname = claims.get(JwtTokenSupport.NICKNAME_CLAIM, String.class);
            if (!StringUtils.hasText(subject) || !StringUtils.hasText(nickname)) {
                throw new AccessTokenAuthenticationException(INVALID_ACCESS_TOKEN, "access token 클레임이 올바르지 않습니다.");
            }

            try {
                return new AuthenticatedMember(Long.parseLong(subject), nickname);
            } catch (NumberFormatException e) {
                throw new AccessTokenAuthenticationException(INVALID_ACCESS_TOKEN, "access token subject가 올바르지 않습니다.");
            }
        } catch (ExpiredJwtException e) {
            throw new AccessTokenAuthenticationException(ACCESS_TOKEN_EXPIRED, "access token이 만료되었습니다.");
        } catch (JwtException | IllegalArgumentException e) {
            throw new AccessTokenAuthenticationException(INVALID_ACCESS_TOKEN, "access token 검증에 실패했습니다.");
        }
    }

    private void validateTokenType(String tokenType) {
        if (!JwtTokenSupport.ACCESS_TOKEN_TYPE.equals(tokenType)) {
            throw new AccessTokenAuthenticationException(INVALID_ACCESS_TOKEN, "access token만 허용됩니다.");
        }
    }
}
