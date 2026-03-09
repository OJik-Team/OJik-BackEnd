package ojik.ojikback.domain.port.in.auth.result;

import ojik.ojikback.domain.entity.Member;
import ojik.ojikback.domain.entity.Team;

public record MemberSummary(
        Long id,
        String nickname,
        Long favoriteTeamId
) {
    public static MemberSummary from(Member member) {
        Team favoriteTeam = member.getFavoriteTeam();
        Long favoriteTeamId = favoriteTeam != null ? favoriteTeam.getId() : null;
        return new MemberSummary(member.getId(), member.getNickname(), favoriteTeamId);
    }
}
