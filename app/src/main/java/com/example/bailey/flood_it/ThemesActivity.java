package com.example.bailey.flood_it;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class ThemesActivity extends AppCompatActivity {

    RadioGroup themeGroup;
    RadioButton radioTheme1;
    RadioButton radioTheme2;
    RadioButton radioTheme3;
    RadioButton radioTheme4;
    ImageView themePreview;
    Button applyButton;

    Intent themesIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        themesIntent = new Intent(this, MainMenu.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_themes);
        setCurrentSelection();

        applyButton = findViewById(R.id.buttonApply);
        themeGroup = findViewById(R.id.radioThemeSelect);


        setThemePreviewListener();
        applyButton.setOnClickListener(applyButtonListener);

    }

    public void setCurrentSelection() {
        radioTheme1 = findViewById(R.id.radioTheme1);
        radioTheme2 = findViewById(R.id.radioTheme2);
        radioTheme3 = findViewById(R.id.radioTheme3);
        radioTheme4 = findViewById(R.id.radioTheme4);

        Bundle bundle;
        bundle = getIntent().getExtras();
        assert bundle != null;
        int currentTheme = bundle.getInt("themeSelection");

        //Set the radiobutton based upon the currently selected theme passed from the MainMenu
        switch (currentTheme) {
            case 0:
                radioTheme1.setChecked(true);
                break;
            case 1:
                radioTheme2.setChecked(true);
                break;
            case 2:
                radioTheme4.setChecked(true);
                break;
            case 3:
                radioTheme3.setChecked(true);
                break;
            default:
                radioTheme1.setChecked(true);
                break;
        }

    }

    public void setThemePreviewListener() {
        themePreview = findViewById(R.id.imagePreview);

        themeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedid) {

                int selectedThemeID = themeGroup.getCheckedRadioButtonId();
                System.out.println(selectedThemeID);
                /* Get the selected radiobutton and display it's corresponding theme preview (a preview of what
                  the grid will look like with the theme */
                switch (selectedThemeID) {
                    case R.id.radioTheme1:
                        themePreview.setImageResource(R.drawable.themepreview1);
                        break;
                    case R.id.radioTheme2:
                        themePreview.setImageResource(R.drawable.themepreview2);
                        break;
                    case R.id.radioTheme3:
                        themePreview.setImageResource(R.drawable.themepreview3);
                        break;
                    case R.id.radioTheme4:
                        themePreview.setImageResource(R.drawable.themepreview4);
                        break;
                }
            }
        });
    }


    private View.OnClickListener applyButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int selectedThemeID = themeGroup.getCheckedRadioButtonId();

            /*Gets the currently selected radioid when the apply button is pressed and puts extra
              data in an intent that represents the theme selected, this will be used in the GameView
              to draw the correct theme
             */
            switch(selectedThemeID) {
                case R.id.radioTheme1:
                    themesIntent.putExtra("themeSelection", 0);
                    break;
                case R.id.radioTheme2:
                    themesIntent.putExtra("themeSelection", 1);
                    break;
                case R.id.radioTheme3:
                    themesIntent.putExtra("themeSelection", 3);
                    break;
                case R.id.radioTheme4:
                    themesIntent.putExtra("themeSelection", 2);
                    break;
            }

            Toast themeConfirm = Toast.makeText(getApplicationContext(), "Theme Applied.", Toast.LENGTH_SHORT);
            themeConfirm.show();

            startActivity(themesIntent);
        }
    };
}

