package com.team16.aims.repository;

import com.team16.aims.entity.Newspaper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewspaperRepo extends JpaRepository<Newspaper, Integer> {
}
