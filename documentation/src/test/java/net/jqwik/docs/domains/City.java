package net.jqwik.docs.domains;

public class City {

	private String name;

	public City(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
