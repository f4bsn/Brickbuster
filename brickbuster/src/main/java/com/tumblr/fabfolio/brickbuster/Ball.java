package com.tumblr.fabfolio.brickbuster;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

import java.util.ArrayList;
import java.util.Random;

public class Ball extends ShapeDrawable {

    private static final int RESET_TIME = 1000;
    private int leftBound;
    private int rightBound;
    private int upperBound;
    private int lowerBound;
    private int size;
    private int velocityX;
    private int velocityY;
    private int canvas_width;
    private int canvas_height;
    private boolean batCollision;
    private boolean brickCollision;
    private Rect batRect;
    private Rect ballRect;
    private Context context;

    public Ball(Context context) {
        super(new RectShape());
        this.context = context;
        this.getPaint().setColor(context.getResources().getColor(R.color.brick_red));
    }

    public void initCoordinates(int width, int height) {
        batCollision = false;
        brickCollision = false;
        canvas_width = width;
        canvas_height = height;

        size = canvas_width / 72;
        velocityX = size / 2;
        velocityY = size;

        leftBound = (canvas_width / 2) - size;
        rightBound = (canvas_width / 2) + size;
        upperBound = (canvas_height / 2) - size;
        lowerBound = (canvas_height / 2) + size;

        int startDir = new Random().nextInt(2);
        if (startDir > 0) {
            velocityX = -velocityX;
        }
    }

    public void drawBall(Canvas canvas) {
        this.setBounds(leftBound, upperBound, rightBound, lowerBound);
        this.draw(canvas);
    }

    public int collisionDetect() {
        int bottomHit = 0;

        if (brickCollision) {
            velocityY = -velocityY;
            brickCollision = false; // reset
        }

        if (batCollision && velocityY > 0) {
            int paddleSplit = (batRect.right - batRect.left) / 4;
            int ballCenter = ballRect.centerX();
            if (ballCenter < batRect.left + paddleSplit) {
                velocityX = -(size * 3);
            } else if (ballCenter < batRect.left + (paddleSplit * 2)) {
                velocityX = -(size * 2);
            } else if (ballCenter < batRect.centerX() + paddleSplit) {
                velocityX = size * 2;
            } else {
                velocityX = size * 3;
            }
            velocityY = -velocityY;
        }

        if (this.getBounds().right >= canvas_width) {
            velocityX = -velocityX;
        } else if (this.getBounds().left <= 0) {
            this.setBounds(0, upperBound, size * 2, lowerBound);
            velocityX = -velocityX;
        }

        if (this.getBounds().top <= 0) {
            velocityY = -velocityY;
        } else if (this.getBounds().centerY() > canvas_height) {
            bottomHit = 1;
            try {
                Thread.sleep(RESET_TIME);
                initCoordinates(canvas_width, canvas_height);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        leftBound += velocityX;
        rightBound += velocityX;
        upperBound += velocityY;
        lowerBound += velocityY;

        return bottomHit;
    }

    public boolean checkBatCollision(Bat bat) {
        batRect = bat.getBounds();
        ballRect = this.getBounds();

        batCollision = batRect.intersect(ballRect);

        return batCollision;
    }

    public int checkBricksCollision(ArrayList<Brick> bricks) {
        int points = 0;
        int brickListLength = bricks.size();
        ballRect = this.getBounds();

        int ballLeft = ballRect.left + velocityX;
        int ballRight = ballRect.right + velocityY;
        int ballTop = ballRect.top + velocityY;
        int ballBottom = ballRect.bottom + velocityY;

        for (int i = brickListLength - 1; i >= 0; i--) {
            Rect brickRect = bricks.get(i).getBounds();

            int color = bricks.get(i).getBrickColor();

            if (ballLeft >= brickRect.left - (size * 2)
                    && ballLeft <= brickRect.right + (size * 2)
                    && (ballTop == brickRect.bottom || ballTop == brickRect.top)) {
                brickCollision = true;
                bricks.remove(i);
            } else if (ballRight <= brickRect.right
                    && ballRight >= brickRect.left
                    && ballTop <= brickRect.bottom && ballTop >= brickRect.top) {
                brickCollision = true;
                bricks.remove(i);
            } else if (ballLeft >= brickRect.left
                    && ballLeft <= brickRect.right
                    && ballBottom <= brickRect.bottom
                    && ballBottom >= brickRect.top) {
                brickCollision = true;
                bricks.remove(i);
            } else if (ballRight <= brickRect.right
                    && ballRight >= brickRect.left
                    && ballBottom <= brickRect.bottom
                    && ballBottom >= brickRect.top) {
                brickCollision = true;
                bricks.remove(i);
            }

            if (brickCollision) {
                return points += getPoints(color);
            }
        }
        return points;
    }

    private int getPoints(int color) {
        int points = 0;
        if (color == context.getResources().getColor(R.color.brick_blue))
            points = 1;
        else if (color == context.getResources().getColor(R.color.brick_green))
            points = 2;
        else if (color == context.getResources().getColor(R.color.brick_yellow))
            points = 3;
        else if (color == context.getResources().getColor(R.color.brick_brown))
            points = 4;
        else if (color == context.getResources().getColor(R.color.brick_orange))
            points = 5;
        else if (color == context.getResources().getColor(R.color.brick_red))
            points = 6;

        return points;
    }
}