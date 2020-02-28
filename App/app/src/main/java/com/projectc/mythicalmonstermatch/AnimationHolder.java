package com.projectc.mythicalmonstermatch;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;

public class AnimationHolder {
    ObjectAnimator oA1 = null;
    ObjectAnimator oA2 = null;
    ValueAnimator vA1 = null;
    ValueAnimator vA2 = null;
    int animationFlag = -1;
    public AnimationHolder() {

    }

    public AnimationHolder(ObjectAnimator change_y_position){
        oA1 = change_y_position;
        animationFlag = 1;
    }

    public AnimationHolder(ObjectAnimator change_y_position, ValueAnimator change_x, ValueAnimator change_y){
        oA1 = change_y_position;
        vA1 = change_x;
        vA2 = change_y;
        animationFlag = 2;
    }

    public AnimationHolder(ObjectAnimator change_x_position, ObjectAnimator change_y_position, ValueAnimator change_x, ValueAnimator change_y) {
        oA1 = change_x_position;
        oA2 = change_y_position;
        vA1 = change_x;
        vA2 = change_y;
        animationFlag = 3;
    }


    public void start(){
        switch (animationFlag){
            case 1:
                oA1.start();
                break;
            case 2:
                oA1.start();
                vA1.start();
                vA2.start();
                break;
            case 3:
                oA1.start();
                oA2.start();
                vA1.start();
                vA2.start();
                break;
            case 4:
                break;
        }
    }

    public void reverse(){
        switch (animationFlag){
            case 1:
                oA1.reverse();
                break;
            case 2:
                oA1.reverse();
                vA1.reverse();
                vA2.reverse();
                break;
            case 3:
                oA1.reverse();
                oA2.reverse();
                vA1.reverse();
                vA2.reverse();
                break;
            case 4:
                break;
        }
    }

}
