package com.smartjob.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartjob.common.Result;
import com.smartjob.utils.CurrentUserHolder;
import com.smartjob.utils.JwtUtil;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Value("${jwt.prefix}")
    private String prefix;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) 
            throws ServletException, IOException {
        
        try {
            String authHeader = request.getHeader("Authorization");
            
            if (StringUtils.hasText(authHeader) && authHeader.startsWith(prefix)) {
                String token = authHeader.substring(prefix.length());
                
                if (jwtUtil.validateToken(token)) {
                    Long userId = jwtUtil.getUserId(token);
                    String role = jwtUtil.getRole(token);
                    String username = jwtUtil.getUsername(token);
                    
                    CurrentUserHolder.setUserId(userId);
                    CurrentUserHolder.setRole(role);
                    CurrentUserHolder.setUsername(username);
                    
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                    userId,
                                    null,
                                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                            );
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(objectMapper.writeValueAsString(Result.unauthorized()));
                    return;
                }
            }
            
            filterChain.doFilter(request, response);
            
        } finally {
            CurrentUserHolder.clear();
        }
    }
}
