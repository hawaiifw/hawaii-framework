package org.hawaiiframework.logging.logback;

public class ExceptionThrower {

    private void foo() {
        throw new IllegalArgumentException("bla bla");
    }

    private void bar() {
        try {
            foo();
        } catch (Throwable t) {
            throw new RuntimeException("BAR: ", t);
        }
    }

    public void xyz() {
        try {
            bar();
        } catch (Throwable t) {
            throw new RuntimeException("XYZ: caught error.", t);
        }
    }
}
