package org.innowise.service;

import lombok.Getter;
import org.innowise.model.Part;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

/**
 * Represents a factory that produces robot parts and stores them in a shared blocking queue.
 * The factory operates for a specified number of days, producing a random number of parts each day.
 * Implements the Runnable interface to run in a separate thread.
 *
 * @author Dmitry Novogrodsky
 * @see Runnable
 * @see Part
 * @see LinkedBlockingQueue
 */
public class Factory implements Runnable {
    private static final Logger logger = Logger.getLogger(Factory.class.getName());

    private final int WORKING_DAYS;
    private final int MAX_PARTS_PER_DAY;
    private final Random random;
    private final CyclicBarrier dayBarrier;
    private final CyclicBarrier nightBarrier;

    /**
     * Shared storage queue for parts
     */
    @Getter
    private BlockingQueue<Part> storage;

    public Factory(CyclicBarrier dayBarrier, CyclicBarrier nightBarrier, int workingDays, int maxPartsPerDay) {
        this.dayBarrier = dayBarrier;
        this.nightBarrier = nightBarrier;
        this.WORKING_DAYS = workingDays;
        this.MAX_PARTS_PER_DAY = maxPartsPerDay;
        this.random = new Random();
        this.storage = new LinkedBlockingQueue<>(workingDays * maxPartsPerDay);
    }

    /**
     * The main execution method for the factory thread.
     * Runs the production cycle for the specified number of days.
     * Handles interruptions gracefully and logs appropriate messages.
     */
    @Override
    public void run() {
        logger.info("Factory is starting");
        try {
            for (int day = 1; day <= WORKING_DAYS; day++) {
                int produced = producePartsForDay();
                logger.info("Factory: day " + day + " produced " + produced + " parts");

                dayBarrier.await();
                nightBarrier.await();
            }
        } catch (Exception e) {
            logger.severe("Factory was interrupted " + e);
            Thread.currentThread().interrupt();
        } finally {
            logger.fine("Factory has finished its work after " + WORKING_DAYS + " days");
        }
    }

    /**
     * Produces parts for a specific day.
     * The number of parts produced is random between 1 and MAX_PARTS_PER_DAY.
     *
     * @return count of produced parts
     * @throws InterruptedException if the thread is interrupted
     */
    private int producePartsForDay() throws InterruptedException {
        int partsToProduce = random.nextInt(MAX_PARTS_PER_DAY) + 1;
        for (int i = 0; i < partsToProduce; i++) {
            storage.put(generatePart());
        }
        return partsToProduce;
    }

    private Part generatePart() {
        return Part.values()[random.nextInt(Part.values().length)];
    }
}
