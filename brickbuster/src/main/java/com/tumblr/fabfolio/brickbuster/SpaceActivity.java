package com.tumblr.fabfolio.brickbuster;

import android.app.Activity;
import android.os.Bundle;

public class SpaceActivity extends Activity {

    private SpaceView spaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spaceView = new SpaceView(this);
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
