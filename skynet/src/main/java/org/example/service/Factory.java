package org.example.service;

import lombok.Getter;
import org.example.model.Part;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
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
    private static final int SLEEP_TIME_IN_MILLS = 50;

    private final int WORKING_DAYS;
    private final int MAX_PARTS_PER_DAY;
    private final Random random;
    /** Shared storage queue for parts */
    @Getter
    private BlockingQueue<Part> storage;

    public Factory(int workingDays, int maxPartsPerDay) {
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
            executeProductionCycle();
        } catch (InterruptedException e) {
            logger.severe("Factory was interrupted " + e);
            Thread.currentThread().interrupt();
        } finally {
            logger.fine("Factory has finished its work after " + WORKING_DAYS + " days");
        }
    }

    private Part generatePart() {
        return Part.values()[random.nextInt(Part.values().length)];
    }

    /**
     * Executes the complete production cycle for all working days.
     * Each day consists of producing parts and sleeping for a fixed interval.
     *
     * @throws InterruptedException if the thread is interrupted
     */
    private void executeProductionCycle() throws InterruptedException {
        for (int day = 1; day <= WORKING_DAYS; day++) {
            producePartsForDay(day);
            Thread.sleep(SLEEP_TIME_IN_MILLS);
        }
    }

    /**
     * Produces parts for a specific day and logs the production.
     * The number of parts produced is random between 1 and MAX_PARTS_PER_DAY.
     *
     * @param day the current day number
     * @throws InterruptedException if the thread is interrupted
     */
    private void producePartsForDay(int day) throws InterruptedException {
        int partsToProduce = random.nextInt(MAX_PARTS_PER_DAY) + 1;
        produceParts(partsToProduce);
        logger.info("DAY: " + day);
    }

    /**
     * Produces the specified number of parts and adds them to the storage queue.
     * Each part is generated randomly and put into the blocking queue.
     *
     * @param partsToProduce the number of parts to produce and store in day
     * @throws InterruptedException if the thread is interrupted
     */
    private void produceParts(int partsToProduce) throws InterruptedException {
        for (int i = 0; i < partsToProduce; i++) {
            storage.put(generatePart());
        }
    }
}
