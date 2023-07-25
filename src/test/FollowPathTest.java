package test;

import main.FollowPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class FollowPathTest {

    private FollowPath followPath;

    private void testCorrectPath(char[][] map, String expectedLetters, String expectedPath) {
        followPath.setMap(map);
        Map<String, String> result = followPath.start();

        String letters = result.get("Letters");
        String path = result.get("Path");

        assertEquals(expectedLetters, letters);
        assertEquals(expectedPath, path);
    }

    private void testWrongPath(char[][] map, String expectedErrorMessage) {
        followPath.setMap(map);
        Error error = assertThrows(Error.class, () -> followPath.start());
        assertEquals(expectedErrorMessage, error.getMessage());
    }

    @BeforeEach
    public void setUp() {
        followPath = new FollowPath();
    }

    /**
     * Testing correct paths
     */
    @Test
    public void testSimplePath() {
        char[][] map = {
                {'@', '-', '-', '-', 'A', '-', '-', '-', '+'},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '|'},
                {'x', '-', 'B', '-', '+', ' ', ' ', ' ', 'C'},
                {' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', '|'},
                {' ', ' ', ' ', ' ', '+', '-', '-', '-', '+'},
        };

        testCorrectPath(map, "ACB", "@---A---+|C|+---+|+-B-x");
    }

    @Test
    public void testPathWithIntersections() {
        char[][] map = {
                {'@', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {'|', ' ', '+', '-', 'C', '-', '-', '+', ' '},
                {'A', ' ', '|', ' ', ' ', ' ', ' ', '|', ' '},
                {'+', '-', '-', '-', 'B', '-', '-', '+', ' '},
                {' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', 'x'},
                {' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|'},
                {' ', ' ', '+', '-', '-', '-', 'D', '-', '+'},
        };

        testCorrectPath(map, "ABCD", "@|A+---B--+|+--C-+|-||+---D-+|x");
    }

    @Test
    public void testPathWithLettersOnTurns() {
        char[][] map = {
                {'@', '-', '-', '-', 'A', '-', '-', '-', '+'},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '|'},
                {'x', '-', 'B', '-', '+', ' ', ' ', ' ', '|'},
                {' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', '|'},
                {' ', ' ', ' ', ' ', '+', '-', '-', '-', 'C'},
        };

        testCorrectPath(map, "ACB", "@---A---+|||C---+|+-B-x");
    }

    @Test
    public void testPathWithLettersOnCrossing() {
        char[][] map = {
                {' ', ' ', ' ', ' ', '+', '-', 'O', '-', 'N', '-', '+', ' ', ' '},
                {' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' '},
                {' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', '+', '-', 'I', '-', '+'},
                {'@', '-', 'G', '-', 'O', '-', '+', ' ', '|', ' ', '|', ' ', '|'},
                {' ', ' ', ' ', ' ', '|', ' ', '|', ' ', '+', '-', '+', ' ', 'E'},
                {' ', ' ', ' ', ' ', '+', '-', '+', ' ', ' ', ' ', ' ', ' ', 'S'},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '|'},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'x'},
        };

        testCorrectPath(map, "GOONIES", "@-G-O-+|+-+|O||+-O-N-+|I|+-+|+-I-+|ES|x");
    }

    @Test
    public void testCompactPath() {
        char[][] map = {
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', '+', '-', 'L', '-', '+', ' ', ' ', ' '},
                {' ', '|', ' ', ' ', '+', 'A', '-', '+', ' '},
                {'@', 'B', '+', ' ', '+', '+', ' ', 'H', ' '},
                {' ', '+', '+', ' ', ' ', ' ', ' ', 'x', ' '},
        };

        testCorrectPath(map, "BLAH", "@B+++B|+-L-+A+++A-+Hx");
    }

    @Test
    public void testLongerThanNeededPath() {
        char[][] map = {
                {'@', '-', 'A', '-', '-', '+', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', '+', '-', 'B', '-', '-', 'x', '-', 'C'},
        };

        testCorrectPath(map, "AB", "@-A--+|+-B--x");
    }

    /**
     * Testing invalid paths
     */
    @Test
    public void testMissingStartingPosition() {
        char[][] map = {
                {'A', '|', ' ', 'x'},
        };

        testWrongPath(map, "Invalid input, missing starting position!");
    }

    @Test
    public void testMissingEndPosition() {
        char[][] map = {
                {'@', 'A', '|', ' '},
        };

        testWrongPath(map, "Invalid input, missing end position!");
    }

    @Test
    public void testMultipleStartingPositions() {
        char[][] map = {
                {'@', 'A', '|', ' ', 'x'},
                {'@', 'B', '|', ' ', 'x'},
        };

        testWrongPath(map, "Invalid input, two or more starting positions!");
    }

    @Test
    public void testForkInPath() {
        char[][] map = {
                {'@', ' ', ' '},
                {'|', ' ', 'x'},
                {'A', ' ', '|'},
                {'+', '-', '+'},
                {' ', ' ', '|'},
                {' ', ' ', 'N'},
                {' ', ' ', 'x'},
        };

        testWrongPath(map, "Invalid input, fork in the path!");
    }

    @Test
    public void testBrokenPath() {
        char[][] map = {
                {'@', '-', '-', '-', 'A', '-', '-', '-', '+'},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '|'},
                {'x', '-', 'B', '-', '+', ' ', ' ', ' ', 'C'},
                {' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', '|'},
                {' ', ' ', ' ', ' ', '+', '-', ' ', '-', '+'},
        };

        testWrongPath(map, "Invalid input, broken path!");
    }

    @Test
    public void multipleStartingPaths() {
        char[][] map = {
                {'x', '-', 'B', '-', '@', '-', 'A', '-', 'x'},
        };

        testWrongPath(map, "Invalid input, multiple starting paths!");
    }

    @Test
    public void testFakeTurnInPath() {
        char[][] map = {
                {'@', '-', 'A', '-', '+', '-', 'B', '-', 'x'},
        };

        testWrongPath(map, "Invalid input, fake turn!");
    }

    /**
     * Testing class methods
     */
    @Test
    public void testGetStartingPositionMethod() {
        char[][] map = {
                {' ', ' ', ' ', ' ', '+', '-', 'O', '-', 'N', '-', '+', ' ', ' '},
                {' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' '},
                {' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', '+', '-', 'I', '-', '+'},
                {' ', ' ', 'G', '-', 'O', '-', '+', ' ', '|', ' ', '|', ' ', '|'},
                {' ', ' ', '|', ' ', '|', ' ', '|', ' ', '+', '-', '+', ' ', 'E'},
                {' ', ' ', '@', ' ', '+', '-', '+', ' ', ' ', ' ', ' ', ' ', 'S'},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '|'},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'x'},
        };

        followPath.setMap(map);
        int[] startPosition = followPath.getStartingPosition();

        assertArrayEquals(startPosition, new int[]{5, 2});
    }

    @Test
    public void testIsCharUpperCaseLetterMethod() {
        boolean isNotCharUpperCaseLetter = followPath.isCharUpperCaseLetter(']');
        boolean isCharUpperCaseLetter = followPath.isCharUpperCaseLetter('A');

        assertFalse(isNotCharUpperCaseLetter);
        assertTrue(isCharUpperCaseLetter);
    }

    @Test
    public void testGetDirectionMethod() {
        char[][] map = {
                {' ', ' ', ' ', ' ', '+', '-', 'O', '-', 'N', '-', '+', ' ', ' '},
                {' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' '},
                {' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', '+', '-', 'I', '-', '+'},
                {' ', ' ', 'G', '-', 'O', '-', '+', ' ', '|', ' ', '|', ' ', '|'},
                {' ', ' ', '|', ' ', '|', ' ', '|', ' ', '+', '-', '+', ' ', 'E'},
                {' ', ' ', '@', ' ', '+', '-', '+', ' ', ' ', ' ', ' ', ' ', 'S'},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '|'},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'x'},
        };

        followPath.setMap(map);

        int FROM_LEFT = 0;
        int[] previousPosition = new int[]{2, 8};
        int[] currentPosition = new int[]{2, 9};

        int direction = followPath.getDirection(previousPosition, currentPosition);

        assertEquals(FROM_LEFT, direction);
    }

    @Test
    public void testGetSurroundingWaysMethod() {
        char[][] map = {
                {' ', ' ', ' ', ' ', '+', '-', 'O', '-', 'N', '-', '+', ' ', ' '},
                {' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' '},
                {' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', '+', '-', 'I', '-', '+'},
                {' ', ' ', 'G', '-', 'O', '-', '+', ' ', '|', ' ', '|', ' ', '|'},
                {' ', ' ', '|', ' ', '|', ' ', '|', ' ', '+', '-', '+', ' ', 'E'},
                {' ', ' ', '@', ' ', '+', '-', '+', ' ', ' ', ' ', ' ', ' ', 'S'},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '|'},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'x'},
        };

        followPath.setMap(map);
        int[] currentPosition = new int[]{2, 9};

        Map<int[], Character> ways = followPath.getSurroundingWays(currentPosition);
        Map<String, Character> modifiedWays = new HashMap<>();
        for (Map.Entry<int[],Character> entry : ways.entrySet()) {
            modifiedWays.put(Arrays.toString(entry.getKey()), entry.getValue());
        }
            Map<String, Character> expectedWays = new HashMap<>() {{
            put(Arrays.toString(new int[]{2, 8}), '+');
            put(Arrays.toString(new int[]{2, 10}), 'I');
        }};

        assertEquals(expectedWays, modifiedWays);
    }

    @Test
    public void testGetHorizontalOrVerticalStepMethod() {
        char[][] map = {
                {' ', ' ', ' ', ' ', '+', '-', 'O', '-', 'N', '-', '+', ' ', ' '},
                {' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' '},
                {' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', '+', '-', 'I', '-', '+'},
                {' ', ' ', 'G', '-', 'O', '-', '+', ' ', '|', ' ', '|', ' ', '|'},
                {' ', ' ', '|', ' ', '|', ' ', '|', ' ', '+', '-', '+', ' ', 'E'},
                {' ', ' ', '@', ' ', '+', '-', '+', ' ', ' ', ' ', ' ', ' ', 'S'},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '|'},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'x'},
        };

        followPath.setMap(map);
        int[] currentPosition = new int[]{2, 9};

        int FROM_LEFT = 0;
        int FROM_RIGHT = 1;
        int FROM_ABOVE = 2;
        int FROM_BELOW = 3;

        char leftStep = followPath.getHorizontalOrVerticalStep(FROM_RIGHT, currentPosition);
        char rightStep = followPath.getHorizontalOrVerticalStep(FROM_LEFT, currentPosition);
        char aboveStep = followPath.getHorizontalOrVerticalStep(FROM_BELOW, currentPosition);
        char belowStep = followPath.getHorizontalOrVerticalStep(FROM_ABOVE, currentPosition);

        assertEquals(leftStep, '+');
        assertEquals(rightStep, 'I');
        assertEquals(aboveStep, ' ');
        assertEquals(belowStep, ' ');
    }
}
