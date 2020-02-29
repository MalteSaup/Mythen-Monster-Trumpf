package com.projectc.mythicalmonstermatch;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

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

    public AnimationHolder createThreeEnemyCardAnimation(final View view, int direction){            //0 = Unten 1 = Rechts, 2 = Links
        float y = view.getHeight();
        float newY = (float) (height*0.9);

        float dir_y = (newY - y) / 2 - height * 0.25f;

        ImageView imageView = view.findViewById(R.id.imageView);
        TextView cardName = view.findViewById(R.id.cardName);
        TableLayout tableLayout = view.findViewById(R.id.tableLayout);

        ValueAnimator[] valueAnimators = createResizeAnimation(view).getValueAnimators();

        ObjectAnimator change_x_position = null;
        ObjectAnimator change_y_position = null;
        ObjectAnimator change_image = null;
        ObjectAnimator change_cardName = null;
        ObjectAnimator change_table = null;

        ObjectAnimator[] objectAnimators = null;

        Log.d("HEIGHT", "" + height);
        change_image = ObjectAnimator.ofFloat(imageView, "translationY", height * 0.055f);
        change_cardName = ObjectAnimator.ofFloat(cardName, "translationY", height * 0.8f);      //TODO
        change_table = ObjectAnimator.ofFloat(cardName, "translationY", height * 0.055f);

        change_image.setDuration(500);
        change_cardName.setDuration(500);
        change_table.setDuration(500);



        switch (direction){
            case 0:
                change_y_position = ObjectAnimator.ofFloat(view, "translationY", (newY - y) / 2);
                change_y_position.setDuration(500);
                objectAnimators = new ObjectAnimator[]{change_y_position, change_cardName, change_image, change_table};
                break;
            default:
                change_y_position = ObjectAnimator.ofFloat(view, "translationY", dir_y);
                change_x_position = ObjectAnimator.ofFloat(view, "translationX", (float)(width*0.23*Math.pow(-1,direction+1)));
                change_x_position.setDuration(500);
                change_y_position.setDuration(500);
                objectAnimators = new ObjectAnimator[]{change_x_position, change_y_position, change_cardName, change_image, change_table};
                break;
        }
        return new AnimationHolder(objectAnimators, valueAnimators);
    }

    public AnimationHolder createResizeAnimation(final View view){
        final ImageView imageView = view.findViewById(R.id.imageView);
        final TextView cardName = view.findViewById(R.id.cardName);
        final TableLayout tableLayout = view.findViewById(R.id.tableLayout);

        float x = view.getWidth();
        float y = view.getHeight();

        float newX = (float) (width*0.86);
        float newY = (float) (height*0.9);

        float image_y = imageView.getHeight();
        float name_y = cardName.getHeight();
        float table_y = tableLayout.getHeight();

        ValueAnimator change_x = ValueAnimator.ofFloat(x, newX);
        ValueAnimator change_y = ValueAnimator.ofFloat(y, newY);
        ValueAnimator change_img_y = ValueAnimator.ofFloat(image_y, (float)(height*0.6));
        ValueAnimator change_name_y = ValueAnimator.ofFloat(name_y, (float)(height*0.26));
        ValueAnimator change_table_y = ValueAnimator.ofFloat(table_y, (float)(height*0.12));

        ValueAnimator[] valueAnimators = new ValueAnimator[]{
                change_x,
                change_y,
                change_name_y,
                change_img_y,
                change_table_y
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
                view.getLayoutParams().height = (int) animatedValue;
                view.requestLayout();
            }
        });
        change_img_y.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float)animation.getAnimatedValue();
                imageView.getLayoutParams().height = (int) animatedValue;
                imageView.requestLayout();
            }
        });
        change_name_y.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float)animation.getAnimatedValue();
                cardName.getLayoutParams().height = (int) animatedValue;
                cardName.requestLayout();
            }
        });
        change_table_y.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float)animation.getAnimatedValue();
                tableLayout.getLayoutParams().height = (int) animatedValue;
                tableLayout.requestLayout();
            }
        });

        return new AnimationHolder(valueAnimators);
    }

}
