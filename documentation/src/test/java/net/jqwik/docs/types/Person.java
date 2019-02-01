package net.jqwik.docs.types;

public class Person {

	public String name;
	public final int age;

	public static Person withFirstAndLast(String first, String last) {
		return new Person(first + " " + last, 0);
	}

	public Person(String name, int age) {
		if (name == null || name.trim().isEmpty())
			throw new IllegalArgumentException();
		if (age < 0 || age > 130)
			throw new IllegalArgumentException();

		this.name = name;
		this.age = age;
	}

	@Override
	public String toString() {
		return String.format("%s (%d)", name, age);
	}
}
