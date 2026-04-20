package redswitch.greenledger.project.model;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import redswitch.greenledger.project.service.TokenBlacklistService;

import java.util.Date;
import java.util.List;
import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    private  TokenBlacklistService tokenBlacklistService;

    public JwtFilter(JwtUtil jwtUtil, TokenBlacklistService tokenBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {



        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String header = request.getHeader("Authorization");
        //String header = request.getHeader("Authorization");
        String path = request.getServletPath();

//        if (path.equals("/user/sendOtp") || path.equals("/factor/getFactor") ||
//                path.equals("/factor/addFactor")||path.equals("/factor/updateFactor")||
//                path.equals("/scope1Ingest/ingestEmission")||
//                path.equals("/scope1Ingest/updateEmission")||
//                path.equals("/scope1Ingest/getAllIngest") ||
//                path.equals("/user/verifyOtp") ||
//                path.equals("/user/addUser")||
//                path.equals("/user/getAllUser")
//
//        ) {
//            chain.doFilter(request, response);
//            return;
//        }


        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);
            if (tokenBlacklistService.isBlacklisted(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token is blacklisted. Please login again.");
                return;
            }
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
