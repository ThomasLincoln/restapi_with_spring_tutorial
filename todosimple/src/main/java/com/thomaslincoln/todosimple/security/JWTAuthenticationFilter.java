package com.thomaslincoln.todosimple.security;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomaslincoln.todosimple.exceptions.GlobalExceptionHandler;
import com.thomaslincoln.todosimple.models.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

  private AuthenticationManager authenticationManager;

  private JWTUtil jwtUtil;

  public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
    setAuthenticationFailureHandler(new GlobalExceptionHandler());
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{
    try {
      User userCredentials = new ObjectMapper().readValue(request.getInputStream(), User.class);
      UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
        userCredentials.getUsername(), userCredentials.getPassword(), new ArrayList<>());
      
      Authentication authentication = this.authenticationManager.authenticate(authToken);
      return authentication;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, 
  FilterChain filterChain, Authentication authentication) throws IOException, ServletException {
    UserSprintSecurity userSprintSecurity = (UserSprintSecurity) authentication.getPrincipal();
    String username = userSprintSecurity.getUsername();
    String token = jwtUtil.generateToken(username);
    response.addHeader("Authorizarion", "Bearer " + token);
    response.addHeader("acess-control-expose-headers", "Authorization");
  }
}
