package com.tumblr.fabfolio.brickbuster;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

public class GameView extends SurfaceView implements Runnable {

    private static final int SPEED = 35;
    private static final int WAIT_FOR = 60;
    private static final int STARTING_LIVES = 3;

    private final Paint textPaint;
    private final Paint scorePaint;
    private final Paint livesPaint;
    private final String FILE_PATH = "data/data/com.tumblr.fabfolio.brickbuster/save.dat";
    private int points = 0;
    private int lives = 3;
    private Ball ball;
    private ArrayList<Brick> bricksList;
    private Bat bat;
    private Canvas canvas;
    private SurfaceHolder holder;
    private boolean running = false;
    private Thread thread = null;
    private float touchCoordX;
    private boolean touched = false;
    private boolean newGame = false;
    private boolean bricksCleared = true;
    private ObjectOutputStream oos;
    private boolean startNewGame = false;
    private int timeWaited = 0;
    private boolean gameOver = false;
    private int levelsFinished = 0;

    public GameView(Context context, boolean startNewGame) {
        super(context);

        this.startNewGame = startNewGame;

        holder = getHolder();
        newGame = false;

        bricksList = new ArrayList<Brick>();
        bat = new Bat(context);
        ball = new Ball(context);

        textPaint = new Paint();
        textPaint.setColor(Color.GRAY);
        textPaint.setTextSize(50);
        textPaint.setTextAlign(Paint.Align.CENTER);

        scorePaint = new Paint();
        scorePaint.setColor(Color.GRAY);
        scorePaint.setTextSize(80);

        livesPaint = new Paint();
        livesPaint.setColor(Color.GRAY);
        livesPaint.setTextSize(80);
        livesPaint.setTextAlign(Paint.Align.RIGHT);
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
        if (startNewGame) {
            initBricks(canvas);
        } else {
            restoreGameData();
        }
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

                if (bricksList.size() == 0) {
                    bricksCleared = true;
                    newGame = true;
                    levelsFinished++;
                }

                if (newGame) {
                    init();
                    timeWaited = 0;
                    newGame = false;
                    if(levelsFinished > 1) {
                        lives++;
                    }
                }
                timeWaited++;

                if (touched) bat.moveBat((int) touchCoordX);

                drawScene();
                physicsEngine();
                canvas.drawText(Integer.toString(points), 30, scorePaint.getTextSize(), scorePaint);
                canvas.drawText(Integer.toString(lives), canvas.getWidth()-30, livesPaint.getTextSize(), livesPaint);
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
        if (timeWaited > WAIT_FOR) {
            ball.checkBatCollision(bat);
            lives -= ball.collisionDetect();
            if( lives < 1) {
                gameOver();
                gameOver = true;
                return;
            }
            points += ball.checkBricksCollision(bricksList);
        } else {
            canvas.drawText("GET READY", canvas.getWidth() / 2, canvas.getHeight() / 3, textPaint);
            if (gameOver) {
                canvas.drawText("GAME OVER", canvas.getWidth() / 2,
                        canvas.getHeight() / 3 - textPaint.getTextSize(), textPaint);
            }
        }
    }

    private void gameOver() {
        points = 0;
        lives = STARTING_LIVES;
        levelsFinished = 0;
        bricksList.clear();
    }

    private void saveGameData() {
        ArrayList<int[]> brickSaveState = new ArrayList<int[]>();

        for (Brick brick : bricksList) {
            brickSaveState.add(brick.getBrickData());
        }

        try {
            FileOutputStream fos = new FileOutputStream(FILE_PATH);
            oos = new ObjectOutputStream(fos);
            oos.writeInt(points);
            oos.writeInt(lives);
            oos.writeObject(brickSaveState);
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void restoreBricks(ArrayList<int[]> brickSaveState) {
        for (int[] brickData : brickSaveState) {
            Rect r = new Rect();
            r.set(brickData[0], brickData[1], brickData[2], brickData[3]);
            Brick brick = new Brick(r, brickData[4]);
            bricksList.add(brick);
        }
    }

    private void restoreGameData() {
        try {
            FileInputStream fis = new FileInputStream(FILE_PATH);
            ObjectInputStream ois = new ObjectInputStream(fis);
            points = ois.readInt();
            lives = ois.readInt();
            ArrayList<int[]> brickSaveState = (ArrayList<int[]>) ois.readObject();
            restoreBricks(brickSaveState);
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        startNewGame = true;
    }

    public void pause() {
        saveGameData();
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