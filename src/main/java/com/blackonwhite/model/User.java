package com.blackonwhite.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
	private String blackCardId;
	private String blackCardMetaInf;
	private String whiteCardMetaInf;
	
	@Enumerated(EnumType.STRING)
	private Role role;

	@OneToMany(cascade = CascadeType.REFRESH)
	private List<Card> cards = new ArrayList<>();
}
