package ojik.ojikback.domain.port.out.auth;

import ojik.ojikback.domain.entity.enums.SocialProvider;
import ojik.ojikback.domain.port.out.auth.model.SocialUserInfo;

public interface SocialAuthClient {
    SocialProvider provider();

    SocialUserInfo fetchUserInfo(String authCode);
}
