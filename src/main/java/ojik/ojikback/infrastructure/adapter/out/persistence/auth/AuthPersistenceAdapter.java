package ojik.ojikback.infrastructure.adapter.out.persistence.auth;

import java.util.Optional;
import ojik.ojikback.domain.entity.Member;
import ojik.ojikback.domain.entity.SocialAccount;
import ojik.ojikback.domain.entity.Team;
import ojik.ojikback.domain.entity.enums.SocialProvider;
import ojik.ojikback.domain.port.out.auth.MemberNicknameChecker;
import ojik.ojikback.domain.port.out.auth.MemberWriter;
import ojik.ojikback.domain.port.out.auth.SocialAccountReader;
import ojik.ojikback.domain.port.out.auth.SocialAccountWriter;
import ojik.ojikback.domain.port.out.auth.TeamReader;
import ojik.ojikback.infrastructure.repository.SpringDataMemberRepository;
import ojik.ojikback.infrastructure.repository.SpringDataSocialAccountRepository;
import ojik.ojikback.infrastructure.repository.SpringDataTeamRepository;
import org.springframework.stereotype.Component;

@Component
public class AuthPersistenceAdapter implements
        SocialAccountReader,
        TeamReader,
        MemberNicknameChecker,
        MemberWriter,
        SocialAccountWriter {

    private final SpringDataMemberRepository memberRepository;
    private final SpringDataSocialAccountRepository socialAccountRepository;
    private final SpringDataTeamRepository teamRepository;
    private final AuthPersistenceMapper mapper;

    public AuthPersistenceAdapter(
            SpringDataMemberRepository memberRepository,
            SpringDataSocialAccountRepository socialAccountRepository,
            SpringDataTeamRepository teamRepository,
            AuthPersistenceMapper mapper
    ) {
        this.memberRepository = memberRepository;
        this.socialAccountRepository = socialAccountRepository;
        this.teamRepository = teamRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<SocialAccount> findByProviderAndProviderUserId(SocialProvider provider, String providerUserId) {
        return socialAccountRepository.findByProviderAndProviderUserId(
                ojik.ojikback.infrastructure.repository.entity.enums.SocialProvider.valueOf(provider.name()),
                providerUserId
        ).map(mapper::toDomainSocialAccount);
    }

    @Override
    public Optional<Team> findById(Long teamId) {
        return teamRepository.findById(teamId).map(mapper::toDomainTeam);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    @Override
    public Member save(Member member) {
        ojik.ojikback.infrastructure.repository.entity.Team favoriteTeam = teamRepository.getReferenceById(member.getFavoriteTeam().getId());
        return mapper.toDomainMember(memberRepository.save(mapper.toEntityMember(member, favoriteTeam)));
    }

    @Override
    public SocialAccount save(SocialAccount socialAccount) {
        ojik.ojikback.infrastructure.repository.entity.Member member = memberRepository.getReferenceById(socialAccount.getMember().getId());
        return mapper.toDomainSocialAccount(socialAccountRepository.save(mapper.toEntitySocialAccount(socialAccount, member)));
    }
}
