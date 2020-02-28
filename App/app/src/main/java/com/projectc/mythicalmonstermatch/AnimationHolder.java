package com.projectc.mythicalmonstermatch;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;

public class AnimationHolder {
    ObjectAnimator oA1 = null;
    ValueAnimator vA1 = null;
    ValueAnimator vA2 = null;
    int animationFlag = -1;

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

    public void start(){
        if(animationFlag == 1){oA1.start();}
        else if(animationFlag == 2){
            oA1.start();
            vA1.start();
            vA2.start();
        }
    }

    public void reverse(){
        if(animationFlag == 1){oA1.reverse();}
        else if(animationFlag == 2) {
            oA1.reverse();
            vA1.reverse();
            vA2.reverse();
        }

    }

}
