package org.koreait.member;

import lombok.Getter;
import lombok.Builder;
import lombok.Setter;
import lombok.ToString;
import org.koreait.member.entities.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

@ToString
@Builder
@Getter
@Setter
public class MemberInfo implements UserDetails {

    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Member member;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() { // 계정 만료 됬는지, 안됬는지.
        return true;
    }

    // 비번이 만료됬는지 안됬는지, false면 CredentialsExpiredException 발생.
    @Override
    public boolean isCredentialsNonExpired() {
        // 지난 비번 변경 시점.
        LocalDateTime credentialChangedAt = member.getCredentialChangedAt();
        // null이거나 30일이 지났을 경우 비번 변경창으로 이동할 수 있게 만듦.
        return credentialChangedAt != null &&
                credentialChangedAt.isAfter(LocalDateTime.now().minusMonths(1L));
    }

    @Override
    public boolean isEnabled() { // 회원 탈퇴 여부 만약 탈퇴되었다면 이것도 DisabledException으로 감.
        return member.getDeletedAt() == null;
    }
}
