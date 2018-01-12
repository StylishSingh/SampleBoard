package com.sampleboard.utils;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;

/**
 * Created by ritesh.local on 2/4/2016.
 */
public abstract class CustomAnimationDrawableNew extends AnimationDrawable {

    /** Handles the animation callback. */
    Handler mAnimationHandler;

    public CustomAnimationDrawableNew(AnimationDrawable aniDrawable) {
        /* Add each frame to our animation drawable */
        for (int i = 0; i < aniDrawable.getNumberOfFrames(); i++) {
            this.addFrame(aniDrawable.getFrame(i), aniDrawable.getDuration(i));
        }
    }

    @Override
    public void start() {
        super.start();
        /*
         * Call super.start() to call the base class start animation method.
         * Then add a handler to call onAnimationFinish() when the total
         * duration for the animation has passed
         */
        mAnimationHandler = new Handler();
        onAnimtionStart();
        mAnimationHandler.postDelayed(new Runnable() {

            public void run() {
                onAnimationFinish();
            }
        }, getTotalDuration());

    }

    /**
     * Gets the total duration of all frames.
     *
     * @return The total duration.
     */
    public int getTotalDuration() {

        int iDuration = 0;

        for (int i = 0; i < this.getNumberOfFrames(); i++) {
            iDuration += this.getDuration(i);
        }

        return iDuration;
    }

    /**
     * Called when the animation finishes.
     */
    public abstract void onAnimationFinish();

    public abstract void onAnimtionStart();
}