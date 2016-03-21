package net.jqwik;

public class IntegerGenerator implements Generator<Integer> {
    @Override
    public Integer generate() {
        return 0;
    }

    @Override
    public boolean canServeType(Class<?> type) {
        return type == Integer.class || type == int.class;
    }

    public static IntegerGenerator any() {
        return new IntegerGenerator();
    }
}
