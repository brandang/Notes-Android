package com.example.notes;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.widget.AppCompatButton;

/**
 * A custom Button that has custom animations for showing and hiding the Button.
 */
public class AnimatedButton extends AppCompatButton {

    /**
     * A custom Button that has custom animations for showing and hiding the Button.
     */
    public AnimatedButton(Context context) {
        super(context);
    }

    /**
     * A custom Button that has custom animations for showing and hiding the Button.
     */
    public AnimatedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * A custom Button that has custom animations for showing and hiding the Button.
     */
    public AnimatedButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Show the button using an Animation.
     */
    public void show() {
        this.setVisibility(VISIBLE);
        this.startAnimation(this.getShowAnim());
    }

    /**
     * Hide the button using an Animation.
     */
    public void hide() {
        this.startAnimation(this.getHideAnim());
    }

    /**
     * Get the Animation to show the Button.
     * @return The Animation.
     */
    private Animation getShowAnim() {
        return AnimationUtils.loadAnimation(this.getContext(), R.anim.show_button_anim);
    }

    /**
     * Get the Animation to hide the Button.
     * @return The Animation.
     */
    private Animation getHideAnim() {
        Animation anim = AnimationUtils.loadAnimation(this.getContext(), R.anim.hide_button_anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                return;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                return;
            }
        });
        return anim;
    }
}
