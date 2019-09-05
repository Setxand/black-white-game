package com.blackonwhite.repository;

import com.blackonwhite.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, String> {


	@Query(nativeQuery = true, value = "SELECT * FROM card LEFT JOIN card_rooms ON card.id = card_rooms.card_id " +
			"WHERE card_type = ?1 AND (rooms != ?2 OR rooms IS NULL ) ORDER BY RAND() LIMIT 1")
	Optional<Card> randCard(String type, Integer roomId);

	@Modifying
	@Query(nativeQuery = true, value = "DELETE FROM card_rooms WHERE rooms = ?1")
	void deleteRoom(Integer roomId);
}

//@Query(nativeQuery = true, value = "SELECT * FROM card WHERE card.card_type = ?1 " +
//		"AND ((SELECT COUNT(*) from card_rooms WHERE rooms != ?2 LIMIT 1) > 0) ORDER BY RAND() LIMIT 1")
