package com.shoppingmall.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DatabaseConfig {

    @Bean
    @Primary
    @ConditionalOnProperty(name = "spring.profiles.active", havingValue = "local", matchIfMissing = true)
    public DataSource localDataSource(
            @Value("${spring.datasource.url:jdbc:postgresql://localhost:5432/shoppingmall}") String url,
            @Value("${spring.datasource.username:postgres}") String username,
            @Value("${spring.datasource.password:}") String password) {
        
        System.out.println("=== 로컬 DataSource 설정 ===");
        System.out.println("URL: " + url);
        
        return DataSourceBuilder
                .create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName("org.postgresql.Driver")
                .build();
    }

    @Bean
    @Primary
    @Profile("!local")
    public DataSource productionDataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");
        
        System.out.println("=== 프로덕션 DataSource 설정 ===");
        System.out.println("DATABASE_URL 환경 변수: " + (databaseUrl != null ? "존재" : "없음"));
        
        if (databaseUrl != null && databaseUrl.startsWith("postgres://")) {
            // Railway/Heroku 스타일 URL을 JDBC URL로 변환
            try {
                URI dbUri = new URI(databaseUrl);
                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                String jdbcUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
                
                System.out.println("변환된 JDBC URL: " + jdbcUrl);
                System.out.println("Username: " + username);
                
                return DataSourceBuilder
                        .create()
                        .url(jdbcUrl)
                        .username(username)
                        .password(password)
                        .driverClassName("org.postgresql.Driver")
                        .build();
            } catch (URISyntaxException e) {
                throw new RuntimeException("Invalid DATABASE_URL format: " + databaseUrl, e);
            }
        }
        
        // SPRING_DATASOURCE_URL 환경 변수 사용
        String springUrl = System.getenv("SPRING_DATASOURCE_URL");
        String springUsername = System.getenv("SPRING_DATASOURCE_USERNAME");
        String springPassword = System.getenv("SPRING_DATASOURCE_PASSWORD");
        
        if (springUrl != null) {
            System.out.println("SPRING_DATASOURCE_URL 사용: " + springUrl);
            
            return DataSourceBuilder
                    .create()
                    .url(springUrl)
                    .username(springUsername != null ? springUsername : "postgres")
                    .password(springPassword != null ? springPassword : "")
                    .driverClassName("org.postgresql.Driver")
                    .build();
        }
        
        throw new RuntimeException("데이터베이스 환경 변수가 설정되지 않았습니다. DATABASE_URL 또는 SPRING_DATASOURCE_URL을 설정하세요.");
    }
}

