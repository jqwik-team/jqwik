package net.jqwik.docs.domains;

public class Address {

	private final Street street;
	private final int number;
	private final City city;

	public Address(Street street, int number, City city) {
		this.street = street;
		this.number = number;
		this.city = city;
	}

	@Override
	public String toString() {
		return String.format("%s %s, %s", number, street, city);
	}
}
