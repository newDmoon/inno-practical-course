package org.innowise.util;

import org.innowise.service.Faction;
import org.junit.jupiter.api.Test;

import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThreadRunnerTest {
    @Test
    void testCompleteSimulationRunsSuccessfully() {
        ThreadRunner runner = new ThreadRunner();

        long startTime = System.currentTimeMillis();
        runner.runSimulation();
        long duration = System.currentTimeMillis() - startTime;

        assertTrue(duration > 0);
    }

    @Test
    void testDetermineWinnerLogic() {
        ThreadRunner runner = new ThreadRunner();
        Faction factionWorld = new StubFaction("World", 8);
        Faction factionWednesday = new StubFaction("Wednesday", 5);

        Faction winner = runner.determineWinner(factionWorld, factionWednesday);
        assertNotNull(winner);
        assertEquals("World", winner.getTitle());
    }

    static class StubFaction extends Faction {
        private final int robotsBuilt;

        public StubFaction(String title, int robotsBuilt) {
            super(null, null, title, new LinkedBlockingQueue<>(), 0, 1);
            this.robotsBuilt = robotsBuilt;
        }

        @Override
        public int getRobotsBuilt() {
            return robotsBuilt;
        }
    }
}