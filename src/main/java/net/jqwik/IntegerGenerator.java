package net.jqwik;

public class IntegerGenerator implements Generator<Integer> {
    @Override
    public Integer generate() {
        return 0;
    }

    public static IntegerGenerator any() {
        return new IntegerGenerator();
    }
}
