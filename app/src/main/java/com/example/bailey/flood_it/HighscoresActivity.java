package com.example.bailey.flood_it;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class HighscoresActivity extends AppCompatActivity {

    private ListView listView;
    private Button easyButton;
    private Button mediumButton;
    private Button hardButton;
    private Button extremeButton;
    private Integer buttonPressed = 0;
    ConstraintLayout themesLayout;


    ArrayAdapter<Score> adapter;
    ScoresDataSource datasource = new ScoresDataSource(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscores);

        /*Get the xml elements*/
        listView = findViewById(R.id.listHs);
        easyButton = findViewById(R.id.buttonEasy);
        mediumButton = findViewById(R.id.buttonMedium);
        hardButton = findViewById(R.id.buttonHard);
        extremeButton = findViewById(R.id.buttonExtreme);
        themesLayout = new ConstraintLayout(this);

        /*Opens a new ScoresDataSource to access it's methods*/
        datasource.open();
        getNewScore();

        adapter = new ArrayAdapter<Score>(this, android.R.layout.simple_list_item_2, android.R.id.text1, updateList()) {

            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                /*Overrides the list view layout to allow it's text views to be set*/
                View view = super.getView(position, convertView, parent);
                TextView text1 = view.findViewById(android.R.id.text1);
                TextView text2 = view.findViewById(android.R.id.text2);
                text1.setText("");
                text2.setText("");

                /*Sets the text views for each list view entry based upon the table loaded into the current list from the datasource*/
                if (position < updateList().size()) {
                    text1.setText(String.format("%s. %s", String.valueOf(position + 1), updateList().get(position).getName()));
                    text2.setText(String.format("Rounds Left: %s", String.valueOf(updateList().get(position).getScore())));
                    listView.invalidateViews();


                }

                /*Sets button listeners that are used to change the dataset and to the corresponding datasource table*/
                easyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        buttonPressed = 0;
                        adapter.notifyDataSetChanged();
                        listView.invalidate();


                    }
                });

                mediumButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        buttonPressed = 1;
                        adapter.notifyDataSetChanged();
                        listView.invalidate();

                    }
                });

                hardButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        buttonPressed = 2;
                        adapter.notifyDataSetChanged();
                        listView.invalidate();

                    }
                });

                extremeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        buttonPressed = 3;
                        adapter.notifyDataSetChanged();
                        listView.invalidate();

                    }
                });

                return view;
            }
        };

        listView.setAdapter(adapter);
    }

    /*Returns a list of scores based upon the buttonPressed (buttons that represent the difficulty)
    * the selected button loads the correct table from the datasource*/
    public List<Score> updateList() {
        return datasource.getAllScores(buttonPressed);
    }

    public void getNewScore() {
        /*Obtains the intent passed from the GameActivity that holds highscores*/
        Bundle highScore = getIntent().getExtras();
        if (highScore != null) {
            String pName = highScore.getString("playerName");
            System.out.println(pName);
            Integer pScore = highScore.getInt("playerScore");

            assert pName != null;
            if (pName.length() == 0) {
                pName = "Anonymous";
            }

            /*Creates a new score based upon the highscore values*/
            datasource.createScore(pName, pScore, highScore.getInt("gameMode"));
        }
    }
}
