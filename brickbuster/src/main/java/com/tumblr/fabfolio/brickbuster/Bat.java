package com.tumblr.fabfolio.brickbuster;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

public class Bat extends ShapeDrawable {

    private int leftBound;
    private int rightBound;
    private int upperBound;
    private int lowerBound;
    private int batWidth;
    private int batHeight;
    private int batOffset;
    private int batSpeed;

    private int canvas_width;
    private int canvas_height;

    public Bat(Context context) {
        super(new RectShape());
        this.getPaint().setColor(context.getResources().getColor(R.color.brick_red));
    }

    public void initCoordinates(int width, int height) {
        canvas_width = width;
        canvas_height = height;

        batWidth = canvas_width / 10;
        batHeight = canvas_width / 80;
        batOffset = canvas_height / 8;

        leftBound = (canvas_width / 2) - batWidth;
        rightBound = (canvas_width / 2) + batWidth;
        upperBound = (canvas_height - batOffset) - batHeight;
        lowerBound = (canvas_height - batOffset) + batHeight;

        batSpeed = canvas_width / 15;
    }

    public void drawBat(Canvas canvas) {
        this.setBounds(leftBound, upperBound, rightBound, lowerBound);
        this.draw(canvas);
    }

    public void moveBat(int x) {
        if (x >= leftBound && x <= rightBound) {
            leftBound = x - batWidth;
            rightBound = x + batWidth;
        } else if (x > rightBound) {
            leftBound += batSpeed;
            rightBound += batSpeed;
        } else if (x < leftBound) {
            leftBound -= batSpeed;
            rightBound -= batSpeed;
        }
        checkBorders();
    }

    private void checkBorders() {
        if (leftBound < 0) {
            leftBound = 0;
            rightBound = batWidth * 2;
        }
        if (rightBound > canvas_width) {
            rightBound = canvas_width;
            leftBound = canvas_width - (batWidth * 2);
        }
    }
}