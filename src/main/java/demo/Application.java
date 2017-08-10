package demo;

import net.unicon.cas.client.configuration.EnableCasClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

/**
 * Application class.
 */
@SpringBootApplication
@EnableCasClient
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Value("${casLogoutUrl}")
  private String SECURITY_CAS_LOGOUTURL;

  @Bean
  public LogoutFilter logoutFilter() {
    return new LogoutFilter("https://localhost:10100/cas/logout",
        new SecurityContextLogoutHandler());
  }

}
