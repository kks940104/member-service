package org.koreait.global.entities;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class DbConfig {

    @PersistenceContext
    private EntityManager em;

    @Lazy
    @Bean
    /*
      쿼리 DSL 사용하기 위함.
      EX) SELECT 컬럼명 FROM 테이블명 WHERE 조건....등등 이런거 사용하기 위함.
      DB쪽이니 DbConfig에 넣음.
     */
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(em);
    }
}
