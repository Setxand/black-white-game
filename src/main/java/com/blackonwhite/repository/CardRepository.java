package com.blackonwhite.repository;

import com.blackonwhite.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

public interface CardRepository extends JpaRepository<Card, String> {
}
