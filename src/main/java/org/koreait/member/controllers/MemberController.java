package org.koreait.member.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.koreait.global.libs.Utils;
import lombok.RequiredArgsConstructor;
import org.koreait.global.rests.JSONData;
import org.koreait.member.MemberInfo;
import org.koreait.member.jwt.TokenService;
import org.koreait.member.validators.LoginValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.koreait.member.validators.JoinValidator;
import org.koreait.member.services.MemberUpdateService;
import org.koreait.global.exceptions.BadRequestException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name="Member", description = "회원 인증/인가 API")
@RestController
// api/vi 게이트웨이 설정 시 이게 붙음
@RequiredArgsConstructor
public class MemberController {

    @Value("${front.domain}")
    private String frontDomain;

    private final Utils utils;
    private final JoinValidator joinValidator;
    private final MemberUpdateService updateService;
    private final TokenService tokenService;
    private final LoginValidator loginValidator;

    @PostMapping("/join")
    @ResponseStatus(HttpStatus.CREATED)
    public void join(@RequestBody @Valid RequestJoin form, Errors errors) {
        joinValidator.validate(form, errors);

        if (errors.hasErrors()) {
            throw new BadRequestException(utils.getErrorMessages(errors));
        }

        updateService.process(form);
    }

    /**
     * 로그인 성공시 토큰 발급
     * @param form
     * @param errors
     */
    @PostMapping("/login")
    public JSONData login(@RequestBody @Valid RequestLogin form, Errors errors, HttpServletResponse response) {
        loginValidator.validate(form, errors);
        if (errors.hasErrors()) {
            throw new BadRequestException(utils.getErrorMessages(errors));
        }

        String email = form.getEmail();
        String token = tokenService.create(email);

        if (StringUtils.hasText(frontDomain)) {
            String[] domains = frontDomain.split(",");
            for (String domain : domains) {
/*                Cookie cookie = new Cookie("token", token);
                cookie.setPath("/");
                cookie.setDomain(domain);
                cookie.setSecure(true);
                cookie.setHttpOnly(true);*/
                response.setHeader("Set-Cookie", String.format("token=%s; Path=/; Domain=%s; Secure; HttpOnly; SameSite=None", token, domain)); // SameSite : None - 다른 서버에서도 쿠키 설정이 가능, Https는 필수.
                //response.addCookie(cookie);
            }
        }

        return new JSONData(token);
    }

    /**
     * 로그인 한 회원 정보 조회
     * @return
     */
    @GetMapping("/")
    public JSONData info(@AuthenticationPrincipal MemberInfo memberInfo) {

        return new JSONData(memberInfo.getMember());
    }
}














