package ojik.ojikback.infrastructure.adapter.out.persistence.member;

import java.util.Optional;
import ojik.ojikback.domain.entity.Member;
import ojik.ojikback.domain.port.out.member.ActiveMemberReader;
import ojik.ojikback.domain.port.out.member.MemberWithdrawalProcessor;
import ojik.ojikback.infrastructure.repository.SpringDataMemberRepository;
import ojik.ojikback.infrastructure.repository.SpringDataSocialAccountRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class MemberPersistenceAdapter implements ActiveMemberReader, MemberWithdrawalProcessor {
    private final SpringDataMemberRepository memberRepository;
    private final SpringDataSocialAccountRepository socialAccountRepository;
    private final MemberPersistenceMapper mapper;

    public MemberPersistenceAdapter(
            SpringDataMemberRepository memberRepository,
            SpringDataSocialAccountRepository socialAccountRepository,
            MemberPersistenceMapper mapper
    ) {
        this.memberRepository = memberRepository;
        this.socialAccountRepository = socialAccountRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Member> findActiveById(Long memberId) {
        return memberRepository.findByIdAndDeletedAtIsNull(memberId)
                .map(mapper::toDomainMember);
    }

    @Override
    @Transactional
    public void withdraw(Member member) {
        ojik.ojikback.infrastructure.repository.entity.Member entity = memberRepository.findByIdAndDeletedAtIsNull(member.getId())
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 활성 회원입니다: " + member.getId()));

        entity.setUpdatedAt(member.getUpdatedAt());
        entity.setDeletedAt(member.getDeletedAt());
        socialAccountRepository.deleteByMember_Id(member.getId());
    }
}
