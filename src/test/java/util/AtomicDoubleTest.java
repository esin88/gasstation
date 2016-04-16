package util;

import org.junit.Test;

import static util.GasStationTestHelper.assertEquals;

/**
 * Created by esin on 17.04.2016.
 */
@SuppressWarnings("MagicNumber")
public final class AtomicDoubleTest {
    @Test
    public void testGet() {
        final AtomicDouble value = new AtomicDouble(10.5);
        assertEquals(10.5, value.get());
    }

    @Test
    public void testSet() {
        final AtomicDouble value = new AtomicDouble(10.5);
        value.set(100.5);
        assertEquals(100.5, value.get());
    }

    @Test
    public void testAdd() {
        final AtomicDouble value = new AtomicDouble(0.5);
        value.add(100.7);
        assertEquals(101.2, value.get());
    }

    @Test
    public void testSubtract() {
        final AtomicDouble value = new AtomicDouble(100.7);
        value.subtract(100.5);
        assertEquals(0.2, value.get());
    }
}
