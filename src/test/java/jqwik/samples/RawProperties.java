package jqwik.samples;

import net.jqwik.Property;

class RawProperties {

    Property aSucceedingProperty() {
        return new Property() {
            @Override
            public String name() {
                return "a succeeding property";
            }

            @Override
            public boolean evaluate() {
                return true;
            }
        };
    }

    Property aFailingProperty() {
        return new Property() {
            @Override
            public String name() {
                return "a failing property";
            }

            @Override
            public boolean evaluate() {
                return false;
            }
        };
    }
}
