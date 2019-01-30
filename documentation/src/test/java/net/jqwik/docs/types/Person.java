package net.jqwik.docs.types;

public class Person {

	private String name;

	public static Person withFirstAndLast(String first, String last) {
		return new Person(first + " " + last);
	}

	public Person(String name) {
		if (name == null || name.trim().isEmpty())
			throw new IllegalArgumentException();
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
