package com.projectc.mythicalmonstermatch;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;

public class AnimationHolder {
    ObjectAnimator oA1 = null;
    ObjectAnimator oA2 = null;
    ValueAnimator vA1 = null;
    ValueAnimator vA2 = null;
    ObjectAnimator[] objectAnimators = null;
    ValueAnimator[] valueAnimators = null;
    int animationFlag = -1;
    public boolean gotPlayed = false;
    public AnimationHolder() {}

    public AnimationHolder(ObjectAnimator change_y_position){
        //oA1 = change_y_position;
        objectAnimators = new ObjectAnimator[]{change_y_position};
        animationFlag = 1;
    }

    public AnimationHolder(ValueAnimator[] valueAnimators) {
        this.valueAnimators = valueAnimators;
    }

    public AnimationHolder(ObjectAnimator[] objectAnimators, ValueAnimator[] valueAnimators){
        this.objectAnimators = objectAnimators;
        this.valueAnimators = valueAnimators;
        animationFlag = 2;
    }



    public void start(){
        switch (animationFlag){
            case 1:
                objectAnimators[0].start();
                break;
            /*case 2:
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
                break;*/
            default:
                for(ValueAnimator vA : valueAnimators){
                    vA.start();
                }
                for(ObjectAnimator oA : objectAnimators){
                    oA.start();
                }
                break;
        }
    }

    public void reverse(){
        switch (animationFlag){
            case 1:
                objectAnimators[0].reverse();
                break;
            default:
                for(ObjectAnimator oA : objectAnimators){
                    oA.reverse();
                }
                for(ValueAnimator vA : valueAnimators){
                    vA.reverse();
                }
                break;
        }
    }

    public ValueAnimator[] getValueAnimators(){
        return valueAnimators;
    }
    public ObjectAnimator[] getObjectAnimators() {return objectAnimators;}

}
