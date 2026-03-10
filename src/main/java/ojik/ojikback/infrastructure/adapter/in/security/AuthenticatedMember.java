package ojik.ojikback.infrastructure.adapter.in.security;

public record AuthenticatedMember(
        Long memberId,
        String nickname
) {
}
