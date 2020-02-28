package com.projectc.mythicalmonstermatch;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.View;

public class CardAnimator {

    MainActivity gA;
    float height;
    float width;


    public CardAnimator(MainActivity gA){
        Display display = gA.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        height = size.y;
        width = size.x;
    }

    public ObjectAnimator createPlayerCardAnimation(View view){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationY", -(height/2));
        objectAnimator.setDuration(500);
        return objectAnimator;
    }

    public AnimationHolder createSingleEnemyCardAnimation(final View view){
        float x = view.getWidth();
        float y = view.getHeight();

        float newX = (float) (width*0.86);
        float newY = (float) (height*0.9);

        Log.d("ANISIZE", "" + (float) (height*0.9) + " " + (float) (height*0.85) +  " " + (float) (height*0.8));

        ObjectAnimator change_y_position = ObjectAnimator.ofFloat(view, "translationY", (float)(newX - x));
        ValueAnimator change_x = ValueAnimator.ofFloat(x, newX);
        ValueAnimator change_y = ValueAnimator.ofFloat(y, newY);

        change_y_position.setDuration(500);
        change_x.setDuration(500);
        change_y.setDuration(500);

        change_x.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float)animation.getAnimatedValue();
                view.getLayoutParams().width = (int)animatedValue;
                view.requestLayout();
            }
        });

        change_y.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float)animation.getAnimatedValue();
                view.getLayoutParams().height = (int) animatedValue;
                view.requestLayout();
            }
        });

        return new AnimationHolder(change_y_position, change_x, change_y);
    }

    /*public AnimationHolder createUpperEnemeyCardAnimation(View[] views, int directiond){
        ObjectAnimator objectAnimator = new ObjectAnimator();

        return new AnimationHolder(objectAnimator, objectAnimator, objectAnimator);
    }*/

}
