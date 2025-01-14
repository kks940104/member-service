package org.koreait.member.controllers;

import jakarta.validation.Valid;
import org.koreait.global.libs.Utils;
import lombok.RequiredArgsConstructor;
import org.koreait.global.rests.JSONData;
import org.koreait.member.jwt.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.koreait.member.validators.JoinValidator;
import org.koreait.member.services.MemberUpdateService;
import org.koreait.global.exceptions.BadRequestException;

@RestController
@RequestMapping("/member") // api/vi 게이트웨이 설정 시 이게 붙음
@RequiredArgsConstructor
public class MemberController {

    private final Utils utils;
    private final JoinValidator joinValidator;
    private final MemberUpdateService updateService;
    private final TokenService tokenService;

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
    public JSONData login(@RequestBody @Valid RequestLogin form, Errors errors) {

        if (errors.hasErrors()) {
            throw new BadRequestException(utils.getErrorMessages(errors));
        }

        String email = form.getEmail();
        String token = tokenService.create(email);

        return new JSONData(token);
    }
}














