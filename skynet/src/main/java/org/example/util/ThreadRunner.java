package org.example.util;

import org.example.model.Part;
import org.example.service.Faction;
import org.example.service.Factory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Coordinates and manages the simulation of robot production competition between factions.
 * This class handles the execution of factory and faction threads, manages thread pooling,
 * and determines the winner based on production results.
 *
 * @author Dmitry Novogrodsky
 * @see Factory
 * @see Faction
 * @see ExecutorService
 */
public class ThreadRunner {
    private static final Logger logger = Logger.getLogger(ThreadRunner.class.getName());
    private static final int MAX_PARTS_PER_DAY = 10;
    private static final int DAYS = 100;
    private static final int MAX_CARRY_COUNT = 5;
    private static final int THREAD_POOL_SIZE = 3;

    /**
     * Executes the complete simulation of robot production competition.
     * Creates a factory and two factions, manages their execution through a thread pool,
     * waits for completion, and logs the results.
     *
     * <p><p>The simulation includes:</p>
     * <p>Factory producing random parts daily</p>
     * <p>Two factions competing for parts from the shared storage</p>
     * <p>Robot assembly based on collected parts</p>
     * <p>Winner determination based on total robots built</p>
     * </p>
     */
    public void runSimulation() {
        Factory factory = new Factory(DAYS, MAX_PARTS_PER_DAY);
        BlockingQueue<Part> storage = factory.getStorage();
        Faction worldFaction = new Faction("World", storage, DAYS, MAX_CARRY_COUNT);
        Faction wednesdayFaction = new Faction("Wednesday", storage, DAYS, MAX_CARRY_COUNT);

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        submitTasks(executor, factory, worldFaction, wednesdayFaction);

        executor.shutdown();
        try {
            if (executor.awaitTermination(DAYS, TimeUnit.SECONDS)) {
                logger.fine("Simulation completed successfully");
            } else {
                logger.warning("Simulation timed out");
            }
        } catch (InterruptedException e) {
            logger.severe("Simulation was interrupted " + e);
            Thread.currentThread().interrupt();
        }

        logWinner(worldFaction, wednesdayFaction);
    }

    /**
     * Logs the results of both factions and determines the winner.
     * Displays the number of robots built by each faction and invokes winner determination.
     *
     * @param world     the first faction participating in the simulation
     * @param wednesday the second faction participating in the simulation
     */
    private void logWinner(Faction world, Faction wednesday) {
        logger.info("\t\tSimulation Results");
        logger.info("Faction " + world.getTitle() + ": " + world.getRobotsBuilt() + " robots");
        logger.info("Faction " + wednesday.getTitle() + ": " + wednesday.getRobotsBuilt() + " robots");
        determineWinner(world, wednesday);
    }

    /**
     * Determines the winner of the simulation based on the number of robots built.
     * Compares the production results of both factions and returns the winning faction.
     * In case of a draw, returns null and logs the draw result.
     *
     * @param world     the first faction to compare
     * @param wednesday the second faction to compare
     * @return the winning Faction instance, or null in case of a draw
     */
    public Faction determineWinner(Faction world, Faction wednesday) {
        int result = world.getRobotsBuilt() - wednesday.getRobotsBuilt();

        if (result > 0) {
            logger.info("\t\tFaction " + world.getTitle() + " wins");
            return world;
        } else if (result < 0) {
            logger.info("\t\tFaction " + wednesday.getTitle() + " wins");
            return wednesday;
        } else {
            logger.info("\t\tDraw");
            return null;
        }
    }

    /**
     * Submits multiple Runnable tasks to the ExecutorService for execution.
     * Each task is executed in the thread pool managed by the ExecutorService.
     *
     * @param executor the ExecutorService that will manage task execution
     * @param tasks    the Runnable tasks to be executed
     */
    private void submitTasks(ExecutorService executor, Runnable... tasks) {
        for (Runnable task : tasks) {
            executor.execute(task);
        }
    }
}
