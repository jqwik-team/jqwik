
package net.jqwik;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import org.junit.gen5.commons.util.ReflectionUtils;

public class ComposedProperty implements Property {

	@Override
	public String name() {
        Optional<Method> candidate = getMethod();
        return candidate.map(method -> method.getName()).orElse("no method with return value boolean");
	}

    private Optional<Method> getMethod() {
        return Arrays.stream(getClass().getDeclaredMethods()).filter(
                method -> method.getReturnType().equals(boolean.class)).findFirst();
    }

    @Override
	public boolean evaluate() {
        return (boolean) getMethod().map(method -> ReflectionUtils.invokeMethod(method, this)).orElse(false);
	}
}
