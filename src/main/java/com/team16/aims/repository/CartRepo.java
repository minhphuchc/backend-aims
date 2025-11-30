package com.team16.aims.repository;

import com.team16.aims.entity.Cart;
import com.team16.aims.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepo extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUser(User user);

    Optional<Cart> findBySessionId(String sessionId);
}
