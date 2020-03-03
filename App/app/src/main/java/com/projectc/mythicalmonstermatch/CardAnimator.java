package com.projectc.mythicalmonstermatch;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.View;

public class CardAnimator {

    float height;
    float width;
    boolean flipped = false;
    boolean changed = false;

    boolean fragChanged = false;
    boolean fragDirection = false;


    public CardAnimator(MainActivity gA){
        Display display = gA.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        height = size.y;
        width = size.x;
    }

    public AnimationHolder createPlayerCardAnimation(View view){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationY", -(height/2));
        objectAnimator.setDuration(500);
        return new AnimationHolder(objectAnimator);
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

        return new AnimationHolder(new ObjectAnimator[]{change_y_position}, new ValueAnimator[]{change_x, change_y});
    }

    public AnimationHolder createTwoEnemyCardAnimation(final View view, int direction){            //1 = Links, 2 = Rechts
        float x = view.getWidth();
        float y = view.getHeight();

        float newX = (float) (width*0.86);
        float newY = (float) (height*0.9);

        ObjectAnimator change_y_position = ObjectAnimator.ofFloat(view, "translationY", (newY - y) / 2);
        ObjectAnimator change_x_position = ObjectAnimator.ofFloat(view, "translationX", (float)(width*0.22*Math.pow(-1,direction)));

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
        return new AnimationHolder(new ObjectAnimator[]{change_x_position, change_y_position}, new ValueAnimator[]{change_x, change_y});
    }

    public AnimationHolder createThreeEnemyCardAnimation(final View view, int direction, boolean anim){            //0 = Unten 1 = Rechts, 2 = Links
        float y = view.getHeight();
        float newY = (float) (height*0.9);

        float dir_y = (newY - y) / 2 - height * 0.25f;

        ValueAnimator[] valueAnimators = createResizeAnimation(view, anim).getValueAnimators();

        ObjectAnimator change_x_position = null;
        ObjectAnimator change_y_position = null;

        ObjectAnimator[] objectAnimators = null;

        switch (direction){
            case 0:
                change_y_position = ObjectAnimator.ofFloat(view, "translationY", (newY - y) / 2);
                change_y_position.setDuration(500);
                objectAnimators = new ObjectAnimator[]{change_y_position};
                break;
            default:
                change_y_position = ObjectAnimator.ofFloat(view, "translationY", dir_y);
                change_x_position = ObjectAnimator.ofFloat(view, "translationX", (float)(width*0.23*Math.pow(-1,direction+1)));
                change_x_position.setDuration(500);
                change_y_position.setDuration(500);
                objectAnimators = new ObjectAnimator[]{change_x_position, change_y_position};
                break;
        }
        return new AnimationHolder(objectAnimators, valueAnimators);
    }

    public AnimationHolder createResizeAnimation(final View view, final boolean anim){
        float x = view.getWidth();
        final float y = view.getHeight();

        float newX = (float) (width*0.86);
        final float newY = (float) (height*0.9);

        final float diffY = newY - y;

        ValueAnimator change_x = ValueAnimator.ofFloat(x, newX);
        ValueAnimator change_y = ValueAnimator.ofFloat(y, newY);

        ValueAnimator[] valueAnimators = new ValueAnimator[]{
                change_x,
                change_y
        };

        for(ValueAnimator valueAnimator : valueAnimators){
            valueAnimator.setDuration(500);
        }

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
                if(anim){Log.d("ENEMYANIM", "" + animatedValue+ "Y: " + y + " NEW Y: "+ newY + " DIFFY: " + diffY + "_ : " + fragChanged); }
                if(!fragDirection){
                    //Log.d("ENEMYANIM", "" + anim + " " + (animatedValue-y > diffY *0.5));
                    if(animatedValue-y > diffY * 0.015f && !fragChanged && animatedValue < newY){
                        Log.d("ENEMYANIM", "" + anim + " " +fragChanged + " " + fragDirection);
                        if(anim){
                            //view.setVisibility(View.VISIBLE);
                            view.setAlpha(1.0f);
                            Log.d("ENEMYANIM", "ANIM VISIBLE");
                            fragChanged = true;
                        }
                        else{
                            //view.setVisibility(View.GONE);
                            view.setAlpha(0.0f);
                            Log.d("ENEMYANIM", "ANIM GONE");
                            //fragChanged =true;
                        }

                    }else if(animatedValue >= newY){
                        fragDirection = true;
                        fragChanged = false;
                    }
                }else{
                    if(animatedValue-y < diffY * 0.02f && !fragChanged && animatedValue > y){
                        if(!anim){
                            //view.setVisibility(View.VISIBLE);
                            view.setAlpha(1.0f);
                            Log.d("ENEMYANIM", "ANIM VISIBLE2");
                        }
                        else{
                            //view.setVisibility(View.GONE);
                            view.setAlpha(0.0f);
                            Log.d("ENEMYANIM", "ANIM GONE2");
                            fragChanged = true;
                        }

                    }else if(animatedValue <= y){
                        fragDirection = false;
                        fragChanged = false;
                    }
                }
                view.getLayoutParams().height = (int) animatedValue;
                view.requestLayout();
            }
        });

        return new AnimationHolder(valueAnimators);
    }
}
