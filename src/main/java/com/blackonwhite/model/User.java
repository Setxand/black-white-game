package com.blackonwhite.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

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
	private UserStatus status;

	@OneToOne
	private Card blackCard;

	@Enumerated(EnumType.STRING)
	private Role role;

	@OneToMany
	private List<Card> cards = new ArrayList<>();
}
