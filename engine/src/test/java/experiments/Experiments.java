package experiments;

import java.util.*;

import org.junit.jupiter.api.*;

import net.jqwik.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class Experiments {

	@Test
	public void whenPalindrome_thenAccept() {
		assertTrue(isPalindrome("noon"));
	}
	boolean isPalindrome(String inputString) {
		if (inputString.length() == 0) {
			return true;
		} else {
			char firstChar = inputString.charAt(0);
			char lastChar = inputString.charAt(inputString.length() - 1);
			String mid = inputString.substring(1, inputString.length() - 1);
			return (firstChar == lastChar) && isPalindrome(mid);
		}
	}

}
