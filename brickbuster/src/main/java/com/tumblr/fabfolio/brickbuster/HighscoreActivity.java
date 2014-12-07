package com.tumblr.fabfolio.brickbuster;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;


public class HighscoreActivity extends Activity {

    private static final String HIGHSCORE_PREF = "HIGHSCORE_PREF";
    private static final String FILE_PATH = "data/data/com.tumblr.fabfolio.brickbuster/save.dat";
    private int highscore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);
    }

    private int getHighscore() {
        int points = 0;

        FileInputStream fis;
        try {
            fis = new FileInputStream(FILE_PATH);
            ObjectInputStream ois = new ObjectInputStream(fis);
            points = ois.readInt();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return points;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences scoreSettings = getSharedPreferences(HIGHSCORE_PREF, 0);
        highscore = scoreSettings.getInt("highscore", 0);

        int currentScore = getHighscore();
        TextView highscores = (TextView) findViewById(R.id.highscores);
        if ( currentScore > highscore) {
            highscore = currentScore;
        }
        highscores.setText("Highscore: \n" + Integer.toString(highscore));
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences highScoreSave = getSharedPreferences(HIGHSCORE_PREF, 0);
        SharedPreferences.Editor scoreEditor = highScoreSave.edit();
        scoreEditor.putInt("highscore", highscore);
        scoreEditor.apply();
    }
}
