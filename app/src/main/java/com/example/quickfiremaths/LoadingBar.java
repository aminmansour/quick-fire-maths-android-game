package com.example.quickfiremaths;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

/**
 * Created by Amans on 06/06/2017.
 */

public class LoadingBar implements Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {


    private final MainActivity activity;
    private final RelativeLayout loadingBar;
    private ValueAnimator animator;
    private int screenWidth;
    private boolean isAnswered;

    public LoadingBar(int screenWidth,MainActivity activity){
        this.screenWidth = screenWidth;
        this.activity = activity;
        loadingBar = (RelativeLayout)activity.findViewById(R.id.loadingBar);
        isAnswered = false;
    }

    public void load(long delay){
        animator = new ValueAnimator();
        animator.setDuration(1020);
        animator.setStartDelay(delay);
        animator.setFloatValues(0, screenWidth+40);
        animator.setInterpolator(new LinearInterpolator());
        animator.setTarget(this);
        animator.addUpdateListener(this);
        animator.addListener(this);
    }

    public void playTimer(){
        animator.start();
    }

    public void stopTimer(){
        isAnswered = true;
        if(animator!=null) {
            animator.end();
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if(!isAnswered) {
            activity.signalGameOver();
        }
        System.out.println("hello1234");
        isAnswered = false;
        loadingBar.setTranslationX(0);
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        loadingBar.setTranslationX((Float) animation.getAnimatedValue());
    }

    public void setPosition(float value){
        loadingBar.setTranslationX(value);
    }


    public void setIsFinished(boolean isFinished){
        this.isAnswered = isFinished;
    }
}
