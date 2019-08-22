package com.blackonwhite.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {

	public enum Role {
		USER,
		MODERATOR,
		ADMIN
	}

	@Id
	private Integer chatId;
	private String name;
	private Integer roomId;
	private UserStatus status;
	private int winRate;
	private String blackCardId;
	private String blackCardMetaInf;
	private String whiteCardMetaInf;
	private Locale locale = new Locale("en");

	@Enumerated(EnumType.STRING)
	private Role role;

	@OneToMany(cascade = CascadeType.REFRESH)
	private List<Card> cards = new ArrayList<>();
}
