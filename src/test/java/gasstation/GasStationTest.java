package gasstation;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasStation;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static util.GasStationTestHelper.assertEquals;

/**
 * Created by esin on 17.04.2016.
 */
@SuppressWarnings("FieldCanBeLocal")
public final class GasStationTest {
    @SuppressWarnings("NullableProblems")
    @NotNull
    private GasStation station;
    @SuppressWarnings("NullableProblems")
    @NotNull
    private GasPump regularPump;
    private final double pumpGasAmount = 10.1d;
    private final double regularPrice = 1.1d;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {
        station = new GasStationImpl();
        regularPump = new GasPump(GasType.REGULAR, pumpGasAmount);
        station.addGasPump(regularPump);
        station.setPrice(GasType.REGULAR, regularPrice);
    }

    @Test
    public void testBuy() throws NotEnoughGasException, GasTooExpensiveException {
        final double totalPrice = station.buyGas(GasType.REGULAR, pumpGasAmount, regularPrice);

        assertEquals(pumpGasAmount * regularPrice, totalPrice);
        assertEquals(totalPrice, station.getRevenue());
        Assert.assertEquals(1, station.getNumberOfSales());
    }

    @Test
    public void testBuyFailTooExpensive() throws NotEnoughGasException, GasTooExpensiveException {
        expectedException.expect(GasTooExpensiveException.class);
        station.buyGas(GasType.REGULAR, pumpGasAmount, regularPrice - 1d);
        expectedException = ExpectedException.none();
    }

    @Test
    public void testBuyFailNotEnough() throws NotEnoughGasException, GasTooExpensiveException {
        expectedException.expect(NotEnoughGasException.class);
        station.buyGas(GasType.REGULAR, pumpGasAmount + 1d, regularPrice);
        expectedException = ExpectedException.none();
    }

    @Test
    public void testBuyFailNoSuchPump() throws NotEnoughGasException, GasTooExpensiveException {
        expectedException.expect(NotEnoughGasException.class);
        station.buyGas(GasType.DIESEL, pumpGasAmount, regularPrice);
        expectedException = ExpectedException.none();
    }
}
