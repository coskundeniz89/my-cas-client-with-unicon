package demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;

@Controller
public class MainController {

  @Value("${casLogoutUrl}")
  private String casLogoutUrl;

  public String getCasLogoutUrl() {
    return casLogoutUrl;
  }

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String index() {
    return "index";
  }

  @RequestMapping(value = "/protected", method = RequestMethod.GET)
  public String protected1(HttpServletRequest request, Model model) {
    Principal principal = request.getUserPrincipal();
    model.addAttribute("principal", principal);
    return "protected";
  }
}
