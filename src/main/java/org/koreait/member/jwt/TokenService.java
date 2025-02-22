package org.koreait.member.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import io.jsonwebtoken.security.UnsupportedKeyException;
import org.koreait.global.libs.Utils;
import org.koreait.member.MemberInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Lazy;
import org.koreait.member.services.MemberInfoService;
import org.springframework.security.core.Authentication;
import org.koreait.global.exceptions.UnAuthorizedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@EnableConfigurationProperties(JwtProperties.class)
public class TokenService {

    private final JwtProperties properties;
    private final MemberInfoService infoService;

    @Autowired
    private Utils utils;

    private Key key;

    public TokenService(JwtProperties properties, MemberInfoService infoService) {
        this.properties = properties;
        this.infoService = infoService;

        byte[] keyBytes = Decoders.BASE64.decode(properties.getSecret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * JWT 토큰 생성
     *
     * @param email
     * @return
     */
    public String create(String email) {
        MemberInfo memberInfo = (MemberInfo) infoService.loadUserByUsername(email);

        String authorities = memberInfo.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.joining("||"));

        int validTime = properties.getValidTime() * 1000;
        Date date = new Date((new Date()).getTime() + validTime); // 15분 뒤의 시간(만료 시간)

        return Jwts.builder()
                .setSubject(memberInfo.getEmail())
                .claim("authorities", authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(date)
                .compact();
    }

    /**
     * 토큰으로 인증 처리(로그인 처리)
     * 요청 헤더에 토큰이 실려서 옴
     *      Authorization: Bearer 토큰
     * @param token
     * @return
     */
    public Authentication authentication(String token) {

        // 토큰 유효성 검사
        validate(token);

        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .build().parseClaimsJws(token)
                .getPayload();

        String email = claims.getSubject();
        String authorities = (String) claims.get("authorities");
        List<SimpleGrantedAuthority> _authorities = Arrays.stream(authorities.split("\\|\\|")).map(SimpleGrantedAuthority::new).toList();

        MemberInfo memberInfo = (MemberInfo) infoService.loadUserByUsername(email);
        memberInfo.setAuthorities(_authorities);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(memberInfo, null, _authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication); // 로그인처리

        return authentication;
    }

    public Authentication authentication(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (!StringUtils.hasText(authHeader)) {
            return null; // 회원가십시 또는 로그인 시
        }
        String token = authHeader.substring(7);

        return authentication(token);
    }

    /**
     * 토큰 검증
     *
     * @param token
     */
    public void validate(String token) {
        String errorCode = null;
        Exception error = null;
        try {
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getPayload();
        } catch (SecurityException | MalformedJwtException e) {
            errorCode = "JWT.malformed";
            error = e;
        } catch (ExpiredJwtException e) { // 토큰 만료
            errorCode = "JWT.expired";
            error = e;
        } catch (UnsupportedJwtException e) {
            errorCode = "JWT.unsupported";
            error = e;
        } catch (Exception e) {
            errorCode = "JWT.error";
            error = e;
        }
        if (StringUtils.hasText(errorCode)) {
            throw new UnAuthorizedException(utils.getMessage(errorCode));
        }
        if (error != null) {
            error.printStackTrace();
        }
    }
}














