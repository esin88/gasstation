package util;

import net.bigpoint.assessment.gasstation.GasStation;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;

/**
 * Created by esin on 17.04.2016.
 */
public final class GasStationTestHelper {
    public static final double DELTA = 1e-8;

    public static void assertEquals(double expected, double value) {
        Assert.assertEquals(expected, value, DELTA);
    }

    public static void buyGasSafe(@NotNull GasStation station, @NotNull GasType type, double amount, double maxPrice) {
        try {
            buyGasUnsafe(station, type, amount, maxPrice);
        } catch (NotEnoughGasException | GasTooExpensiveException ignored) {
        }
    }

    public static void buyGasUnsafe(@NotNull GasStation station, @NotNull GasType type, double amount, double maxPrice) throws NotEnoughGasException, GasTooExpensiveException {
        station.buyGas(type, amount, maxPrice);
    }
}
