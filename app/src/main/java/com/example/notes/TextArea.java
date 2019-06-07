package com.example.notes;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;

/**
 * A custom EditText that Overrides methods related to text size, allowing for easier usage.
 */
public class TextArea extends androidx.appcompat.widget.AppCompatEditText {

    /**
     * A new TextArea.
     * @param context The Context.
     */
    public TextArea(Context context) {
        super(context);
    }

    /**
     * A new TextArea.
     * @param context The Context.
     * @param attrs The AttributeSet.
     */
    public TextArea(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * A new TextArea.
     * @param context The Context.
     * @param attrs The AttributeSet.
     * @param defStyleAttr The style attribute.
     */
    public TextArea(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Sets the text size in scaled pixels.
     * @param size The text size.
     */
    public void setTextSize(float size) {
        super.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    /**
     * Returns the text size, in units of scaled pixels.
     * @return The text size.
     */
    public float getTextSize() {
        // Calculate text size in scaled pixels.
        float px = super.getTextSize();
        float sp = px / getResources().getDisplayMetrics().scaledDensity;
        return sp;
    }
}
