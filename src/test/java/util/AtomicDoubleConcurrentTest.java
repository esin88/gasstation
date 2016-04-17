package util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static util.GasStationTestHelper.assertEquals;

/**
 * Created by esin on 17.04.2016.
 */
@RunWith(Parameterized.class)
public class AtomicDoubleConcurrentTest {
    @Parameterized.Parameters
    public static List<Object[]> data() {
        return Arrays.asList(new Object[100][0]);
    }

    @Test
    public void testConcurrent() throws InterruptedException {
        final AtomicDouble value = new AtomicDouble(3.3d);
        final ExecutorService adderExecutor = Executors.newFixedThreadPool(10);
        final Runnable adder = () -> value.add(4.4d);
        final ExecutorService subtractorExecutor = Executors.newFixedThreadPool(10);
        final Runnable subtractor = () -> value.subtract(4.4d);

        for (int i = 0; i < 1000; i++) {
            adderExecutor.execute(adder);
            subtractorExecutor.execute(subtractor);
        }
        adderExecutor.shutdown();
        subtractorExecutor.shutdown();
        adderExecutor.awaitTermination(10, TimeUnit.MINUTES);
        subtractorExecutor.awaitTermination(10, TimeUnit.MINUTES);
        assertEquals(3.3d, value.get());
    }
}
