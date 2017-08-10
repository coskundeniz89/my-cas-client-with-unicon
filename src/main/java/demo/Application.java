package demo;

import com.kakawait.spring.boot.security.cas.CasHttpSecurityConfigurer;
import com.kakawait.spring.boot.security.cas.CasSecurityConfigurerAdapter;
import com.kakawait.spring.boot.security.cas.CasSecurityProperties;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.Http401AuthenticationEntryPoint;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.security.cas.userdetails.AbstractCasAssertionUserDetailsService;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Application class.
 */
@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

//  @Bean
//  AbstractCasAssertionUserDetailsService rolesAuthenticationUserDetailsService() {
//    return new CasAssertionUserDetailsService();
//  }
//
//  private static class CasAssertionUserDetailsService
//      extends AbstractCasAssertionUserDetailsService {
//
//    private static final String NON_EXISTENT_PASSWORD_VALUE = "NO_PASSWORD";
//
//    protected UserDetails loadUserDetails(Assertion assertion) {
//      String username = assertion.getPrincipal().getName();
//      if (!StringUtils.hasText(username)) {
//        throw new UsernameNotFoundException("Unable to retrieve username from CAS assertion");
//      }
//      List<GrantedAuthority> authorities = new ArrayList<>();
//      authorities.add(new SimpleGrantedAuthority("TEST"));
//
//      return new User(username, NON_EXISTENT_PASSWORD_VALUE, authorities);
//    }
//  }

  @Bean
  FilterRegistrationBean forwardedHeaderFilter() {
    FilterRegistrationBean filterRegBean = new FilterRegistrationBean();
    filterRegBean.setFilter(new ForwardedHeaderFilter());
    filterRegBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return filterRegBean;
  }

  @Profile("!custom-logout")
  @Configuration
  static class LogoutConfiguration extends CasSecurityConfigurerAdapter {
    @Override
    public void configure(HttpSecurity http) throws Exception {
      // Allow GET method to /logout even if CSRF is enabled
      http.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
    }
  }

  @Configuration
  static class ApiSecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http.antMatcher("/api/**").authorizeRequests().anyRequest().authenticated();
      // Applying CAS security on current HttpSecurity (FilterChain)
      // I'm not using .apply() from HttpSecurity due to following issue
      // https://github.com/spring-projects/spring-security/issues/4422
      CasHttpSecurityConfigurer.cas().configure(http);
      http.exceptionHandling().authenticationEntryPoint(new Http401AuthenticationEntryPoint("CAS"));
    }
  }

  @Profile("custom-logout")
  @Configuration
  static class CustomLogoutConfiguration extends CasSecurityConfigurerAdapter {
    private final CasSecurityProperties casSecurityProperties;

    public CustomLogoutConfiguration(CasSecurityProperties casSecurityProperties) {
      this.casSecurityProperties = casSecurityProperties;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
      http.logout()
          .permitAll()
          .logoutSuccessUrl("/logout.html")
          .logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
      String logoutUrl = UriComponentsBuilder
          .fromUri(casSecurityProperties.getServer().getBaseUrl())
          .path(casSecurityProperties.getServer().getPaths().getLogout())
          .toUriString();
      LogoutFilter filter = new LogoutFilter(logoutUrl, new SecurityContextLogoutHandler());
      filter.setFilterProcessesUrl("/cas/logout");
      http.addFilterBefore(filter, LogoutFilter.class);
    }
  }

  @Profile("custom-logout")
  @Configuration
  static class WebMvcConfiguration extends WebMvcConfigurerAdapter {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
      registry.addViewController("/logout.html").setViewName("logout");
      registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }
  }

}
