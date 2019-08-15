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
	private Integer roomId;
	private UserStatus status;
	private int vinRate;
	private String metaInf;

	@OneToOne(cascade = CascadeType.REFRESH)
	private Card blackCard;

	@Enumerated(EnumType.STRING)
	private Role role;

	@OneToMany(cascade = CascadeType.REFRESH)
	private List<Card> cards = new ArrayList<>();
}
