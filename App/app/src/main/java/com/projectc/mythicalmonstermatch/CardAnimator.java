package com.projectc.mythicalmonstermatch;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Point;
import android.view.Display;
import android.view.View;

public class CardAnimator {

    float height;
    float width;
    boolean flipped = false;
    boolean changed = false;


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

    public AnimationHolder createCardFlip(final View view){
        final ObjectAnimator rotationY = ObjectAnimator.ofFloat(view, "rotationY", 180.0f);
        rotationY.setDuration(500);
        rotationY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float deg = (float) animation.getAnimatedValue();
                if(!flipped){
                    if(deg > 90 && !changed && deg < 180){
                        view.findViewById(R.id.background).setVisibility(View.VISIBLE);
                        changed = true;
                    }else if(deg >= 180){
                        flipped = true;
                        changed = false;
                    }
                }else{
                    if(deg < 90 && !changed && deg > 0){
                        view.findViewById(R.id.background).setVisibility(View.GONE);
                        changed = true;
                    }else if(deg <= 0){
                        flipped = false;
                        changed = false;
                    }
                }
            }
        });
        return new AnimationHolder(rotationY);
    }

    public AnimationHolder createSingleEnemyCardAnimation(final View view){
        float x = view.getWidth();
        float y = view.getHeight();

        float newX = (float) (width*0.86);
        float newY = (float) (height*0.9);

        ObjectAnimator change_y_position = ObjectAnimator.ofFloat(view, "translationY", (newY - y)/2);
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

    public AnimationHolder createTwoEnemyCardAnimation(final View view, int direction){            //1 = Links, 2 = Rechts
        float x = view.getWidth();
        float y = view.getHeight();

        float newX = (float) (width*0.86);
        float newY = (float) (height*0.9);

        ObjectAnimator change_y_position = ObjectAnimator.ofFloat(view, "translationY", (newY - y) / 2);
        ObjectAnimator change_x_position = ObjectAnimator.ofFloat(view, "translationX", (float)(y*0.3*Math.pow(-1,direction)));

        ValueAnimator change_x = ValueAnimator.ofFloat(x, newX);
        ValueAnimator change_y = ValueAnimator.ofFloat(y, newY);

        change_x_position.setDuration(500);
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

        return new AnimationHolder(change_x_position, change_y_position, change_x, change_y);
    }

}
