package com.example.bailey.flood_it;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainMenu extends AppCompatActivity {
    private int currentTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        if(savedInstanceState != null) {
            currentTheme = savedInstanceState.getInt("theme");
        }


        Bundle themeSelection = getIntent().getExtras();
        /*If a theme has been selected, receive it's extra to pass to the newGame and then the game
          view to draw the selected theme
         */
        if (themeSelection != null) {
            currentTheme = themeSelection.getInt("themeSelection");
        }


    }

    public void openGameActivity(View view) {

        Intent intent = new Intent(this, NewGameActivity.class);
        intent.putExtra("themeSelection", currentTheme);

        startActivity(intent);

    }

    public void openThemesActivity(View view) {
        Intent intent = new Intent(this, ThemesActivity.class);
        intent.putExtra("themeSelection", currentTheme);
        startActivity(intent);

    }

    public void openHighscoresActivity(View view) {
        Intent intent = new Intent(this, HighscoresActivity.class);
        startActivity(intent);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("theme", currentTheme);
    }

}
