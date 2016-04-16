package net.bigpoint.assessment.gasstation;

/**
 * This class is an implementation of a gas pump.
 * <p>
 * It is final and should not be modified!
 * <p>
 * It is not thread-safe! It should only ever be used by one thread.
 * <p>
 * Especially only one thread at a time may call the pumpGas(double) method!
 */
public final class GasPump {
    private final GasType gasType;
    private double amount;

    public GasPump(GasType gasType, double amount) {
        super();
        this.gasType = gasType;
        this.amount = amount;
    }

    public void pumpGas(double amount) {
        this.amount -= amount;

        // simulate that it takes time to pump some gas
        try {
            Thread.sleep((long) (amount * 100));
        } catch (InterruptedException e) {
            // ignored
        }
    }

    public double getRemainingAmount() {
        return amount;
    }

    public GasType getGasType() {
        return gasType;
    }
}
