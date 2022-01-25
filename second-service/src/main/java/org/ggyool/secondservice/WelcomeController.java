package org.ggyool.secondservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class WelcomeController {

    @GetMapping(value = "/welcome")
    public String welcome() {
        return "Welcome to the Second Service";
    }
}
