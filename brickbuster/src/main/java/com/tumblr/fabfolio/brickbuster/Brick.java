package com.tumblr.fabfolio.brickbuster;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

public class Brick extends ShapeDrawable {

    private Paint paint;
    private int brickColor;

    public Brick(Rect rect, int color) {
        super(new RectShape());
        this.setBounds(rect);
        paint = new Paint();
        paint.setColor(color);
        brickColor = color;
    }

    public void drawBrick(Canvas canvas) {
        canvas.drawRect(this.getBounds(), paint);
    }

    public int[] getBrickData() {
        return new int[]{this.getBounds().left, this.getBounds().top,
                this.getBounds().right, this.getBounds().bottom, brickColor};
    }

    public int getBrickColor () {
        return brickColor;
    }
}