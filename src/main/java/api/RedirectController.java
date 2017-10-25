package api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/alto-boot")
public class RedirectController {
    @RequestMapping("")
    public String toNewSession() {
        return "redirect:/alto-boot/newsession.html";
    }
}
