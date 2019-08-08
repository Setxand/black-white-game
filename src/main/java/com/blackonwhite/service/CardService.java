package com.blackonwhite.service;

import com.blackonwhite.model.Card;
import com.blackonwhite.repository.CardRepository;
import org.springframework.stereotype.Service;
import telegram.Message;

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
}
