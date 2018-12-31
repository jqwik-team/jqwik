package net.jqwik.api;

import java.util.*;
import java.util.logging.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class FacadeLoader {

	private static final Logger LOG = Logger.getLogger(FacadeLoader.class.getName());

	/**
	 * Load single implementation of an API facade. This should be registered as
	 * service provider in the jqwik engine module. There must be exactly
	 * one facade registered otherwise null will be returned and a severe error
	 * is logged.
	 *
	 * @param facadeClass
	 * @param <T>
	 * @return instance of facade
	 */
	public static <T> T load(Class<T> facadeClass) {
		try {
			T facade = null;
			for (T implementation : ServiceLoader.load(facadeClass)) {
				if (facade != null) {
					LOG.log(Level.SEVERE, createErrorMessage(facadeClass, "Several implementations registered."));
				}
				facade = implementation;
			}
			if (facade == null) {
				LOG.log(Level.SEVERE, createErrorMessage(facadeClass, "No implementation registered."));
			}
			return  facade;
		} catch (Exception e) {
			LOG.log(Level.SEVERE, createErrorMessage(facadeClass, ""), e);
			return null;
		}
	}

	private static <T> String createErrorMessage(Class<T> facadeClass, String addendum) {
		return "Cannot load implementation for " + facadeClass.getName() + ". " + addendum;
	}
}
