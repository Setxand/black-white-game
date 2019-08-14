package com.blackonwhite.repository;

import com.blackonwhite.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, String> {


	@Query(nativeQuery = true, value = "SELECT * FROM card WHERE card.card_type = ?1 " +
			"AND ((SELECT COUNT(*) from card_rooms WHERE rooms != ?2 LIMIT 1) > 0) ORDER BY RAND() LIMIT 1")
	Optional<Card> randCard(String type, String roomId);

	@Modifying
	@Query(nativeQuery = true, value = "DELETE FROM card_rooms WHERE rooms = ?1")
	void deleteRoom(String roomId);
}
