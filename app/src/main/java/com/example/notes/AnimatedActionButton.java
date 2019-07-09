package com.example.notes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.core.view.ViewCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A custom Floating Action Button that has a custom animation when it appears. Also has a method to
 * optionally activate a click animation for when it is clicked. Overrides hide() method so that
 * it is guaranteed to work in all cases.
 */
public class AnimatedActionButton extends FloatingActionButton {

    // Whether or not to play an animation when clicked.
    private boolean enableAnimation = true;

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
        // Animation does not start when the View is not laid out, so force it to start by using
        // our own animation.
        if (ViewCompat.isLaidOut(this)) {
            this.setVisibility(INVISIBLE);
            super.show();
        } else {
            this.setVisibility(VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(this.getContext(),
                    R.anim.show_button_anim);
            this.startAnimation(animation);
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void hide() {
        this.setVisibility(VISIBLE);
        this.startAnimation(this.getHideAnim());
    }

    /**
     * Enable or disable animation showing up when the button is clicked.
     * @param enable True for yes, False for no.
     */
    public void enableClickAnimation(boolean enable) {
        this.enableAnimation = enable;
    }

    /**
     * Start the animation for when this button gets clicked. Does nothing if the animation is
     * disabled.
     */
    private void startClickAnimation() {
        if (!this.enableAnimation)
            return;
        Animation animation = this.getButtonClickAnim();
        this.startAnimation(animation);
    }

    /**
     * Start animation for button to show up and grab attention of user.
     */
    @SuppressLint("RestrictedApi")
    public void startShowAndFocusAnimation() {
        this.setVisibility(VISIBLE);
        this.startAnimation(this.getButtonShowAndFocusAnimation());
    }

    /**
     * Returns animation that makes button show up and then grabs attention of user.
     * @return The animation.
     */
    private Animation getButtonShowAndFocusAnimation() {
        Animation showAnim = AnimationUtils.loadAnimation(this.getContext(), R.anim.show_button_anim);
        final Animation scaleUp = AnimationUtils.loadAnimation(this.getContext(), R.anim.scale_up_anim);
        final Animation scaleDown = AnimationUtils.loadAnimation(this.getContext(), R.anim.scale_down_anim);

        showAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                return;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AnimatedActionButton.this.startAnimation(scaleUp);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                return;
            }
        });

        scaleUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                return;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AnimatedActionButton.this.startAnimation(scaleDown);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                return;
            }
        });
        return showAnim;
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
     * Returns Animation for Button to hide.
     * @return The Animation.
     */
    private Animation getHideAnim() {
        Animation hide = AnimationUtils.loadAnimation(this.getContext(), R.anim.hide_button_anim);
        hide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                return;
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onAnimationEnd(Animation animation) {
                AnimatedActionButton.this.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                return;
            }
        });
        return hide;
    }
}
