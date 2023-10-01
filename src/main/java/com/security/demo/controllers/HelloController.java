package com.security.demo.controllers;

import com.security.demo.entities.User;
import com.security.demo.services.UserDetailsServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class HelloController {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserDetailsServiceImplementation userDetailsService;

    @GetMapping(value = "/")
    public String index() {
        return "index";
    }


    @GetMapping(value = "/login")
    public String login(HttpServletRequest req, HttpSession session) {

        session.setAttribute(
                "error", getErrorMessage(req, ")")
        );

        return "login";
    }

    @GetMapping(value = "/register")
    public String register() {
        return "register";
    }

    @PostMapping(value = "/register",
                consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
                produces = { MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
    )
    public void registerNewUser(@RequestParam Map<String, String> params) {
        User user = new User();
        user.setUsername(params.get("username"));
        user.setPassword(passwordEncoder.encode(params.get("password")));
        user.setAccountNonLocked(true);
        userDetailsService.createUser(user);
    }

    private String getErrorMessage(HttpServletRequest request, String key) {
        Exception exception = (Exception) request.getSession().getAttribute(key);
        String error = "";
        if (exception instanceof BadCredentialsException) {
            error = "Invalid username and password!";
        } else if (exception instanceof LockedException) {
            error = exception.getMessage();
        } else {
            error = "Invalid username and password!";
        }
        return error;
    }
}
