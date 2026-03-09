package ojik.ojikback.domain.port.out.auth;

import ojik.ojikback.domain.entity.Member;

public interface MemberWriter {
    Member save(Member member);
}
