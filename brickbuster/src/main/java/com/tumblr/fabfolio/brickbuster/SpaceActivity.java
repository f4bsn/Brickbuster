package com.tumblr.fabfolio.brickbuster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SpaceActivity extends Activity {

    private SpaceView spaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        boolean startNewGame = intent.getBooleanExtra("START_NEW_GAME", true);
        spaceView = new SpaceView(this, startNewGame);
        setContentView(spaceView);
    }

    @Override
    public void onPause() {
        super.onPause();
        spaceView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        spaceView.resume();
    }
}
