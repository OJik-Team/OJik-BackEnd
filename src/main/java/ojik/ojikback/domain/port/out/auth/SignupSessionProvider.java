package ojik.ojikback.domain.port.out.auth;

import ojik.ojikback.domain.port.out.auth.model.SignupSession;
import ojik.ojikback.domain.port.out.auth.model.SocialUserInfo;

public interface SignupSessionProvider {
    String create(SocialUserInfo userInfo);

    SignupSession get(String token);
}
