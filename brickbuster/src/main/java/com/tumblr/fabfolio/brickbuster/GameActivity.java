package com.tumblr.fabfolio.brickbuster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class GameActivity extends Activity {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        boolean startNewGame = intent.getBooleanExtra("START_NEW_GAME", true);
        gameView = new GameView(this, startNewGame);
        setContentView(gameView);
    }

    @Override
    public void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }
}
