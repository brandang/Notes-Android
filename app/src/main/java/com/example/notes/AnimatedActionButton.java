package com.example.notes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A custom Floating Action Button that has a custom animation when it appears. Also has a method to
 * activate a click animation for when it is clicked.
 */
public class AnimatedActionButton extends FloatingActionButton {

    /**
     * Creates a new Action Button with the the given Context.
     * @param context The context.
     */
    public AnimatedActionButton(Context context) {
        super(context);
    }

    /**
     * Creates a new Action Button with the given info.
     * @param context The context.
     * @param attrs The attribute set.
     */
    public AnimatedActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Create a new Action button with the given info.
     * @param context The context.
     * @param attrs The attribute set.
     * @param defStyleAttr The style.
     */
    public AnimatedActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setOnClickListener (final View.OnClickListener l) {
        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimatedActionButton.this.startClickAnimation();
                l.onClick(view);
            }
        });
    }

    /**
     * Show the button by starting a fade in animation.
     */
    @SuppressLint("RestrictedApi")
    public void show() {
        Animation animation = this.getPopInAnim();
        this.startAnimation(animation);
        this.setVisibility(VISIBLE);
    }

    /**
     * Start the animation for when this button gets clicked.
     */
    private void startClickAnimation() {
        Animation animation = this.getButtonClickAnim();
        this.startAnimation(animation);
    }

    /**
     * Returns an animation to use for whenever the button is clicked.
     * @return The Animation.
     */
    private Animation getButtonClickAnim() {

        Animation jumpUp = AnimationUtils.loadAnimation(this.getContext(),
                R.anim.jump_up_anim);
        final Animation jumpDown = AnimationUtils.loadAnimation(this.getContext(),
                R.anim.jump_down_anim);

        jumpUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                return;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AnimatedActionButton.this.startAnimation(jumpDown);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                return;
            }
        });
        return jumpUp;
    }

    /**
     * Returns animation for use on the button to show up.
     * @return The Animation.
     */
    private Animation getPopInAnim() {
        Animation popUp = AnimationUtils.loadAnimation(this.getContext(), R.anim.pop_up_anim);
        final Animation attention = AnimationUtils.loadAnimation(this.getContext(),
                R.anim.attention_anim);
        popUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {return;}
            @Override
            public void onAnimationEnd(Animation animation) {
                AnimatedActionButton.this.startAnimation(attention);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {return;}
        });
        return popUp;
    }
}
