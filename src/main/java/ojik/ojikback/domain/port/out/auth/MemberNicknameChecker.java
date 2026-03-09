package ojik.ojikback.domain.port.out.auth;

public interface MemberNicknameChecker {
    boolean existsByNickname(String nickname);
}
