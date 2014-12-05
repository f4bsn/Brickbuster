package com.tumblr.fabfolio.brickbuster;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class SpaceView extends SurfaceView implements Runnable {

    private static final int SPEED = 35;
    private Ball ball;
    private ArrayList<Brick> bricksList;
    private Bat bat;
    private Canvas canvas;
    private SurfaceHolder holder;
    private boolean running = false;
    private Thread thread = null;
    private float touchCoordX;
    private boolean touched = false;
    private boolean newGame;

    public SpaceView(Context context) {
        super(context);

        holder = getHolder();
        newGame = true;

        bricksList = new ArrayList<Brick>();
        bat = new Bat(context);
        ball = new Ball(context);
    }

    private void initBricks(Canvas canvas) {
        int brickHeight = canvas.getWidth() / 36;
        int topOffset = canvas.getHeight() / 6;
        int brickWidth = (canvas.getWidth() / 10);

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 10; j++) {
                int y_coord = (i * brickHeight) + topOffset;
                int x_coord = j * (brickWidth);

                Rect rect = new Rect();
                rect.set(x_coord, y_coord, x_coord + brickWidth, y_coord + brickHeight);

                int color = 0;

                if (i < 1)
                    color = getResources().getColor(R.color.brick_red);
                else if (i < 2)
                    color = getResources().getColor(R.color.brick_orange);
                else if (i < 3)
                    color = getResources().getColor(R.color.brick_brown);
                else if (i < 4)
                    color = getResources().getColor(R.color.brick_yellow);
                else if (i < 5)
                    color = getResources().getColor(R.color.brick_green);
                else if (i < 6)
                    color = getResources().getColor(R.color.brick_blue);

                Brick brick = new Brick(rect, color);
                bricksList.add(brick);
            }
        }
    }

    private void init() {
        // set up starting coordinates
        touched = false;
        ball.initCoordinates(canvas.getWidth(), canvas.getHeight());
        initBricks(canvas);
        bat.initCoordinates(canvas.getWidth(), canvas.getHeight());
    }

    /**
     * GameLoop
     */
    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(SPEED);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (holder.getSurface().isValid()) {
                canvas = holder.lockCanvas();
                canvas.drawColor(Color.BLACK);

                if (newGame) {
                    init();
                    newGame = false;
                }

                if (touched) bat.moveBat((int) touchCoordX);

                drawScene();
                physicsEngine();
                holder.unlockCanvasAndPost(canvas);
            }
        }

    }

    private void drawScene() {
        for (Brick brick : bricksList) brick.drawBrick(canvas);
        bat.drawBat(canvas);
        ball.drawBall(canvas);
    }

    private void physicsEngine() {
        ball.checkBatCollision(bat);
        ball.collisionDetect();
        ball.checkBricksCollision(bricksList);
    }

    public void pause() {
        running = false;
        while (true) {
            try {
                thread.join();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
        thread = null;
    }

    public void resume() {
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN
                || event.getAction() == MotionEvent.ACTION_MOVE) {
            touchCoordX = event.getX();
            touched = true;
        }
        return touched;
    }
}