package net.jqwik.docs.arbitrarydecorator;

public class ComplexNumber {

	private final double rational;
	private final double imaginary;

	public ComplexNumber(double rational, double imaginary) {
		this.rational = rational;
		this.imaginary = imaginary;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();
		if (rational != 0) {
			sb.append(rational);
		}
		if (imaginary != 0) {
			if (rational != 0) {
				String plusMinus = imaginary > 0.0 ? " + " : " - ";
				sb.append(plusMinus);
			}
			sb.append(Math.abs(imaginary));
			sb.append("i");
		}
		return sb.toString();
	}
}
