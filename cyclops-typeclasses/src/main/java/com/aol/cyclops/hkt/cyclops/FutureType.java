package com.aol.cyclops.hkt.cyclops;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

import org.reactivestreams.Publisher;

import com.aol.cyclops.control.FutureW;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.jdk.CompletableFutureType;

/**
 * Simulates Higher Kinded Types for FutureW's
 * 
 * FutureWType is a FutureW and a Higher Kinded Type (FutureWType.µ,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the FutureW
 */

public final class FutureType<T> extends FutureW<T> implements Higher<FutureType.µ, T>  {
   
    
    private FutureType(CompletableFuture<T> future) {
        super(
              future); 
    }





    /**
     * Witness type
     * 
     * @author johnmcclean
     *
     */
    public static class µ {
    }

    /**
     * An empty FutureW
     * 
     * @return A FutureType that wraps a CompletableFuture with a null result
     */
    public static <T> FutureType<T> empty() {
        return widen(FutureW.empty());
    }
    /**
     * An empty FutureType
     * 
     * @return A FutureType that wraps a CompletableFuture with a null result
     */
    public static <T> FutureType<T> future() {
        return widen(FutureW.future());
    }
    /**
     * Convert the raw Higher Kinded Type for  FutureType types into the FutureType type definition class
     * 
     * @param future HKT encoded list into a FutureType
     * @return FutureType
     */
    public static <T> FutureType<T> narrowK(final Higher<FutureType.µ, T> future) {
       return (FutureType<T>)future;
    }
    /**
     * Construct a FutureW asyncrhonously that contains a single value extracted from the supplied reactive-streams Publisher
     * 
     * 
     * <pre>
     * {@code 
     *   ReactiveSeq<Integer> stream =  ReactiveSeq.of(1,2,3);
        
        FutureType<Integer> future = FutureType.fromPublisher(stream,ex);
        
        //FutureType[1]
     * 
     * }
     * </pre>
     * 
     * 
     * @param pub Publisher to extract value from
     * @param ex Executor to extract value on
     * @return FutureType populated asyncrhonously from Publisher
     */
    public static <T> FutureType<T> fromPublisher(final Publisher<T> pub, final Executor ex) {
        return widen(FutureW.fromPublisher(pub,ex));
    }

    /**
     * Construct a FutureW asyncrhonously that contains a single value extracted from the supplied Iterable
     * <pre>
     * {@code 
     *  ReactiveSeq<Integer> stream =  ReactiveSeq.of(1,2,3);
        
        FutureType<Integer> future = FutureType.fromIterable(stream,ex);
        
        //FutureType[1]
     * 
     * }
     * </pre>
     * @param iterable Iterable to generate a FutureW from
     * @param ex  Executor to extract value on
     * @return FutureType populated asyncrhonously from Iterable
     */
    public static <T> FutureType<T> fromIterable(final Iterable<T> iterable, final Executor ex) {

        return widen(FutureW.fromIterable(iterable,ex));
    }

    /**
     * Construct a FutureW syncrhonously that contains a single value extracted from the supplied reactive-streams Publisher
     * <pre>
     * {@code 
     *   ReactiveSeq<Integer> stream =  ReactiveSeq.of(1,2,3);
        
        FutureType<Integer> future = FutureType.fromPublisher(stream);
        
        //FutureW[1]
     * 
     * }
     * </pre>
     * @param pub Publisher to extract value from
     * @return FutureType populated syncrhonously from Publisher
     */
    public static <T> FutureType<T> fromPublisher(final Publisher<T> pub) {
        return widen(FutureW.fromPublisher(pub));
    }

    /**
     * Construct a FutureW syncrhonously that contains a single value extracted from the supplied Iterable
     * 
     * <pre>
     * {@code 
     *  ReactiveSeq<Integer> stream =  ReactiveSeq.of(1,2,3);
        
        FutureType<Integer> future = FutureType.fromIterable(stream);
        
        //FutureType[1]
     * 
     * }
     * </pre>
     * 
     * 
     * @param iterable Iterable to extract value from
     * @return FutureType populated syncrhonously from Iterable
     */
    public static <T> FutureType<T> fromIterable(final Iterable<T> iterable) {
        return widen(FutureW.fromIterable(iterable));
    }

    /**
     * Create a FutureType instance from the supplied CompletableFuture
     * 
     * @param f CompletableFuture to wrap as a FutureW
     * @return FutureType wrapping the supplied CompletableFuture
     */
    public static <T> FutureType<T> of(final CompletableFuture<T> f) {
        return  widen(FutureW.of(f));
    } 

    /**
     * Schedule the population of a FutureW from the provided Supplier, the provided Cron (Quartz format) expression will be used to
     * trigger the population of the FutureW. The provided ScheduledExecutorService provided the thread on which the 
     * Supplier will be executed.
     * 
     * <pre>
     * {@code 
     *  
     *    FutureType<String> future = FutureType.schedule("* * * * * ?", Executors.newScheduledThreadPool(1), ()->"hello");
     *    
     *    //FutureType["hello"]
     * 
     * }</pre>
     * 
     * 
     * @param cron Cron expression in Quartz format
     * @param ex ScheduledExecutorService used to execute the provided Supplier
     * @param t The Supplier to execute to populate the FutureW
     * @return FutureW populated on a Cron based Schedule
     */
    public static <T> FutureType<T> schedule(final String cron, final ScheduledExecutorService ex, final Supplier<T> t) {
        return widen(FutureW.schedule(cron,ex,t));
    }

    /**
     * Schedule the population of a FutureW from the provided Supplier after the specified delay. The provided ScheduledExecutorService provided the thread on which the 
     * Supplier will be executed.
     * <pre>
     * {@code 
     *  
     *    FutureW<String> future = FutureW.schedule(10l, Executors.newScheduledThreadPool(1), ()->"hello");
     *    
     *    //FutureW["hello"]
     * 
     * }</pre>
     * 
     * @param delay Delay after which the FutureW should be populated
     * @param ex ScheduledExecutorService used to execute the provided Supplier
     * @param t he Supplier to execute to populate the FutureW
     * @return FutureW populated after the specified delay
     */
    public static <T> FutureType<T> schedule(final long delay, final ScheduledExecutorService ex, final Supplier<T> t) {
        return widen(FutureW.schedule(delay,ex,t));
    }
    /**
     * Create a FutureW object that asyncrhonously populates using the Common
     * ForkJoinPool from the user provided Supplier
     * 
     * @param s
     *            Supplier to asynchronously populate results from
     * @return FutureW asynchronously populated from the Supplier
     */
    public static <T> FutureType<T> ofSupplier(final Supplier<T> s) {
        return widen(FutureW.ofSupplier(s));
    }

    /**
     * Create a FutureW object that asyncrhonously populates using the provided
     * Executor and Supplier
     * 
     * @param s
     *            Supplier to asynchronously populate results from
     * @param ex
     *            Executro to asynchronously populate results with
     * @return FutureW asynchronously populated from the Supplier
     */
    public static <T> FutureType<T> ofSupplier(final Supplier<T> s, final Executor ex) {
        return widen(FutureW.ofSupplier(s,ex));
    }
    /**
     * Construct a successfully completed FutureW from the given value
     * 
     * @param result
     *            To wrap inside a FutureW
     * @return FutureW containing supplied result
     */
    public static <T> FutureType<T> ofResult(final T result) {
        return widen(FutureW.ofResult(result));
    }

    /**
     * Construct a completed-with-error FutureW from the given Exception
     * 
     * @param error
     *            To wrap inside a FutureW
     * @return FutureW containing supplied error
     */
    public static <T> FutureType<T> ofError(final Throwable error) {
        final CompletableFuture<T> cf = new CompletableFuture<>();
        cf.completeExceptionally(error);

        return widen(FutureW.of(cf));
    }

    /**
     * Convert a FutureW to a simulated HigherKindedType that captures FutureW nature
     * and FutureW element data type separately. Recover via @see FutureWType#narrow
     * 
     * If the supplied FutureW implements FutureWType it is returned already, otherwise it
     * is wrapped into a FutureW implementation that does implement FutureWType
     * 
     * @param FutureW FutureW to widen to a FutureWType
     * @return FutureWType encoding HKT info about FutureWs
     */
    public static <T> FutureType<T> widen(final FutureW<T> futureW) {
        if (futureW instanceof FutureType)
            return (FutureType<T>) futureW;
        return new FutureType<T>(
                         futureW.getFuture());
    }

    /**
     * Convert the HigherKindedType definition for a FutureW into
     * 
     * @param FutureW Type Constructor to convert back into narrowed type
     * @return FutureW from Higher Kinded Type
     */
    public static <T> FutureW<T> narrow(final Higher<FutureType.µ, T> futureW) {
        if (futureW instanceof FutureW)
            return (FutureW) futureW;
        //this code should be unreachable due to HKT type checker
        final FutureType<T> type = (FutureType<T>) futureW;
        return type.narrow();
    }
    /**
     * Convert the HigherKindedType definition for a FutureW into
     * 
     * @param Future Type Constructor to convert back into narrowed type
     * @return CompletableFuture from Higher Kinded Type
     */
    public static <T> CompletableFuture<T> narrowCompletableFuture(final Higher<FutureType.µ, T> futureW) {
        if (futureW instanceof FutureW)
            return ((FutureW<T>) futureW).getFuture();
        final FutureType<T> type = (FutureType<T>) futureW;
        return type.narrow().getFuture();
    }

    

       

        /**
         * @return This back as a FutureW
         */
        public FutureW<T> narrow() {
            return  this;
        }

        
       

    

}
