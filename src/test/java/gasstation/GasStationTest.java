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

import java.util.Collection;

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
        station = new GasStationImpl(regularPrice);
        regularPump = new GasPump(GasType.REGULAR, pumpGasAmount);
        station.addGasPump(regularPump);
    }

    @Test
    public void testBuy() throws NotEnoughGasException, GasTooExpensiveException {
        final double totalPrice = station.buyGas(GasType.REGULAR, pumpGasAmount, regularPrice);

        assertEquals(pumpGasAmount * regularPrice, totalPrice);
    }

    @Test
    public void testGetRevenue() throws NotEnoughGasException, GasTooExpensiveException {
        final double price = station.buyGas(GasType.REGULAR, pumpGasAmount, regularPrice);
        assertEquals(price, station.getRevenue());
    }

    @Test
    public void testGetNumberOfSales() throws NotEnoughGasException, GasTooExpensiveException {
        Assert.assertEquals(0, station.getNumberOfSales());
        station.buyGas(GasType.REGULAR, pumpGasAmount, regularPrice);
        Assert.assertEquals(1, station.getNumberOfSales());
    }

    @Test
    public void testBuyFailZeroAmount() throws NotEnoughGasException, GasTooExpensiveException {
        expectedException.expectMessage("Amount must be > 0");
        expectedException.expect(IllegalArgumentException.class);
        station.buyGas(GasType.REGULAR, 0, regularPrice);
        expectedException = ExpectedException.none();
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

    @Test
    public void testSetPrice() {
        station.setPrice(GasType.DIESEL, regularPrice * 2);
        assertEquals(regularPrice * 2, station.getPrice(GasType.DIESEL));
    }

    @Test
    public void testSetPriceFailNullType() {
        expectedException.expectMessage("Gas type is null");
        expectedException.expect(IllegalArgumentException.class);
        station.setPrice(null, 0);
        expectedException = ExpectedException.none();
    }

    @Test
    public void testSetPriceFailZeroPrice() {
        expectedException.expectMessage("Price must be > 0");
        expectedException.expect(IllegalArgumentException.class);
        station.setPrice(GasType.SUPER, 0);
        expectedException = ExpectedException.none();
    }

    @Test
    public void testAddGetGasPumps() {
        Collection<GasPump> pumps = station.getGasPumps();
        assertEquals(1, pumps.size());

        station.addGasPump(new GasPump(GasType.REGULAR, pumpGasAmount));
        pumps = station.getGasPumps();
        assertEquals(2, pumps.size());

        for (GasPump pump : pumps) {
            Assert.assertEquals(GasType.REGULAR, pump.getGasType());
            assertEquals(pumpGasAmount, pump.getRemainingAmount());
        }
    }

    @Test
    public void testGetGasPumpsUnmodifiable() {
        Collection<GasPump> pumps = station.getGasPumps();
        pumps.add(new GasPump(GasType.DIESEL, pumpGasAmount));

        pumps = station.getGasPumps();
        final GasPump pump = pumps.iterator().next();
        assertEquals(1, pumps.size());
        Assert.assertEquals(GasType.REGULAR, pump.getGasType());
    }
}
