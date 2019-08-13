package com.blackonwhite.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Card {

	public enum CardType {
		BLACK,
		WHITE
	}

	@Id
	@GenericGenerator(strategy = "uuid", name = "uuid")
	@GeneratedValue(generator = "uuid")
	private String id;
	private String name;

	@Enumerated(EnumType.STRING)
	private CardType cardType;

	@ElementCollection
	private List<Integer> rooms = new LinkedList<>();
}
