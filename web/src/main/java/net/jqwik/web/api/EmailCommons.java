package net.jqwik.web.api;

public class EmailCommons {

	// TODO: This is not understandable for me
	public static boolean validUseOfColonInIPv6Address(String ip) {
		if (!checkColonPlacement(ip)) {
			return false;
		}
		boolean first = true;
		boolean inCheck = false;
		for (int i = 0; i < ip.length() - 1; i++) {
			boolean ipContainsTwoColonsAtI = ip.charAt(i) == ':' && (ip.charAt(i + 1) == ':');
			if (ipContainsTwoColonsAtI && first) {
				first = false;
				inCheck = true;
			} else if (ipContainsTwoColonsAtI && !inCheck) {
				return false;
			} else if (!ipContainsTwoColonsAtI) {
				inCheck = false;
			}
		}
		return true;
	}

	private static boolean checkColonPlacement(String ip) {
		boolean ipContainsThreeColons = ip.contains(":::");
		boolean startsWithOnlyOneColon = ip.charAt(0) == ':' && ip.charAt(1) != ':';
		boolean endsWithOnlyOneColon = ip.charAt(ip.length() - 1) == ':' && ip.charAt(ip.length() - 2) != ':';
		return !ipContainsThreeColons && !startsWithOnlyOneColon && !endsWithOnlyOneColon;
	}

}
