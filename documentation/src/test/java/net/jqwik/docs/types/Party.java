package net.jqwik.docs.types;

import java.util.*;

public class Party {

	final String name;
	final Set<Person> people;

	public Party(String name, Set<Person> people) {
		this.name = name;
		this.people = people;
	}

	@Override
	public String toString() {
		return "Party{" +
				   "name='" + name + '\'' +
				   ", people=" + people +
				   '}';
	}
}
