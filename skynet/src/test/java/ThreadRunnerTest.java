import org.example.service.Faction;
import org.example.util.ThreadRunner;
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
        Faction faction1 = new Faction("Winner", new LinkedBlockingQueue<>(), 1, 1);
        Faction faction2 = new Faction("Loser", new LinkedBlockingQueue<>(), 1, 1);

        setRobotsBuiltThroughReflection(faction1, 8);
        setRobotsBuiltThroughReflection(faction2, 5);

        Faction winner = runner.determineWinner(faction1, faction2);
        assertNotNull(winner);
        assertEquals("Winner", winner.getTitle());
    }

    private void setRobotsBuiltThroughReflection(Faction faction, int count) {
        java.lang.reflect.Field field = null;
        try {
            field = Faction.class.getDeclaredField("robotsBuilt");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        field.setAccessible(true);
        try {
            field.set(faction, count);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        assertNotNull(field);
    }
}