package util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

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
        final AtomicLong value = new AtomicLong(0);
        final ExecutorService adderExecutor = Executors.newFixedThreadPool(10);
        final Runnable adder = () -> value.getAndAdd(10);
        final ExecutorService subtractorExecutor = Executors.newFixedThreadPool(10);
        final Runnable subtractor = () -> value.getAndAdd(-10);

        for (int i = 0; i < 1000; i++) {
            adderExecutor.execute(adder);
            subtractorExecutor.execute(subtractor);
        }
        adderExecutor.shutdown();
        subtractorExecutor.shutdown();
        adderExecutor.awaitTermination(10, TimeUnit.MINUTES);
        subtractorExecutor.awaitTermination(10, TimeUnit.MINUTES);
        assertEquals(0, value.get());
    }
}
