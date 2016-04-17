package gasstation;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasType;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static util.GasStationTestHelper.assertEquals;

/**
 * Created by esin on 17.04.2016.
 */
public final class PumpWorkerManagerTest {
    @SuppressWarnings("MagicNumber")
    @Test
    public void testWorkerSorting() throws Exception {
        final PumpWorkerManager manager = new PumpWorkerManager();
        manager.addWorker(new PumpWorker(new GasPump(GasType.REGULAR, 100d)));
        manager.addWorker(new PumpWorker(new GasPump(GasType.REGULAR, 200d)));
        manager.addWorker(new PumpWorker(new GasPump(GasType.REGULAR, 300d)));

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
}
