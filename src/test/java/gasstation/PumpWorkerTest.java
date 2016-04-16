package gasstation;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static util.GasStationTestHelper.assertEquals;

/**
 * Created by esin on 17.04.2016.
 */
public final class PumpWorkerTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testRemainingFuel() throws ExecutionException, InterruptedException {
        final double initialGasAmount = 100d;
        final double amountToBuy = 10d;

        final PumpWorker worker = new PumpWorker(new GasPump(GasType.REGULAR, initialGasAmount));
        final Future<?> future = worker.scheduleBuyGas(amountToBuy);
        future.get();
        assertEquals(initialGasAmount - amountToBuy, worker.getRemainingGas());
    }

    @Test
    public void testguyGasFail() throws ExecutionException, InterruptedException {
        final double initialGasAmount = 100d;
        final PumpWorker worker = new PumpWorker(new GasPump(GasType.REGULAR, initialGasAmount));

        expectedException.expectMessage("Not enough gas remaiend");
        expectedException.expect(IllegalArgumentException.class);
        final Future<?> future = worker.scheduleBuyGas(initialGasAmount * 2);
        future.get();
        expectedException = ExpectedException.none();
    }
}
