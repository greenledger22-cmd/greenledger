package redswitch.greenledger.project.model;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Date;
import java.util.List;
import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {




        String header = request.getHeader("Authorization");
        String path = request.getServletPath();

        if (path.equals("/user/sendOtp") || path.equals("/factor/getFactor") ||
                path.equals("/factor/addFactor")||path.equals("/factor/updateFactor")||
                path.equals("/scope1Ingest/ingestEmission")||
                path.equals("/scope1Ingest/updateEmission")||
                path.equals("/scope1Ingest/getAllIngest") ||
                path.equals("/user/verifyOtp") || path.equals("/user/addUser")) {
            chain.doFilter(request, response);
            return;
        }

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token);

            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority("ROLE_" + role);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            List.of(authority)
                    );
            System.out.println(" in JwtFilter TOKEN: " + token);
        System.out.println(" JwtFilter EMAIL: " + email);
        System.out.println(" JwtFilter ROLE: " + role);

            System.out.println("JwtFilter NOW: " + new Date());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        System.out.println("JwtFilter HEADER: " + header);
//        System.out.println("TOKEN: " + token);
//        System.out.println("EMAIL: " + email);
//        System.out.println("ROLE: " + role);
        chain.doFilter(request, response);
    }
}
