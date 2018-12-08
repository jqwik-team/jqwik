package examples.docs.defaultprovider;

import java.math.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;

class MoneyProperties {

	@Property
	void moneyCanBeMultiplied(@ForAll Money money) {
		Money times2 = money.times(2);
		Assertions.assertThat(times2.getCurrency()).isEqualTo(money.getCurrency());
		Assertions.assertThat(times2.getAmount()).isEqualTo(money.getAmount().multiply(new BigDecimal(2)));
	}
}
