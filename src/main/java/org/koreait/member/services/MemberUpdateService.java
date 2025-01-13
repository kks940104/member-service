package org.koreait.member.services;

import org.modelmapper.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.koreait.member.entities.Member;
import org.springframework.stereotype.Service;
import org.koreait.member.constants.Authority;
import org.koreait.member.entities.Authorities;
import org.koreait.member.entities.QAuthorities;
import org.koreait.member.controllers.RequestJoin;
import org.springframework.context.annotation.Lazy;
import org.koreait.member.repositories.MemberRepository;
import org.koreait.member.repositories.AuthoritiesRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Lazy // 지연로딩 - 최초의 빈을 사용할 때 생성
@Service
@Transactional
@RequiredArgsConstructor
public class MemberUpdateService {

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final AuthoritiesRepository authoritiesRepository;


    /**
     * 커멘드 객체의 타입에 따라서 RequestJoin이면 회원가입 처리
     *                          RequestProfile이면 회원정보 수정 처리
     * @param form
     */
    public void process(RequestJoin form) {
        // 커멘드 객체 -> 엔티티 객체 데이터 옮기기
        Member member = modelMapper.map(form, Member.class);

        // 선택 약관 -> 약관 항목1 || 약관 항목2 || ...
        List<String> optionalTerms = form.getOptionalTerms();
        if (optionalTerms != null) {
            member.setOptionalTerms(String.join("||", optionalTerms));
        }

        // 비밀번호 해시화 - BCrypt
        String hash = passwordEncoder.encode(form.getPassword());
        member.setPassword(hash);
        member.setCredentialChangedAt(LocalDateTime.now());

        // 회원 권한
        Authorities auth = new Authorities();
        auth.setMember(member);
        auth.setAuthority(Authority.USER); // 회원 권한이 없는 경우 - 회원 가입시, 기본 권한 USER

        save(member, List.of(auth)); // 회원 저장 처리
    }

    /**
     * 회원정보 추가 또는 수정 처리
     *
     */
    private void save(Member member, List<Authorities> authorities) {
        memberRepository.saveAndFlush(member);

        // region 회원 권한 업데이트 처리

        if (authorities != null) {
            /**
             * 기존 권한을 삭제하고 다시 등록
             */
            QAuthorities qAuthorities = QAuthorities.authorities;
            List<Authorities> items = (List<Authorities>) authoritiesRepository.findAll(qAuthorities.member.eq(member));

            if (items != null) {
                authoritiesRepository.deleteAll(items);
                authoritiesRepository.flush();
            }

            authoritiesRepository.saveAllAndFlush(authorities);
            //authoritiesRepository.flush();
        }

        // endregion

    }
}

