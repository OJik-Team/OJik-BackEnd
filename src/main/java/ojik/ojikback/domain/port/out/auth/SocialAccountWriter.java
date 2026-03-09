package ojik.ojikback.domain.port.out.auth;

import ojik.ojikback.domain.entity.SocialAccount;

public interface SocialAccountWriter {
    SocialAccount save(SocialAccount socialAccount);
}
