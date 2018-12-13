package com.example.bailey.flood_it;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;


class Game {

    private final int mWidth;
    private final int mHeight;
    private final int mColourCount;
    private final int[][] mData;


    private int currentRound = 1;
    private int roundLimit;


    private Set<GamePlayListener> mGamePlayListeners = new HashSet<>();
    private Set<GameWinListener> mGameWinListeners = new HashSet<>();
    private Set<GameLoseListener> mGameLoseListeners = new HashSet<>();

    Game(final int width, final int height, final int colourCount) {
        Random rand = new Random();
        final float p = 0.2f;

        mWidth = width;
        mHeight = height;
        mColourCount = colourCount;

        currentRound = 0;
        roundLimit = mWidth * 2;

        mData = new int[width][height];
        //Generate an initial color value
        mData[0][0] = rand.nextInt(colourCount);

        //Generate values for the top row of the array
        for (int x = 1; x < mWidth; x++) {
            /* Generates a random float (0.0 - 1.0) and compares to the defined probability,
               if condition is met chance of cluster is increased by setting the value to the
               left array value
             */
            if (rand.nextFloat() < p) {
                mData[x][0] = mData[x - 1][0];
            } else {
                mData[x][0] = rand.nextInt(colourCount);
            }
        }

        //Generate values for the leftmost column of the array
        for (int y = 1; y < mHeight; y++) {
            if (rand.nextFloat() < p) {
                mData[0][y] = mData[0][y - 1];
            } else {
                mData[0][y] = rand.nextInt(colourCount);
            }
        }

        //Generate values for the rest of the array
        for (int innerX = 1; innerX < mWidth; innerX++) {
            for (int innerY = 1; innerY < mHeight; innerY++) {
                //Perform the same probability check
                if (rand.nextFloat() < p) {
                    //Decides to copy the value above or the value to the left
                    if (rand.nextInt(2) == 1) {
                        System.out.println("1");
                        mData[innerX][innerY] = mData[innerX - 1][innerY];
                    } else {
                        System.out.println("2");
                        mData[innerX][innerY] = mData[innerX][innerY - 1];
                    }
                } else {
                    mData[innerX][innerY] = rand.nextInt(colourCount);
                }
            }
        }


    }

    int getHeight() {
        return mHeight;
    }

    int getColourCount() {
        return mColourCount;
    }

    /*The amount of turns the users has to complete the game*/
    int getRoundLimit() {
        return roundLimit;
    }

    /*returns the round limit - the current round, this gives the rounds remaining which represents the users score at the end of the game*/
    int getScore() {
        return getRoundLimit() - currentRound;
    }


    /*Set the colour at position (x,y) to the colour identified by the colour parameter*/

    private void setColor(int x, int y, int color) {
        mData[x][y] = color;
    }

    /*Private helper that informs the GamePlayListeners*/
    private void getGamePlayListeners() {
        for (GamePlayListener current : mGamePlayListeners) {
            current.onGameChanged(currentRound);
        }
    }

    /*Public method that is used to add a GamePlayListener to a instance of the game*/
    void addGamePlayListener(final GamePlayListener listener) {
        mGamePlayListeners.add(listener);
    }

    /*Private helper that informs the GameWinListeners with a loop*/
    private void getGameWinListeners() {
        for (GameWinListener current : mGameWinListeners) {
            current.onWon(currentRound);
        }
    }

    /*Public method that is used to add a GameWinListeners to an instance of the game*/
    void addGameWinListener(final GameWinListener gameWinListener) {
        mGameWinListeners.add(gameWinListener);
    }

    private void getGameLoseListeners() {
        for (GameLoseListener current : mGameLoseListeners) {
            current.onLose(getRoundLimit());
        }
    }

    void addGameLoseListener(final GameLoseListener gameLoseListener) {
        mGameLoseListeners.add(gameLoseListener);
    }

    /*Used to remove the game lose listener from an instance of the game (i.e when the users wins the game)*/
    void removeGameLoseListener(final GameLoseListener gameLoseListener) {
        mGameLoseListeners.remove(gameLoseListener);
    }

    /*Methods that are called from the flood fill to notify and call the events*/
    private void notifyMove() {
        getGamePlayListeners();
    }

    private void notifyWin() {
        getGameWinListeners();
    }

    private void notifyLost() {
        getGameLoseListeners();
    }

    /*Public method that is called from the GameView when a color input is detected*/
    void playColor(int clr) {
        //If the selected color is the same as the current color, ignore and increment round
        if (clr != mData[0][0]) {
            floodFill(0, 0, clr, mData[0][0]);
            currentRound = currentRound + 1;
            notifyMove();
            if (isWon()) {
                notifyWin();
            }

            if (isLost()) {
                notifyLost();
            }
        }
    }

    /*Private helper that performs the flood fill operation on the array*/
    private void floodFill(int x, int y, int clr, int oldClr) {
        if (x < 0 || x >= mData.length || y < 0 || y >= mData[x].length)
            return;
        if (clr == mData[x][y]) return;
        if (oldClr != mData[x][y]) return;

        if (mData[x][y] == oldClr) {
            setColor(x, y, clr);

            floodFill(x, y + 1, clr, oldClr);
            floodFill(x, y - 1, clr, oldClr);
            floodFill(x + 1, y, clr, oldClr);
            floodFill(x - 1, y, clr, oldClr);
        }
    }

    /*Get the color at the passed xy position of the array, used to access the array elements within the game
      view to draw the Grid
     */
    int getColor(int x, int y) {
        return mData[x][y];
    }

    /*Private method used to check for game wins called on every flood fill*/
    private boolean isWon() {
        for (int x = 0; x < mWidth; x++) {
            for (int y = 0; y < mHeight; y++) {
                int z = mData[0][0];
                if (z != mData[x][y])
                    return false;
            }
        }
        return true;
    }

    /*Private method used to check if the game is lost on every flood fill*/
    private boolean isLost() {
        return currentRound >= getRoundLimit();
    }

    /*Listener interfaces for handling the current round, game win condition and game lose condition*/
    public interface GamePlayListener {
        void onGameChanged(int round);
    }

    public interface GameWinListener {
        void onWon(int rounds);
    }

    public interface GameLoseListener {
        void onLose(int round);
    }
}
