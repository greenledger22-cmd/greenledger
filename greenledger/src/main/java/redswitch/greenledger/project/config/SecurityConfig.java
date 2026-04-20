package redswitch.greenledger.project.config;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import redswitch.greenledger.project.model.JwtFilter;
import redswitch.greenledger.project.model.JwtUtil;
import redswitch.greenledger.project.service.TokenBlacklistService;

import java.util.List;

@EnableMethodSecurity

@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public JwtFilter jwtFilter(TokenBlacklistService tokenBlacklistService) {
        return new JwtFilter(jwtUtil, tokenBlacklistService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtFilter jwtFilter,
                                           JwtAuthenticationEntryPoint entryPoint,
                                           CustomAccessDeniedHandler accessDeniedHandler) throws Exception {

        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(entryPoint)   //  401 handler
                        .accessDeniedHandler(accessDeniedHandler) //  403 handler
                )


                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/user/login",
                                "/user/addUser",
                                "/user/sendOtp",
                                //"/user/getAllUser",
                                "/user/sendOtp",
                                "/user/verifyOtp"
//                                "/factor/getFactor",
//                                "/factor/addFactor",
//                                "/factor/updateFactor",
//                                "/scope1Ingest/ingestEmission",
//                                "/scope1Ingest/updateEmission",
//                                "/scope1Ingest/getAllIngest"

                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter,
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "https://greenledgeresg.com",
                "https://api.greenledgeresg.com",
                "http://localhost:3000"
        )); // frontend URL
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*")); // allows email, otp, auth
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
