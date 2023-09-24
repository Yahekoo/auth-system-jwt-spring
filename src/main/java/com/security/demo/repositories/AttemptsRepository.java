package com.security.demo.repositories;

import com.security.demo.entities.Attempts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttemptsRepository extends JpaRepository<Attempts,Integer> {

    Optional<Attempts> findAttemptsByUsername(String username);
}
