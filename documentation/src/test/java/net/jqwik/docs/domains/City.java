package net.jqwik.docs.domains;

public class City {

	private String name;
	private State state;
	private String zipCode;

	public City(String name, State state, String zipCode) {
		this.name = name;
		this.state = state;
		this.zipCode = zipCode;
	}

	@Override
	public String toString() {
		return String.format("%s, %s %s", name, state.name(), zipCode);
	}
}
