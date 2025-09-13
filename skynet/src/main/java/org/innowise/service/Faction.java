package org.innowise.service;

import lombok.Getter;
import org.innowise.model.Part;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Logger;

/**
 * Represents a faction that used to build robots from parts
 * collected from a shared storage. The faction continues
 * its operations for a specified number of days.
 * Implements the Runnable interface to run in a separate thread.
 *
 * @author Dmitry Novogrodsky
 * @see Runnable
 * @see Part
 * @see BlockingQueue
 */
public class Faction implements Runnable {
    private static final Logger logger = Logger.getLogger(Faction.class.getName());
    private static final int REQUIRED_HANDS = 2;
    private static final int REQUIRED_FEET = 1;
    private static final int REQUIRED_HEAD = 1;
    private static final int REQUIRED_TORSO = 1;

    @Getter
    private final String title;
    /** Shared storage queue for parts */
    private final BlockingQueue<Part> storage;
    private final int DAYS;
    /** Maximum number of parts the faction can collect per day */
    private final int MAX_DAY_CAPACITY;
    /** Inventory mapping parts to their quantities */
    private Map<Part, Integer> inventory;
    /** Total number of robots built by this faction */
    @Getter
    private int robotsBuilt = 0;
    private final CyclicBarrier dayBarrier;
    private final CyclicBarrier nightBarrier;

    public Faction(CyclicBarrier dayBarrier, CyclicBarrier nightBarrier, String title, BlockingQueue<Part> storage, final int DAYS, final int MAX_CAPACITY) {
        this.dayBarrier = dayBarrier;
        this.nightBarrier = nightBarrier;
        this.title = title;
        this.storage = storage;
        this.DAYS = DAYS;
        this.MAX_DAY_CAPACITY = MAX_CAPACITY;
        this.inventory = new EnumMap<>(Part.class);

        for (Part part : Part.values()) {
            inventory.put(part, 0);
        }
    }

    /**
     * The main execution method for the faction thread.
     * Runs the building cycle for the specified number of days.
     * Handles interruptions gracefully and logs appropriate messages.
     */
    @Override
    public void run() {
        logger.info("Faction  " + title + " is starting");
        try {
            for (int day = 1; day <= DAYS; day++) {
                dayBarrier.await();
                collectPartsForDay();
                buildRobots();
                nightBarrier.await();
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            logger.severe("Faction " + title + " was interrupted " + e);
        } finally {
            logger.fine("Faction " + title + " finished");
        }
    }

    /**
     * Attempts to build as many robots as possible from available parts.
     * Continues building until parts remain for another robot.
     */
    private void buildRobots() {
        while (canBuild()) {
            consumeParts();
            robotsBuilt++;
        }
    }

    /**
     * Checks if there are enough parts to build a complete robot.
     *
     * @return true if all required parts are available in enough quantities, false otherwise
     */
    private boolean canBuild() {
        return inventory.getOrDefault(Part.HEAD, 0) >= REQUIRED_HEAD &&
                inventory.getOrDefault(Part.TORSO, 0) >= REQUIRED_TORSO &&
                inventory.getOrDefault(Part.HAND, 0) >= REQUIRED_HANDS &&
                inventory.getOrDefault(Part.FEET, 0) >= REQUIRED_FEET;
    }

    /**
     * Consumes the required parts from inventory to build one robot.
     * Reduces the inventory counts for each part type by the required amount.
     */
    private void consumeParts() {
        inventory.merge(Part.HEAD, -REQUIRED_HEAD, Integer::sum);
        inventory.merge(Part.TORSO, -REQUIRED_TORSO, Integer::sum);
        inventory.merge(Part.HAND, -REQUIRED_HANDS, Integer::sum);
        inventory.merge(Part.FEET, -REQUIRED_FEET, Integer::sum);
    }

    /**
     * Collects parts from the shared storage for the current day.
     * Attempts to collect up to the maximum daily capacity, but stops early if storage is empty.
     */
    private void collectPartsForDay() {
        List<Part> drained = new ArrayList<>();
        storage.drainTo(drained, MAX_DAY_CAPACITY);
        for (Part part : drained) {
            inventory.merge(part, 1, Integer::sum);
        }
        logger.fine("Faction " + title + " collected " + drained.size() + " parts");
    }
}
