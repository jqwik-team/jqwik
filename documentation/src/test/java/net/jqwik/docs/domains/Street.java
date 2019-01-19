package net.jqwik.docs.domains;

public class Street {

	private String name;

	public Street(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
