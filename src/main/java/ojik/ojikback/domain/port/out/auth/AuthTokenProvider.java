package ojik.ojikback.domain.port.out.auth;

import ojik.ojikback.domain.entity.Member;
import ojik.ojikback.domain.port.out.auth.model.AuthTokens;

public interface AuthTokenProvider {
    AuthTokens issue(Member member);
}
