package gasstation;

import net.bigpoint.assessment.gasstation.GasPump;
import org.jetbrains.annotations.NotNull;
import util.AtomicDouble;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by esin on 16.04.2016.
 */
class PumpWorker {
    @NotNull
    private final GasPump pump;
    @NotNull
    private final AtomicDouble remainingGas;
    @NotNull
    private final ExecutorService executor;


    PumpWorker(@NotNull GasPump pump) {
        this.pump = pump;
        this.remainingGas = new AtomicDouble(pump.getRemainingAmount());
        this.executor = Executors.newSingleThreadExecutor();
    }

    double getRemainingGas() {
        return remainingGas.get();
    }

    @NotNull
    Future<?> scheduleBuyGas(double amount) {
        if (remainingGas.get() < amount) {
            throw new IllegalArgumentException("Not enough gas remained");
        }
        remainingGas.subtract(amount);
        return executor.submit(() -> pump.pumpGas(amount));
    }
}
