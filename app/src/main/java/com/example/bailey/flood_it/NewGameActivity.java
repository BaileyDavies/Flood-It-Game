package com.example.bailey.flood_it;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class NewGameActivity extends AppCompatActivity {

    private int currentTheme;
    private RadioGroup rGroupSize;
    private EditText editPlayerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        rGroupSize = findViewById(R.id.radiogroupSize);
        Button applyButton = findViewById(R.id.buttonApply);
        editPlayerName = findViewById(R.id.edittextPlayer);
        applyButton.setOnClickListener(radioListener);

        setDefaultRadio();
        getIntents();
    }

    private void setDefaultRadio() {
        rGroupSize.check(R.id.radiobuttonSize1);

    }


    private void getIntents() {
        Bundle themeSelection = getIntent().getExtras();
        if (themeSelection != null) {
            currentTheme = themeSelection.getInt("themeSelection");
        }

    }

    /*Method that returns the elements of a new Game based on the passed string difficulty value that is attached
      to the buttons on the NewGame activity*/
    private int[] translateDifficulty(String difficulty) {
        int[] results = new int[3];
        switch (difficulty) {
            case "Easy":
                results[0] = 12; //The size of the game
                results[1] = 5; //The amount of colors to be used
                results[2] = 0; //The numeric game mode to be used to store highscore
                return results;

            case "Medium":
                results[0] = 16;
                results[1] = 6;
                results[2] = 1;
                return results;

            case "Hard":
                results[0] = 18;
                results[1] = 7;
                results[2] = 2;
                return results;

            case "Extreme":
                results[0] = 24;
                results[1] = 9;
                results[2] = 3;
                return results;

            default:
                results[0] = 12;
                results[1] = 5;
                results[2] = 0;
                return results;

        }
    }

    private View.OnClickListener radioListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int selectedSizeId = rGroupSize.getCheckedRadioButtonId();
            RadioButton rButtonSize = findViewById(selectedSizeId);

           Intent gameInput = new Intent(NewGameActivity.this, GameActivity.class);

           int[] translatedSettings = translateDifficulty((String.valueOf(rButtonSize.getText())));
            //put the selected game settings in an intent
           gameInput.putExtra("gameSize",   translatedSettings[0]);
           gameInput.putExtra("gameClrs", translatedSettings[1]);
           gameInput.putExtra("gameMode", translatedSettings[2]);
           gameInput.putExtra("playerName", editPlayerName.getText().toString());
           gameInput.putExtra("themeSelection", currentTheme);

           startActivity(gameInput);

        }
    };


}
