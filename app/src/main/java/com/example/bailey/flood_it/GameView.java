package com.example.bailey.flood_it;
/*DECELERATION: Various methods based upon Task 04*/

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import static java.lang.Math.floor;

public class GameView extends FrameLayout implements View.OnClickListener {

    private TextView roundCounter;

    private Paint[] paints = new Paint[10];
    private Bundle gameInfo;
    private Bundle themeSelection;

    private boolean extendButton = false;
    private int buttonTouched;

    private RectF gridBounds[][];
    private RectF buttonBounds[];

    private Game mGame;


    public GameView(Context context, AttributeSet atts) {
        super(context);
        themeSelection = ((Activity) getContext()).getIntent().getExtras();
        inflate(getContext(), R.layout.game, this);
        init();
    }

    /*Private helpers to get the ScreenWidth, used to generate the bounds for
      the various game elements before the onDraw method is called */
    private static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    private static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    /*Calls the methods that together make up a new game*/
    private void init() {
        initLayout();
        createGame();
        setListeners();
        setPaints();
        setGridBounds();
    }

    private void initLayout() {
        /*Get the XML layout elements*/
        roundCounter = findViewById(R.id.roundcount);
        ImageButton resetButton = findViewById(R.id.buttonReset);
        resetButton.setOnClickListener(this);

        /*Ensures that the game will draw*/
        setWillNotDraw(false);

    }

    private void setDialogs(int dialogType, int rounds) {

        /*Creates an intent that will be used in both the lose and win dialog
          Uses getContext so the activity is started from the parent activity (GameAcitivty)*/

        final Intent startNewGame = new Intent(getContext(), NewGameActivity.class);

        /*Uses flag that causes any task that would be associated with the activity to be cleared
          before the activity is started with the intent*/
        startNewGame.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        AlertDialog.Builder gameAlertBuilder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Holo_Dialog);
        gameAlertBuilder.setCancelable(false);

        /*The dialog type is sent to the method to decide which dialog to show*/
        if (dialogType == 0) {
            /*Create the Dialog that will shows onWon*/
            gameAlertBuilder.setTitle("You Won!");
            gameAlertBuilder.setMessage("You flooded the grid in " + rounds + " rounds. Press submit to save your score.");
                /*Set button that will recreate the game by calling init() method*/
            gameAlertBuilder.setNeutralButton("Play Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    init();
                }
            });
                /*Set button that will create an intent that opens the new game activity*/
            gameAlertBuilder.setNegativeButton("New Game", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    /*Ensures that the correct theme will be displayed by attacting the
                      them to an intent, carrying the selected theme to the new game
                      activity so that selected theme will be shown on a new game created
                      from this activity*/

                    if (themeSelection != null) {
                        int passTheme = themeSelection.getInt("themeSelection");
                        startNewGame.putExtra("themeSelection", passTheme);
                    }
                    getContext().startActivity(startNewGame);
                }
            });

            gameAlertBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast hsConfirm = Toast.makeText(getContext(), "Highscore Saved", Toast.LENGTH_SHORT);
                    hsConfirm.show();

                    /*Creates an intent to send to the Highscores activity that will be used to create a new
                      highscore. Gamemode represents the difficulty of the game chosen when a new game was created,
                      passing this to the highscores activity allows the highscore to be stored in the correct
                      table */
                    Intent intent = new Intent(getContext(), HighscoresActivity.class);
                    intent.putExtra("playerName", gameInfo.getString("playerName"));
                    intent.putExtra("playerScore", mGame.getScore());
                    intent.putExtra("gameMode", gameInfo.getInt("gameMode"));

                    getContext().startActivity(intent);
                    init();

                }
            });
        }

        if (dialogType == 1) {
            gameAlertBuilder.setTitle("You Lost");
            gameAlertBuilder.setMessage("You did not flood the grid within the round limit (" + rounds + ")");
            gameAlertBuilder.setPositiveButton("Play Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    init();
                }
            });

            gameAlertBuilder.setNegativeButton("New Game", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (themeSelection != null) {
                        int passTheme = themeSelection.getInt("themeSelection");
                        startNewGame.putExtra("themeSelection", passTheme);
                        getContext().startActivity(startNewGame);
                    }
                }
            });

        }

        gameAlertBuilder.show();
    }

    private void setListeners() {
        final Game.GameLoseListener glViewListenerImplementation;
        final Game.GamePlayListener gpViewListenerImplementation;
        final Game.GameWinListener gwViewListenerImplementation;


        /*Create listener to display the new round on every flood fill*/
        gpViewListenerImplementation = new Game.GamePlayListener() {
            public void onGameChanged(int value) {
                /*Converts the passed value to a string so it can be displayed within the text view*/
                roundCounter.setText(String.format("%s/%s", Integer.toString(value), Integer.toString(mGame.getRoundLimit())));

            }
        };

        /*Create listener that checks for game lose on every flood fill*/
        glViewListenerImplementation = new Game.GameLoseListener() {
            public void onLose(int value) {
                /*Call the loss dialog (1 = loss dialog)*/
                setDialogs(1, value);
            }
        };

        /*Create listener that checks for game win on every flood fill*/
        gwViewListenerImplementation = new Game.GameWinListener() {
            public void onWon(int value) {
                /*Calls the setDialog method which creates the correct dialog based on the value passed to it (1 = win dialog)*/
                setDialogs(0, value);
                mGame.removeGameLoseListener(glViewListenerImplementation);
            }
        };

        /*Add the listeners to the game instance*/
        mGame.addGameLoseListener(glViewListenerImplementation);
        mGame.addGamePlayListener(gpViewListenerImplementation);
        mGame.addGameWinListener(gwViewListenerImplementation);


    }


    private void createGame() {
        /*Get the intent extras created when the new game settings were selected in the NewGameActivity*/
        gameInfo = ((Activity) getContext()).getIntent().getExtras();
        assert gameInfo != null;
        int gameSize = gameInfo.getInt("gameSize");
        int colors = gameInfo.getInt("gameClrs");

        /*Init the Rect arrays that will hold the bounds for the game elements based on the game settings*/
        gridBounds = new RectF[gameSize][gameSize];
        buttonBounds = new RectF[colors];

        /*Creates a game with the selected settings*/
        mGame = new Game(gameSize, gameSize, colors);
        roundCounter.setText(String.format("0/%s", Integer.toString(mGame.getRoundLimit())));
    }

    private void setPaints() {
        /*Set the default theme*/
        int theme = 0;

        /*Gets the intent extra int passed from the themesActivity that repesents the theme to select*/
        if (themeSelection != null) {
            theme = themeSelection.getInt("themeSelection");
            System.out.println(theme);
        }

        /*Based on the selected theme value, load the corresponding resource array of hex values into a string
          array that will be used to set the colors of the Paints array*/
        String[] currentTheme;
        switch (theme) {
            case 0:
                currentTheme = getResources().getStringArray(R.array.theme_1);
                break;
            case 1:
                currentTheme = getResources().getStringArray(R.array.theme_2);
                break;
            case 2:
                currentTheme = getResources().getStringArray(R.array.theme_3);
                break;
            case 3:
                currentTheme = getResources().getStringArray(R.array.theme_4);
                break;
            default:
                currentTheme = getResources().getStringArray(R.array.theme_1);
                break;
        }

        /*Initialise the paint array elements*/
        for (int x = 0; x <= mGame.getColourCount(); x++) {
            paints[x] = new Paint(Paint.ANTI_ALIAS_FLAG);
            paints[x].setStyle(Paint.Style.FILL);
            paints[x].setColor(Color.parseColor(currentTheme[x]));
        }
    }

    private void setGridBounds() {
        /*Get the area of the screen that can be drawn to*/
        double diameterY = floor(getScreenWidth() / (mGame.getHeight() + (mGame.getHeight() + 1)));
        float availableWidth = (float) (mGame.getHeight() + 1) * (float) diameterY + (float) mGame.getHeight() * (float) diameterY;
        /*Calculates how much width of the screen each square of the grid can take up*/
        float cx = availableWidth / (float) mGame.getHeight();

        for (int col = 0; col < mGame.getHeight(); col++) {
            for (int row = 0; row < mGame.getHeight(); row++) {
                gridBounds[col][row] = new RectF();
                gridBounds[col][row].left = (cx * col) + getScreenWidth() / 70;
                gridBounds[col][row].top = (cx * row) + getScreenHeight() / 8;
                gridBounds[col][row].right = cx * (col + 1) + getScreenWidth() / 70;
                gridBounds[col][row].bottom = (cx * (row + 1)) + getScreenHeight() / 8;
            }
        }

        setButtonBounds(cx);

    }

    private void setButtonBounds(float cx) {
        float seperator = getScreenWidth() / 40; //the minimum separation between the color buttons in relation to the width of the screen
        float cWidth = (getScreenWidth() / mGame.getColourCount()) - (seperator / mGame.getColourCount()); //the space in which the buttons can take up on the screen with the given separation value.
        float yposTop = cx * (mGame.getHeight() + 2) + getScreenHeight() / 10;
        float yposBottom = cx * (mGame.getHeight() + 1) + getScreenHeight() / 2;
        float currentRight;

        for (int buttons = 0; buttons < mGame.getColourCount(); buttons++) {
            currentRight = cWidth * (buttons + 1);
            buttonBounds[buttons] = new RectF();
            buttonBounds[buttons].left = seperator;
            buttonBounds[buttons].top = yposTop;
            buttonBounds[buttons].right = currentRight;
            buttonBounds[buttons].bottom = yposBottom;

            seperator = seperator + cWidth; //adding the space the drawn button took up with the seperator to determine where the next button should be drawn
        }
    }

    public void onDraw(Canvas canvas) {
        /*set an on click listener that will listen to xml elements being interacted with*/
        this.setOnClickListener(this);

        /*draws the grid based on the set bounds*/
        for (int col = 0; col < mGame.getHeight(); col++) {
            for (int row = 0; row < mGame.getHeight(); row++) {
                canvas.drawRect(gridBounds[col][row], paints[mGame.getColor(col, row)]);
            }
        }

        /*draws the buttons based on the set bounds*/
        for (int buttons = 0; buttons < mGame.getColourCount(); buttons++) {
            if (extendButton) {
                /*draws an extended button if the button has been touched (detected by the onDown motionevent)*/
                if (buttons == buttonTouched) {
                    /*finds the button that has been touched and draws it extended*/
                    canvas.drawRect(buttonBounds[buttons].left, buttonBounds[buttons].top - 20, buttonBounds[buttons].right, buttonBounds[buttons].bottom, paints[buttons]);
                    buttons = -1;
                    extendButton = false;
                }
            } else {
                /*draws the button normally*/
                canvas.drawRect(buttonBounds[buttons], paints[buttons]);
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            float x = ev.getX();
            float y = ev.getY();

            /*Loop that loops through the button bounds to find bounds that contain the detected xy cords*/
            for (int button = 0; button < mGame.getColourCount(); button++) {
                if (buttonBounds[button].contains(x, y)) {
                    buttonTouched = button;
                    extendButton = true;
                    /*Invalidates the view redrawing the buttons with the selected button extended*/
                    invalidate();
                }
            }
            return true;
        }

        if (ev.getAction() == MotionEvent.ACTION_UP) {
            /*Get location of touch, calculate the color button that has been clicked.
              Call the playToken method of Game to make coloured piece appear.
              Use invalidate() to cause view to be redrawn. */
            extendButton = false;
            float xUp = ev.getX();
            float yUp = ev.getY();
            for (int button = 0; button < mGame.getColourCount(); button++) {
                if (buttonBounds[button].contains(xUp, yUp)) {
                    mGame.playColor(button);
                }
            }
            invalidate();
        }
        return super.onTouchEvent(ev);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.buttonReset) {
            /*calls init to create a new game*/
            init();
            /*re-draw the view*/
            invalidate();
        }
    }
}
