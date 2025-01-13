package org.koreait.global.configs;

import org.modelmapper.ModelMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestTemplate;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * 수동 Bean 관리.
 */
@Configuration
public class BeansConfig {

    @Lazy // 지연로딩. 사용하려고 할 때 가져오려고,
    @Bean // 기본요청 메서드. 금융오픈 API를 사용할 때 자세하게 배움.
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Lazy
    @Bean
    // 같은 Getter, Setter 패턴이 있는걸 자동으로 깊은복사해줌. Class.class 사용함.
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        return mapper;
    }

    /**
     * 자바 객체를 JSON 문자열로. JSON 문자열을 자바객체로 변환해줌
     *
     * @return
     */
    @Lazy
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule()); // java8 data & time api - java.time 패키지

        return om;
    }
}