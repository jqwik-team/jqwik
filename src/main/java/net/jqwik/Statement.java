package net.jqwik;

@FunctionalInterface
public interface Statement {

    boolean evaluate(Parameters parameters) throws Throwable;
}
