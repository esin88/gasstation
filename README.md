This is written in Java programming language using IntelliJ IDEA. Libraries and tools that were used:
 * Maven - for dependency management and building
 * org.jetbraing.annotations - for marking nullable/not nullable fields and methods
 * org.junit - for unit and functional testing
 
First of all, I assumed that gas station works like real-life gas stations, and each pump has a much bigger initial gas amount than any any customer wants to buy.

And because of `GasPump` was final class with `double` remaining gas field, I created `PumpWorker` for each pump. It has `AtomicDouble` remaining gas counter and single-thread pool executor representing line to this gas pump. So each `GasPump` is guaranteed to be used by only one thread.

`AtomicDouble` is a class for safe concurrent `double` operations (it uses `java.util.concurrent.atomic.DoubleAdder` under the hood).

For each gas type I use separate `PumpWorkerManager` with `java.util.concurrent.PriorityBlockingQueue` of gas pump workers, that are sorted in descending remaining gas way. So each pump will empty uniformly. Getting pump with largest remaining gas amount complexity is O(1) and putting this pump back to the managers queue complexity is O(log N).

When customer wants to buy some gas, PumpManager gets pump with largest remaining gas amount and puts customer into the line to this pump, creating a `Future`. Customer waits until this `Future` is completed and then gets total price he has to pay.

The only two synchronized places in code are:
 * getting pump from managers queue and putting it back, so that managers queue is always consistent
 * setting gas price, due to `DoubleAdder` usage

some master change
111
temp 222

master edit
feature edit
