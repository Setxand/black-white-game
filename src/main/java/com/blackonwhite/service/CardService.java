package com.blackonwhite.service;

import com.blackonwhite.model.Card;
import com.blackonwhite.repository.CardRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CardService {

	private final CardRepository cardRepo;

	public CardService(CardRepository cardRepo) {
		this.cardRepo = cardRepo;
	}

	@Transactional
	public void createCard(Card.CardType cardType, String cardName) {
		Card card = new Card();
		card.setName(cardName);
		card.setCardType(cardType);
		cardRepo.save(card);
	}

	@Transactional
	public Card getRandomCard(Integer roomId, Card.CardType type) {
		Card card = cardRepo.randCard(type.name(), roomId.toString())
				.orElseThrow(() -> new IllegalArgumentException("Card pack is empty"));

		if (card.getRooms().contains(roomId)) {
			return getRandomCard(roomId, type);

		} else {
			card.getRooms().add(roomId);
			return card;
		}
	}

	@Transactional
	public void deleteRoom(Integer roomId) {
		cardRepo.deleteRoom(roomId.toString());
	}
}
