package net.jqwik;

import org.junit.gen5.commons.util.ExceptionUtils;

public class JqwikPropertyBuilder {

    private final String name;

    public JqwikPropertyBuilder(String name) {
        this.name = name;
    }

    public static JqwikPropertyBuilder property(String name) {
        return new JqwikPropertyBuilder(name);
    }

    public Property state(Statement statement) {
        return new Property() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public boolean evaluate() {
                try {
                    return statement.evaluate(new Parameters());
                } catch (Throwable throwable) {
                    ExceptionUtils.throwAsUncheckedException(throwable);
                    return false; //we never get there
                }
            }
        };
    }

    public JqwikPropertyBuilder forAll(String parameterName, Class<?> parameterType) {
        return this;
    }

    public JqwikPropertyBuilder forAll(String parameterName, Generator<?> parameterGenerator) {
        return this;
    }
}
