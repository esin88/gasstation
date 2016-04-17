package gasstation;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static util.GasStationTestHelper.assertEquals;

/**
 * Created by esin on 17.04.2016.
 */
@SuppressWarnings("MagicNumber")
public final class PumpWorkerManagerTest {
    private PumpWorkerManager manager;

    @Before
    public void setup() {
        manager = new PumpWorkerManager();
        manager.addWorker(new PumpWorker(new GasPump(GasType.REGULAR, 100d)));
        manager.addWorker(new PumpWorker(new GasPump(GasType.REGULAR, 200d)));
        manager.addWorker(new PumpWorker(new GasPump(GasType.REGULAR, 300d)));
    }

    @Test
    public void testWorkerSorting() {
        PumpWorker worker = manager.getWorkerWithLargestAmount();
        assertNotNull(worker);
        assertEquals(300d, worker.getRemainingGas());

        worker = manager.getWorkerWithLargestAmount();
        assertNotNull(worker);
        assertEquals(200d, worker.getRemainingGas());

        worker = manager.getWorkerWithLargestAmount();
        assertNotNull(worker);
        assertEquals(100d, worker.getRemainingGas());
    }

    @Test
    public void testManagerConstantWorkersAmount() throws NotEnoughGasException {
        manager.scheduleBuyGas(300d);
        manager.scheduleBuyGas(200d);
        manager.scheduleBuyGas(100d);

        for (int i = 0; i < 3; i++) {
            final PumpWorker worker = manager.getWorkerWithLargestAmount();
            assertNotNull(worker);
            assertEquals(0, worker.getRemainingGas());
        }
    }

    @Test
    public void testAddCuncurrentWorkers() throws InterruptedException {
        manager = new PumpWorkerManager();

        final ExecutorService executor = Executors.newFixedThreadPool(30);
        for (int i = 1; i <= 1000; i++) {
            final double amount = i * 10d;
            executor.submit(() -> manager.addWorker(new PumpWorker(new GasPump(GasType.REGULAR, amount))));
        }
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);

        for (int i = 1000; i > 0; i--) {
            final double amount = i * 10d;
            final PumpWorker worker = manager.getWorkerWithLargestAmount();
            assertNotNull(worker);
            assertEquals(amount, worker.getRemainingGas());
        }

        final PumpWorker worker = manager.getWorkerWithLargestAmount();
        assertNull(worker);
    }
}
