package net.jqwik.web.api;

import inet.ipaddr.*;

class WebTestingSupport {

	public static final String ALLOWED_CHARS_DOMAIN = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-.";
	public static final String ALLOWED_CHARS_LOCALPART_UNQUOTED = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.!#$%&'*+-/=?^_`{|}~";
	public static final String ALLOWED_CHARS_LOCALPART_QUOTED = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.!#$%&'*+-/=?^_`{|}~\"(),:;<>@[\\] ";
	public static final String ALLOWED_NOT_NUMERIC_CHARS_TLD = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-";

	static boolean isIPAddress(String host) {
		if (host.charAt(0) == '[' && host.charAt(host.length() - 1) == ']') {
			return true;
		}
		return false;
	}

	static boolean isQuoted(String localPart) {
		if (localPart.length() >= 3 && localPart.charAt(0) == '"' && localPart.charAt(localPart.length() - 1) == '"') {
			return true;
		}
		return false;
	}

	static String getLocalPartOfEmail(String email) {
		int index = email.lastIndexOf('@');
		if (index == -1) {
			index = 0;
		}
		return email.substring(0, index);
	}

	static String getEmailHost(String email) {
		int index = email.lastIndexOf('@');
		return email.substring(index + 1);
	}

	static String extractIPAddress(String host) {
		return host.substring(1, host.length() - 1);
	}

	static boolean isValidWebDomain(String domain) {
		if (!domain.contains(".")) {
			return false;
		}
		String[] domainParts = domain.split("\\.");
		for (String domainPart : domainParts) {
			if (domainPart.length() > 63) {
				return false;
			}
		}
		String tld = domainParts[domainParts.length - 1];
		if (!containsAtLeastOneOf(ALLOWED_NOT_NUMERIC_CHARS_TLD, tld)) {
			return false;
		}
		return true;
	}

	static boolean containsAtLeastOneOf(String string, String chars) {
		for (char c : chars.toCharArray()) {
			if (string.contains(String.valueOf(c))) {
				return true;
			}
		}
		return false;
	}

	static boolean isValidIPAddress(String address) {
		if (address.contains(":")) {
			return isValidIPv6Address(address);
		} else {
			return isValidIPv4Address(address);
		}
	}

	static boolean isValidIPv6Address(String address) {
		return new IPAddressString(address).isIPv6();
	}

	static boolean isValidIPv4Address(String address) {
		return new IPAddressString(address).isIPv4();
	}

	static boolean isIn(int c, String string) {
		return string.contains(String.valueOf((char) c));
	}
}
