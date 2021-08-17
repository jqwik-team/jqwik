package net.jqwik.api;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * An interface that can be used to add information to the report of a failing property.
 * The footnotes will be shown for the original failing sample and the shrunk sample.
 *
 * <p>
 * To get access to a footnotes object, just add a parameter of type {@code Footnotes}
 * to the property method or any lifecycle method.
 * </p>
 */
@API(status = EXPERIMENTAL, since = "1.5.5")
public interface Footnotes {

	/**
	 * Add a {@code footnote} to be displayed in case of failure.
	 *
	 * @param footnote a String
	 */
	void addFootnote(String footnote);

}
