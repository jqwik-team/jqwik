package net.jqwik.web.api;

class EmailTestingSupport {

	static boolean isIPAddress(String domain) {
		if (domain.charAt(0) == '[' && domain.charAt(domain.length() - 1) == ']') {
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

}
