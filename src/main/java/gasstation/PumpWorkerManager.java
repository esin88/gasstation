package gasstation;

import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by esin on 17.04.2016.
 */
final class PumpWorkerManager {
    private static final int DEFAULT_PUMP_BY_TYPE_CAPACITY = 8;

    @NotNull
    private final PriorityBlockingQueue<PumpWorker> workers = new PriorityBlockingQueue<>(
            DEFAULT_PUMP_BY_TYPE_CAPACITY,
            new GasPumpWorkerComparator());

    @NotNull
    synchronized Future<?> scheduleBuyGas(double amountInLiters) throws NotEnoughGasException {
        final PumpWorker worker = getWorkerWithLargestAmount();
        if (worker == null || worker.getRemainingGas() < amountInLiters) {

            throw new NotEnoughGasException();
        }
        final Future<?> future = worker.scheduleBuyGas(amountInLiters);
        addWorker(worker);
        return future;
    }

    @Nullable
    PumpWorker getWorkerWithLargestAmount() {
        return workers.poll();
    }

    void addWorker(@NotNull PumpWorker worker) {
        workers.add(worker);
    }

    private static class GasPumpWorkerComparator implements Comparator<PumpWorker> {
        @Override
        public int compare(PumpWorker o1, PumpWorker o2) {
            return Double.compare(o2.getRemainingGas(), o1.getRemainingGas());
        }
    }
}
