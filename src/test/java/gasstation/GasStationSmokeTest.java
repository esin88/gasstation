package gasstation;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasStation;
import net.bigpoint.assessment.gasstation.GasType;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static util.GasStationTestHelper.DELTA;
import static util.GasStationTestHelper.buyGasSafe;

/**
 * Created by esin on 17.04.2016.
 */
public final class GasStationSmokeTest {
    @SuppressWarnings({"MagicNumber", "TooBroadScope"})
    @Test
    public void smokeTest() throws InterruptedException {
        final GasStation station = new GasStationImpl();

        final int pumpGasStartAmount = 100;
        final int pumpsOfEachType = 3;
        final int buyAmount = 10;
        final int buyIterations = pumpGasStartAmount * pumpsOfEachType / buyAmount;

        for (int i = 0; i < pumpsOfEachType; i++) {
            station.addGasPump(new GasPump(GasType.REGULAR, pumpGasStartAmount));
            station.addGasPump(new GasPump(GasType.DIESEL, pumpGasStartAmount));
            station.addGasPump(new GasPump(GasType.SUPER, pumpGasStartAmount));
        }

        final double regularPrice = 1.1d;
        final double dieselPrice = 2.2d;
        final double superPrice = 3.3d;
        station.setPrice(GasType.REGULAR, regularPrice);
        station.setPrice(GasType.DIESEL, dieselPrice);
        station.setPrice(GasType.SUPER, superPrice);

        final ExecutorService buyerExecutor = Executors.newFixedThreadPool(30);

        final int numberOfTooExpensive = 1;
        for (int i = 0; i < numberOfTooExpensive; i++) {
            buyerExecutor.submit(() -> buyGasSafe(station, GasType.REGULAR, buyAmount, regularPrice - 0.1d));
            buyerExecutor.submit(() -> buyGasSafe(station, GasType.DIESEL, buyAmount, dieselPrice - 0.1d));
            buyerExecutor.submit(() -> buyGasSafe(station, GasType.SUPER, buyAmount, superPrice - 0.1d));
        }
        final int numberOfCancellation = 1;
        for (int i = 0; i < buyIterations + numberOfCancellation; i++) {
            buyerExecutor.submit(() -> buyGasSafe(station, GasType.REGULAR, buyAmount, regularPrice));
            buyerExecutor.submit(() -> buyGasSafe(station, GasType.DIESEL, buyAmount, dieselPrice));
            buyerExecutor.submit(() -> buyGasSafe(station, GasType.SUPER, buyAmount, superPrice));
        }
        buyerExecutor.shutdown();
        buyerExecutor.awaitTermination(10, TimeUnit.MINUTES);

        assertEquals(buyIterations * 3, station.getNumberOfSales());
        assertEquals(buyAmount * buyIterations * (regularPrice + dieselPrice + superPrice), station.getRevenue(), DELTA);
        assertEquals(numberOfCancellation * 3, station.getNumberOfCancellationsNoGas());
        assertEquals(numberOfTooExpensive * 3, station.getNumberOfCancellationsTooExpensive());
    }
}
