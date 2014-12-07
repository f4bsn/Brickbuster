package com.tumblr.fabfolio.brickbuster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;


public class MainActivity extends Activity {

    private static final String START_NEW_GAME = "START_NEW_GAME";
    private boolean startNewGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void resumeGame(View view) {
        startNewGame = false;
        Intent intent = new Intent (this, SpaceActivity.class);
        intent.putExtra(START_NEW_GAME, startNewGame);
        startActivity(intent);
    }

    public void newGame(View view) {
        startNewGame = true;
        Intent intent = new Intent (this, SpaceActivity.class);
        intent.putExtra(START_NEW_GAME, startNewGame);
        startActivity(intent);
    }
}
