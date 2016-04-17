package gasstation;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasStation;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.AtomicDouble;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by esin on 16.04.2016.
 */
public class GasStationImpl implements GasStation {
    @NotNull
    private final AtomicDouble[] gasPrices = new AtomicDouble[GasType.values().length];
    @SuppressWarnings("unchecked")
    @NotNull
    private final PumpWorkerManager[] workerManagers = new PumpWorkerManager[GasType.values().length];
    @NotNull
    private final List<GasPump> allPumps = new LinkedList<>();
    @NotNull
    private final AtomicDouble revenueCounter = new AtomicDouble(0);
    @NotNull
    private final AtomicInteger salesCounter = new AtomicInteger(0);
    @NotNull
    private final AtomicInteger cancellationsNoGasCounter = new AtomicInteger(0);
    @NotNull
    private final AtomicInteger cancellationsTooExpensiveCounter = new AtomicInteger(0);

    public GasStationImpl() {
        for (int i = 0; i < GasType.values().length; i++) {
            gasPrices[i] = new AtomicDouble(0d);
            workerManagers[i] = new PumpWorkerManager();
        }
    }

    @Override
    public void addGasPump(@Nullable GasPump pump) {
        if (pump == null) {
            throw new IllegalArgumentException("Gas pump is null");
        }
        workerManagers[pump.getGasType().ordinal()].addWorker(new PumpWorker(pump));
        allPumps.add(pump);
    }

    @Override
    @NotNull
    public List<GasPump> getGasPumps() {
        return allPumps;
    }

    @Override
    public double buyGas(@Nullable GasType type, double amountInLiters, double maxPricePerLiter) throws NotEnoughGasException, GasTooExpensiveException {
        if (type == null) {
            throw new IllegalArgumentException("Gas type is null");
        }
        final double price = getPrice(type);
        if (price > maxPricePerLiter) {
            cancellationsTooExpensiveCounter.getAndIncrement();
            throw new GasTooExpensiveException();
        }
        final PumpWorkerManager manager = workerManagers[type.ordinal()];
        try {
            final Future<?> future = manager.scheduleBuyGas(amountInLiters);
            future.get();
            final double totalPrice = price * amountInLiters;
            revenueCounter.add(totalPrice);
            salesCounter.getAndIncrement();
            return totalPrice;
        } catch (NotEnoughGasException e) {
            cancellationsNoGasCounter.getAndIncrement();
            throw e;
        } catch (InterruptedException | ExecutionException e) {
            System.err.println(e.getMessage());
            return 0;
        }
    }

    @Override
    public double getRevenue() {
        return revenueCounter.get();
    }

    @Override
    public int getNumberOfSales() {
        return salesCounter.get();
    }

    @Override
    public int getNumberOfCancellationsNoGas() {
        return cancellationsNoGasCounter.get();
    }

    @SuppressWarnings("InstanceMethodNamingConvention")
    @Override
    public int getNumberOfCancellationsTooExpensive() {
        return cancellationsTooExpensiveCounter.get();
    }

    @Override
    public double getPrice(@Nullable GasType type) {
        if (type == null) {
            throw new IllegalArgumentException("Gas type is null");
        }
        return gasPrices[type.ordinal()].get();
    }

    @Override
    public void setPrice(@Nullable GasType type, double price) {
        if (type == null) {
            throw new IllegalArgumentException("Gas type is null");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be > 0");
        }
        gasPrices[type.ordinal()].set(price);
    }
}
