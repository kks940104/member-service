package org.koreait.member.validators;

import lombok.RequiredArgsConstructor;
import org.koreait.global.validators.PasswordValidator;
import org.koreait.member.controllers.RequestJoin;
import org.koreait.member.repositories.MemberRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.time.Period;

@Lazy
@Component
@RequiredArgsConstructor
public class JoinValidator implements Validator, PasswordValidator {

    private final MemberRepository memberRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(RequestJoin.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        if (errors.hasErrors()) {
            return;
        }

        /**
         * 1. 이메일 중복 여부 체크
         * 2. 비밀번호 복잡성 - 알파벳 대소문자 각각 1개 이상, 숫자 1개 이상, 특수 문자 포함
         * 3. 비밀번호, 비밀번호 확인 일치 여부
         */

        RequestJoin form = (RequestJoin) target;

        String email = form.getEmail();
        String password = form.getPassword();
        String confirmPassword = form.getConfirmPassword();

        // region 1. 이메일 중복 여부 체크

        if(memberRepository.exists(email)) {
            errors.rejectValue("email", "Duplicated");
        }

        // endregion

        // region 2. 비밀번호 복잡성

        if (!alphaCheck(password, false) || !numberCheck(password) || !specialCharsCheck(password)) {
            errors.rejectValue("password", "Complexity");
        }

        // endregion

        // region 3. 비밀번호, 비밀번호 확인 일치 여부

        if (!password.equals(confirmPassword)) {
            errors.rejectValue("confirmPassword", "Mismatch");
        }

        // endregion
    }
}
