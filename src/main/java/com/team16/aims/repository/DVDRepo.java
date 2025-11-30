package com.team16.aims.repository;

import com.team16.aims.entity.DVD;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DVDRepo extends JpaRepository<DVD, Integer> {
}
