package net.jqwik.api.domains;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@API(status = MAINTAINED, since = "1.2.0")
public @interface DomainList {
	Domain[] value();
}
