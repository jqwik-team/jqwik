package net.jqwik.api.domains;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Use {@code @Domain(MyDomainContext.class)} to use only
 * {@linkplain net.jqwik.api.providers.ArbitraryProvider arbitrary providers} and
 * {@linkplain net.jqwik.api.configurators.ArbitraryConfigurator arbitrary configurators}
 * defined in given context class
 * <p>
 * You can have many domains on the same element. Providers and configurators
 * from all domains will be considered.
 * <p>
 * The priority of ArbitraryProviders and ArbitraryConfigurators can be changed
 * using {@code priority}
 */
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(DomainList.class)
@API(status = MAINTAINED, since = "1.2.0")
public @interface Domain {

	int PRIORITY_NOT_SET = Integer.MIN_VALUE;

	Class<? extends DomainContext> value();

	int priority() default PRIORITY_NOT_SET;
}
