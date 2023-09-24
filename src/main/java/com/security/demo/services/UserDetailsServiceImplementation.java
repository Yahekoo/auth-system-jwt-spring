package com.security.demo.services;

import com.security.demo.entities.User;
import com.security.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImplementation implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       User user = userRepository.fidnUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
       return user;
    }

    public void createUser(UserDetails user) {
        userRepository.save((User) user);
    }
}
