package ojik.ojikback.api.adapter.out.auth.dto;

import ojik.ojikback.domain.port.in.auth.result.MemberSummary;

public record MemberPayload(
        Long id,
        String nickname,
        Long favoriteTeamId
) {
    public static MemberPayload from(MemberSummary summary) {
        if (summary == null) {
            return null;
        }
        return new MemberPayload(summary.id(), summary.nickname(), summary.favoriteTeamId());
    }
}
