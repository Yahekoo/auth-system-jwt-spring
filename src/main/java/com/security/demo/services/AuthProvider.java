package com.security.demo.services;

import com.security.demo.entities.Attempts;
import com.security.demo.entities.User;
import com.security.demo.repositories.AttemptsRepository;
import com.security.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthProvider implements AuthenticationProvider {

    private static final int ATTEMPTS_LIMIT = 3 ;

    @Autowired
    private UserDetailsServiceImplementation userDetailsService ;
    @Autowired
    private PasswordEncoder passwordEncoder ;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AttemptsRepository attemptsRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        User user = (User) userDetailsService.loadUserByUsername(username);
        boolean is_password_match = false;
        if(!user.isAccountNonLocked()) {
            throw new LockedException("Account Locked Due To Many Attempts");
        } else {
            is_password_match = passwordEncoder.matches(authentication.getCredentials().toString(),user.getPassword());
        }
       if(is_password_match) {
          Optional<Attempts> opt_attempts = attemptsRepository.findAttemptsByUsername(username);
           if(opt_attempts.isPresent()) {
               Attempts attempts = opt_attempts.get();
               attempts.setAttempts(0);
               attemptsRepository.save(attempts);
           }
           return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),authentication.getCredentials());
       } else {
           processFailedAttempts(username,user);
           throw new AuthenticationServiceException("Authentication Failed");
       }
    }

    private void processFailedAttempts(String username, User user) throws AuthenticationException {

        Optional<Attempts> userAttempts = attemptsRepository.findAttemptsByUsername(username);
        if(userAttempts.isEmpty()) {
            Attempts attempts = new Attempts();
            attempts.setUsername(username);
            attempts.setAttempts(1);
            attemptsRepository.save(attempts);
        } else {
           Attempts attempts = userAttempts.get();
           attempts.setAttempts(attempts.getAttempts() + 1);
           attemptsRepository.save(attempts);
           if(attempts.getAttempts() + 1 > ATTEMPTS_LIMIT) {
               user.setAccountNonLocked(true);
               userRepository.save(user);
               throw new LockedException("Too Many Attempts");
           }
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
