package us.redsols.todo.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import us.redsols.todo.component.CookieAuthFilter;

@Configuration
public class CorsConfig {

    private final JwtTokenProvider jwtTokenProvider;

    public CorsConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");

        config.addAllowedOrigin("http://localhost:4200");
        config.setAllowCredentials(true);

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public FilterRegistrationBean<CookieAuthFilter> cookieAuthFilterRegistration() {
        FilterRegistrationBean<CookieAuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CookieAuthFilter(jwtTokenProvider));
        registrationBean.addUrlPatterns("/todos/* /user/*");
        registrationBean.setOrder(1); // Order of execution (lower means higher priority)
        return registrationBean;
    }
}
