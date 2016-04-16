package util;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.DoubleAdder;

/**
 * Created by esin on 16.04.2016.
 */
public class AtomicDouble {
    @NotNull
    private final DoubleAdder adder;

    public AtomicDouble(double initialValue) {
        this.adder = new DoubleAdder();
        adder.add(initialValue);
    }

    public double get() {
        return adder.sum();
    }

    public synchronized void set(double value) {
        adder.reset();
        adder.add(value);
    }

    public void add(double value) {
        adder.add(value);
    }

    public void subtract(double value) {
        adder.add(-value);
    }
}
