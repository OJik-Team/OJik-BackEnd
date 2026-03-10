package ojik.ojikback.infrastructure.adapter.out.auth;

import io.jsonwebtoken.Jwts;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.crypto.SecretKey;
import ojik.ojikback.domain.entity.Member;
import ojik.ojikback.domain.port.out.auth.AuthTokenProvider;
import ojik.ojikback.domain.port.out.auth.model.AuthTokens;
import ojik.ojikback.infrastructure.adapter.in.config.AuthJwtProperties;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthTokenProvider implements AuthTokenProvider {
    private final SecretKey secretKey;
    private final AuthJwtProperties properties;

    public JwtAuthTokenProvider(AuthJwtProperties properties) {
        this.properties = properties;
        this.secretKey = JwtTokenSupport.createSecretKey(properties);
    }

    @Override
    public AuthTokens issue(Member member) {
        LocalDateTime now = LocalDateTime.now();
        return new AuthTokens(
                createToken(member, now, properties.getAccessExpiration(), JwtTokenSupport.ACCESS_TOKEN_TYPE),
                createToken(member, now, properties.getRefreshExpiration(), JwtTokenSupport.REFRESH_TOKEN_TYPE)
        );
    }

    private String createToken(Member member, LocalDateTime issuedAt, java.time.Duration expiration, String tokenType) {
        LocalDateTime expiresAt = issuedAt.plus(expiration);
        return Jwts.builder()
                .subject(String.valueOf(member.getId()))
                .claim(JwtTokenSupport.NICKNAME_CLAIM, member.getNickname())
                .claim(JwtTokenSupport.TOKEN_TYPE_CLAIM, tokenType)
                .issuedAt(toDate(issuedAt))
                .expiration(toDate(expiresAt))
                .signWith(secretKey)
                .compact();
    }

    private Date toDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
