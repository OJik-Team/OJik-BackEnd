package ojik.ojikback.domain.port.in.auth.result;

public record MemberSummary(
        Long id,
        String nickname,
        Long favoriteTeamId
) {
}
