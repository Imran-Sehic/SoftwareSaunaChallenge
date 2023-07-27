package main;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FollowPath {

    private char[][] map;

    // Previous directions constants
    private static final int FROM_LEFT = 0;
    private static final int FROM_RIGHT = 1;
    private static final int FROM_ABOVE = 2;
    private static final int FROM_BELOW = 3;

    public void setMap(char[][] map) {
        this.map = map;
    }

    public int[] getStartingPosition() {
        List<int[]> startPositions = new ArrayList<>();
        List<int[]> endPositions = new ArrayList<>();

        //iterating through the map - O(n^2) time complexity
        for(int i = 0; i < map.length; i++) {
            for(int j = 0; j < map[i].length; j++) {
                if(map[i][j] == '@') {
                    if(!startPositions.isEmpty()) {
                        throw new Error("Invalid input, two or more starting positions!");
                    }
                    startPositions.add(new int[]{i, j});
                }
                if(map[i][j] == 'x') {
                    endPositions.add(new int[]{i, j});
                }
            }
        }

        if(startPositions.isEmpty()) throw  new Error("Invalid input, missing starting position!");
        if(endPositions.isEmpty()) throw  new Error("Invalid input, missing end position!");
        return startPositions.get(0);
    }

    // Starting point of the application
    public Map<String, String> start() {
        List<Character> letters = new ArrayList<>();
        List<Character> path = new ArrayList<>();
        // Initializing this list to have a track of letters at specific index, to not collect the same letter twice
        List<int[]> letterIndexes = new ArrayList<>();

        int[] startPosition = getStartingPosition();

        makeStep(startPosition, letters, path, letterIndexes);

        Map<String, String> result = new HashMap<>();
        result.put("Letters", letters.stream().map(Object::toString).reduce("", String::concat));
        result.put("Path", path.stream().map(Object::toString).reduce("", String::concat));

        return result;
    }

    // Getting the direction we came from
    public int getDirection(int[] previousPosition, int[] currentPosition) {
        int previousRowIndex = previousPosition[0];
        int previousColumnIndex = previousPosition[1];

        int rowIndex = currentPosition[0];
        int columnIndex = currentPosition[1];

        if (previousRowIndex == rowIndex) {
            return (previousColumnIndex < columnIndex) ? FROM_LEFT : FROM_RIGHT;
        } else {
            return (previousRowIndex < rowIndex) ? FROM_ABOVE : FROM_BELOW;
        }
    }

    public Map<int[], Character> getSurroundingWays(int[] currentPosition) {
        int rowIndex = currentPosition[0];
        int columnIndex = currentPosition[1];

        Map<int[], Character> neighbors = new HashMap<>() {{
            if(rowIndex > 0) put(new int[]{rowIndex-1, columnIndex}, map[rowIndex-1][columnIndex]);
            if(columnIndex < map[rowIndex].length - 1) put(new int[]{rowIndex, columnIndex+1}, map[rowIndex][columnIndex+1]);
            if(rowIndex < map.length - 1) put(new int[]{rowIndex+1, columnIndex}, map[rowIndex+1][columnIndex]);
            if(columnIndex > 0) put(new int[]{rowIndex, columnIndex-1}, map[rowIndex][columnIndex-1]);
        }};

        return neighbors.entrySet().stream().filter(w -> w.getValue() != ' ').collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // The function returns the step opposite to the direction we are coming from
    public char getHorizontalOrVerticalStep(int direction, int[] currentPosition) {
        int rowIndex = currentPosition[0];
        int columnIndex = currentPosition[1];
        // We are looking for a left or a right step
        if(direction == FROM_LEFT || direction == FROM_RIGHT) {
            char leftStep = columnIndex > 0 ? map[rowIndex][columnIndex - 1] : ' ';
            char rightStep = columnIndex < map[rowIndex].length - 1 ? map[rowIndex][columnIndex + 1] : ' ';
            return direction == FROM_LEFT ? rightStep : leftStep;
        }
        // We are looking for a top or bottom step
        else {
            char aboveStep = rowIndex > 0 ? map[rowIndex - 1][columnIndex] : ' ';
            char bottomStep = rowIndex < map.length - 1 ? map[rowIndex + 1][columnIndex] : ' ';
            return direction == FROM_ABOVE ? bottomStep : aboveStep;
        }
    }

    private void handleHorizontalOrVerticalStep(int direction, int[] currentPosition, List<Character> letters, List<Character> path, List<int[]> letterIndexes) {
        int rowIndex = currentPosition[0];
        int columnIndex = currentPosition[1];
        char nextStep = getHorizontalOrVerticalStep(direction, currentPosition);
        // If we came from left we go right and vice versa if next step is valid
        if(direction == FROM_LEFT || direction == FROM_RIGHT) {
            char aboveStep = getHorizontalOrVerticalStep(FROM_BELOW, currentPosition);
            char belowStep = getHorizontalOrVerticalStep(FROM_ABOVE, currentPosition);
            if((aboveStep == ' ' || aboveStep == '-') && (belowStep == ' ' || belowStep == '-') && nextStep == ' ') throw new Error ("Invalid input, broken path!");
            makeStep(new int[]{rowIndex, direction == FROM_LEFT ? columnIndex + 1 : columnIndex - 1}, letters, path, letterIndexes, currentPosition);
        }
        // If we came from above we go down and vice versa if next step is valid
        else {
            char rightStep = getHorizontalOrVerticalStep(FROM_LEFT, currentPosition);
            char leftStep = getHorizontalOrVerticalStep(FROM_RIGHT, currentPosition);
            if((rightStep == ' ' || rightStep == '|') && (leftStep == ' ' || leftStep == '|') && nextStep == ' ') throw new Error ("Invalid input, broken path!");
            makeStep(new int[]{direction == FROM_ABOVE ? rowIndex + 1 : rowIndex - 1, columnIndex}, letters, path, letterIndexes, currentPosition);
        }
    }

    private void handleCrossingStep(int direction, int[] currentPosition, List<Character> letters, List<Character> path, List<int[]> letterIndexes) {
        int rowIndex = currentPosition[0];
        int columnIndex = currentPosition[1];

        // If we came from left or right we go either above or down
        if(direction == FROM_LEFT || direction == FROM_RIGHT) {
            char aboveStep = getHorizontalOrVerticalStep(FROM_BELOW, currentPosition);
            char belowStep = getHorizontalOrVerticalStep(FROM_ABOVE, currentPosition);

            if(aboveStep != ' ' && aboveStep != '-') {
                if(belowStep != ' ' && belowStep != '-') throw new Error("Invalid input, fork in the path!");
                makeStep(new int[]{rowIndex - 1, columnIndex}, letters, path, letterIndexes, currentPosition);
            }
            if(belowStep != ' ' && belowStep != '-') {
                makeStep(new int[]{rowIndex + 1, columnIndex}, letters, path, letterIndexes, currentPosition);
            }
            if((aboveStep == ' ' || aboveStep == '-') && (belowStep == ' ' || belowStep == '-')) {
                throw new Error("Invalid input, fake turn!");
            }
        }
        // If we came from above or down we go either left or right
        else {
            char leftStep = getHorizontalOrVerticalStep(FROM_RIGHT, currentPosition);
            char rightStep = getHorizontalOrVerticalStep(FROM_LEFT, currentPosition);

            if(leftStep != ' ' && leftStep != '|') {
                if(rightStep != ' ' && rightStep != '|') throw new Error("Invalid input, fork in the path!");
                makeStep(new int[]{rowIndex, columnIndex - 1}, letters, path, letterIndexes, currentPosition);
            }
            if(rightStep != ' ' && rightStep != '|') {
                makeStep(new int[]{rowIndex, columnIndex + 1}, letters, path, letterIndexes, currentPosition);
            }
            if((leftStep == ' ' || leftStep == '|') && (rightStep == ' ' || rightStep == '|')) {
                throw new Error("Invalid input, fake turn!");
            }
        }
    }

    private void handleLetterStep(int direction, int[] currentPosition, List<Character> letters, List<Character> path, List<int[]> letterIndexes) {
        int rowIndex = currentPosition[0];
        int columnIndex = currentPosition[1];

        boolean isLetterVisited = letterIndexes.stream().anyMatch(position -> Arrays.equals(position, currentPosition));

        // If we came across the same letter twice (letter on the crossing) we won't enter this case
        if (!isLetterVisited) {
            letters.add(map[rowIndex][columnIndex]);
            letterIndexes.add(currentPosition);
        }
        char nextStep = getHorizontalOrVerticalStep(direction, currentPosition);
        // Coming from left we are looking to go right and vice versa
        if(direction == FROM_LEFT || direction == FROM_RIGHT) {
            char belowStep = getHorizontalOrVerticalStep(FROM_ABOVE, currentPosition);
            char aboveStep = getHorizontalOrVerticalStep(FROM_BELOW, currentPosition);
            boolean isBelowValid = belowStep != ' ' && belowStep != '-';
            boolean isAboveValid = aboveStep != ' ' && aboveStep != '-';

            if(!isBelowValid && !isAboveValid && nextStep == '|') {
                throw new Error("Invalid input, broken path!");
            }
            if(nextStep != ' ' && nextStep != '|') {
                makeStep(new int[]{rowIndex, direction == FROM_LEFT ? columnIndex + 1 : columnIndex - 1}, letters, path, letterIndexes, currentPosition);
            }
            // If not able to go straight go either up or down
            else {
                if(isBelowValid && isAboveValid) {
                    throw new Error("Invalid input, fork in the path");
                }
                if(isBelowValid || isAboveValid) {
                    int upOrDown = isBelowValid ? rowIndex + 1 : rowIndex - 1;
                    makeStep(new int[]{upOrDown, columnIndex}, letters, path, letterIndexes, currentPosition);
                }
            }
        }
        // Coming from above we are looking to go below and vice versa
        else {
            char rightStep = getHorizontalOrVerticalStep(FROM_LEFT, currentPosition);
            char leftStep = getHorizontalOrVerticalStep(FROM_RIGHT, currentPosition);
            boolean isRightValid = rightStep != ' ' && rightStep != '|';
            boolean isLeftValid = leftStep != ' ' && leftStep != '|';

            if(!isRightValid && !isLeftValid && nextStep == '-') {
                throw new Error("Invalid input, broken path!");
            }
            if(nextStep != ' ' && nextStep != '-') {
                makeStep(new int[]{direction == FROM_ABOVE ? rowIndex + 1 : rowIndex - 1, columnIndex}, letters, path, letterIndexes, currentPosition);
            }
            // If not able to go straight go either right or left
            else {
                if(isRightValid && isLeftValid) {
                    throw new Error("Invalid input, fork in the path!");
                }
                if(isRightValid || isLeftValid) {
                    int rightOrLeft = isRightValid ? columnIndex + 1 : columnIndex - 1;
                    makeStep(new int[]{rowIndex, rightOrLeft}, letters, path, letterIndexes, currentPosition);
                }
            }
        }
    }

    // Making this method to call the starting position only once, to avoid passing non-existing previous position as parameter
    private void makeStep(int[] startPosition, List<Character> letters, List<Character> path, List<int[]> letterIndexes) {
        int rowIndex = startPosition[0];
        int columnIndex = startPosition[1];

        char currentChar = map[rowIndex][columnIndex];
        // Setting a map for each character (key) and its respective coordinates (value) (checking for multiple starting paths)
        Map<int[], Character> ways = getSurroundingWays(startPosition);

        // If the starting position offers only one way to go we can proceed with further checks
        if(ways.size() == 1) {
            int[] wayPosition = ways.keySet().iterator().next();
            char wayChar = ways.values().iterator().next();
            int direction = getDirection(wayPosition, startPosition);

            //                                           -
            // Checking for these scenarios: |@ or @| or @ or @
            //                                                -
            if((direction == FROM_LEFT || direction == FROM_RIGHT) && wayChar == '|'
                    || (direction == FROM_ABOVE || direction == FROM_BELOW) && wayChar == '-') {
                throw new Error("Invalid input, broken path!");
            }
            path.add(currentChar);
            // Invoking next step call with starting position as previous position
            makeStep(wayPosition, letters, path, letterIndexes, startPosition);
        } else if (ways.isEmpty()) {
            throw new Error("Invalid input, broken path!");
        } else {
            throw new Error("Invalid input, multiple starting paths!");
        }
    }

    // Making this method as override to previous one with addition of previousPosition parameter
    private void makeStep(int[] currentPosition, List<Character> letters, List<Character> path, List<int[]> letterIndexes, int[] previousPosition) {
        int rowIndex = currentPosition[0];
        int columnIndex = currentPosition[1];

        char currentChar = map[rowIndex][columnIndex];

        path.add(currentChar);

        if(currentChar == 'x') {
            // We reached the end successfully
            return;
        }

        Map<int[], Character> ways = getSurroundingWays(currentPosition);

        // From ways filtering out the previous step as we don't want to go back
        Map<int[], Character> possibleWays =
                ways
                        .entrySet().stream().filter(w -> !Arrays.equals(w.getKey(), previousPosition))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if(possibleWays.isEmpty()) {
            // If there is no possible way around then the path is broken
            throw new Error("Invalid input, broken path!");
        }

        int direction = getDirection(previousPosition, currentPosition);

        switch (currentChar) {
            case '-', '|' -> handleHorizontalOrVerticalStep(direction, currentPosition, letters, path, letterIndexes);
            case '+' -> handleCrossingStep(direction, currentPosition, letters, path, letterIndexes);
            default -> {
                // We check for an uppercase letter as the current step
                if (isCharUpperCaseLetter(currentChar)) {
                    handleLetterStep(direction, currentPosition, letters, path, letterIndexes);
                } else {
                    throw new Error("Invalid input, unrecognized character!");
                }
            }
        }

    }

    // Checking whether a character is an uppercase letter
    public boolean isCharUpperCaseLetter(char character) {
        String pattern = "[A-Z]";
        String charAsString = Character.toString(character);
        return Pattern.matches(pattern, charAsString);
    }

}
