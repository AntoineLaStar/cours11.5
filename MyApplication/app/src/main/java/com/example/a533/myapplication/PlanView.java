package com.example.a533.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

public class PlanView extends View {

    private float zoomLevel = 1f;
    private float currX = 0;
    private float currY = 0;
    private List<PlanViewDisplayable> objectToDisplay;
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    public PlanView(Context context) {
        super(context);
        Init(context,null);
    }

    public PlanView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Init(context,attrs);
    }

    public PlanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context,attrs);
    }

    private void Init(Context context,AttributeSet set){
        objectToDisplay = new LinkedList<PlanViewDisplayable>();
        scaleGestureDetector = new ScaleGestureDetector(context,new ScaleListener());
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public void setOnLongClickListener(@Nullable OnLongClickListener l) {
        super.setOnLongClickListener(l);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint myPaint = new Paint();
        myPaint.setTextSize(72);
        canvas.drawText("Test1",10,100,myPaint);
        canvas.save();
        canvas.scale(zoomLevel,zoomLevel);
        displayObject(canvas);
        canvas.restore();
    }

    private void displayObject(Canvas canvas){
        for (PlanViewDisplayable  objectToDisplay: objectToDisplay){
            int positionImageX = (int)objectToDisplay.getPositionX();
            int positionImageY = (int)objectToDisplay.getPositionY();
            int imageToDisplayLeftPosition = 0;
            int imageToDisplayRightPosition = (int)objectToDisplay.getWidth();
            int imageToDisplayTopPosition = 0;
            int imageToDisplayBottomPosition = (int)objectToDisplay.getHeight();

            int imageWhereToDisplayLeftPosition = (int) (positionImageX - currX);
            int imageWhereToDisplayRightPosition = imageWhereToDisplayLeftPosition + imageToDisplayRightPosition - imageToDisplayLeftPosition;
            int imageWhereToDisplayTopPosition = (int) (positionImageY + currY);
            int imageWhereToDisplayBottomPosition = imageWhereToDisplayTopPosition + imageToDisplayBottomPosition - imageToDisplayTopPosition;

            canvas.drawBitmap(objectToDisplay.getBitmap(),
                    new Rect(imageToDisplayLeftPosition,imageToDisplayTopPosition,imageToDisplayRightPosition,imageToDisplayBottomPosition),
                    new Rect(imageWhereToDisplayLeftPosition,imageWhereToDisplayTopPosition,imageWhereToDisplayRightPosition,imageWhereToDisplayBottomPosition),null);
        }

    }
    public void addElementToDisplay(PlanViewDisplayable newObjectToDisplay){
        objectToDisplay.add(newObjectToDisplay);
        invalidate();
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            zoomLevel *= detector.getScaleFactor();
            zoomLevel = Math.max(0.1f, Math.min(zoomLevel,5.0f));
            invalidate();
            return true;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            currX += distanceX / zoomLevel;
            currY -= distanceY / zoomLevel;

            invalidate();
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Bitmap planBitMap = BitmapFactory.decodeResource(getResources(),R.drawable.marker);
            Marqueur marqueur = new Marqueur(planBitMap,currX + ((e.getX())  / zoomLevel - (planBitMap.getWidth()/2)) , ((e.getY()) / zoomLevel - planBitMap.getHeight()) - currY );
            addElementToDisplay(marqueur);
        }
    }
}