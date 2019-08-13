package com.blackonwhite.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.LinkedList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Room {

	@Id
	private Integer hostId;

	@OneToMany(cascade = CascadeType.REFRESH)
	private List<User> userQueue = new LinkedList<>();
}
