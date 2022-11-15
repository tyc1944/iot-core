package com.yunmo.iot.spring;

import com.yunmo.domain.common.Tenant;
import org.bouncycastle.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.x509.X509AuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import java.security.cert.X509Certificate;
import java.util.Optional;

/**
 * @author lh
 */

@Configuration
public class DeviceProvisionConfig {

    public static class DeviceProvisionFilter extends X509AuthenticationFilter {

        @Override
        protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
            return new Tenant(0L, Optional.empty()); // default
        }

        @Override
        protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
            X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");

            if (Arrays.isNullOrEmpty(certs)) {
                return null;
            }
            return certs[0].getSubjectDN().toString().substring(0,21);
        }
    }

    @Bean
  public SecurityFilterChain deviceProvisionSecurityFilterChain(HttpSecurity http)
      throws Exception {
    DeviceProvisionFilter filter = new DeviceProvisionFilter();
    filter.setAuthenticationManager(
        authentication -> {
          if (authentication.getPrincipal() == null) { // required if you configure http
            throw new BadCredentialsException("Access Denied.");
          }
          if (authentication.getCredentials() == null) {
            throw new BadCredentialsException("Access Denied.");
          }
          authentication.setAuthenticated(true);
          return authentication;
        });


    http.antMatcher("/api/devices/provision")
        .csrf()
        .disable()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .addFilter(filter)
        .authorizeRequests()
        .anyRequest()
        .authenticated();
    return http.build();
  }
}
