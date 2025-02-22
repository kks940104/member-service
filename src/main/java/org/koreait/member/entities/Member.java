package org.koreait.member.entities;

import lombok.Data;
import java.util.List;
import lombok.ToString;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.koreait.global.entities.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
public class Member extends BaseEntity {
    @Id @GeneratedValue
    private Long seq; // 회원 번호

    @Column(length=65, nullable = false, unique = true)
    private String email; // 이메일

    @Column(length=65)
    private String password;

    @Column(length=40, nullable = false)
    private String name;

    private boolean requiredTerms1;

    private boolean requiredTerms2;

    private boolean requiredTerms3;

    @Column(length=50)
    private String optionalTerms; // 선택 약관

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "member")
    private List<Authorities> authorities;

    // 비밀번호 변경 일시
    private LocalDateTime credentialChangedAt;
}