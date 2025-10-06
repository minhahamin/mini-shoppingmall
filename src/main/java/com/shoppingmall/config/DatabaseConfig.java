package com.shoppingmall.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DatabaseConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");
        
        if (databaseUrl != null && databaseUrl.startsWith("postgres://")) {
            // Railway/Heroku 스타일 URL을 JDBC URL로 변환
            try {
                URI dbUri = new URI(databaseUrl);
                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                String jdbcUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
                
                return DataSourceBuilder
                        .create()
                        .url(jdbcUrl)
                        .username(username)
                        .password(password)
                        .driverClassName("org.postgresql.Driver")
                        .build();
            } catch (URISyntaxException e) {
                throw new RuntimeException("Invalid DATABASE_URL format", e);
            }
        }
        
        // 환경 변수가 없으면 application.properties의 설정 사용
        String springUrl = System.getenv("SPRING_DATASOURCE_URL");
        String springUsername = System.getenv("SPRING_DATASOURCE_USERNAME");
        String springPassword = System.getenv("SPRING_DATASOURCE_PASSWORD");
        
        if (springUrl != null) {
            return DataSourceBuilder
                    .create()
                    .url(springUrl)
                    .username(springUsername != null ? springUsername : "postgres")
                    .password(springPassword != null ? springPassword : "")
                    .driverClassName("org.postgresql.Driver")
                    .build();
        }
        
        // 기본값: application.properties 또는 application-local.properties 사용
        return DataSourceBuilder
                .create()
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}

