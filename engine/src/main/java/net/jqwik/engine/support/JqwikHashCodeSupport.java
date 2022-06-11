package net.jqwik.engine.support;

public class JqwikHashCodeSupport {

	private JqwikHashCodeSupport() {
	}

	public static int hash(Object o) {
		return baseHash(o) + 31;
	}

	private static int baseHash(Object o) {
		if (o == null)
			return 0;
		return o.hashCode();
	}

	public static int hash(Object o1, Object o2) {
		return 31 * hash(o1) + baseHash(o2);
	}

	public static int hash(Object o1, Object o2, Object o3) {
		return 31 * hash(o1, o2) + baseHash(o3);
	}

	public static int hash(Object o1, Object o2, Object o3 , Object o4) {
		return 31 * hash(o1, o2, o3) + baseHash(o4);
	}

	public static int hash(Object o1, Object o2, Object o3 , Object o4, Object o5) {
		return 31 * hash(o1, o2, o3, o4) + baseHash(o5);
	}

	public static int hash(Object o1, Object o2, Object o3 , Object o4, Object o5, Object o6) {
		return 31 * hash(o1, o2, o3, o4, o5) + baseHash(o6);
	}

	public static int hash(Object o1, Object o2, Object o3 , Object o4, Object o5, Object o6, Object o7) {
		return 31 * hash(o1, o2, o3, o4, o5, o6) + baseHash(o7);
	}

	public static int hash(Object o1, Object o2, Object o3 , Object o4, Object o5, Object o6, Object o7, Object o8) {
		return 31 * hash(o1, o2, o3, o4, o5, o6, o7) + baseHash(o8);
	}

}
