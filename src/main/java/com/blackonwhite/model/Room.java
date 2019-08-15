package com.blackonwhite.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Room {

	@Id
	private Integer hostId;
	private Integer blackCardPlayerId;

	@OneToMany(cascade = CascadeType.REFRESH)
	private List<User> userQueue = new LinkedList<>();

	@ElementCollection
	Map<String, String> pickedCards = new HashMap<>();
}
