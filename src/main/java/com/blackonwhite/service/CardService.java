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
	public Card getRandomCard(Card.CardType type, Integer roomId) {
		return cardRepo.randCard(type.name(), roomId.toString())
				.orElseThrow(() -> new IllegalArgumentException("Card pack is empty"));
	}

	@Transactional
	public void deleteRoom(Integer roomId) {
		cardRepo.deleteRoom(roomId.toString());
	}

	public Card getCard(String cardId) {
		return cardRepo.findById(cardId).orElseThrow(() -> new IllegalArgumentException("Invalid Card ID"));
	}
}
