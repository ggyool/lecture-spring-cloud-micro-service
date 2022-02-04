package org.ggyool.firstservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/first-service")
public class WelcomeController {

    private final Environment environment;

    public WelcomeController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping(value = "/welcome")
    public String welcome() {
        return "Welcome to the First Service";
    }

    @GetMapping(value = "/message")
    public String message(@RequestHeader("first-request") String header) {
        log.info(header);
        return "Message to the First Service";
    }

    @GetMapping(value = "/check")
    public String check(HttpServletRequest httpServletRequest) {
        log.info("Server port={} with request", httpServletRequest.getServerPort());
        log.info("Server port={} with environment", environment.getProperty("local.server.port"));
        return "Check to the First Service " + httpServletRequest.getServerPort();
    }
}
