package com.example.notes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;

/**
 * A custom EditText that Overrides methods related to text size, allowing for easier usage.
 */
public class TextArea extends androidx.appcompat.widget.AppCompatEditText {

    final private static float LINE_WIDTH = 2.0f;

    final private static int MIN_LINES = 1;

    // Default color to use when drawing lines.
    final private static int DEFAULT_LINE_COLOR = Color.rgb(141, 141, 141);

    private int lineColor = DEFAULT_LINE_COLOR;

    // Paint object used to draw lines.
    private Paint paint;

    /**
     * A new textarea.
     * @param context The Context.
     */
    public TextArea(Context context) {
        super(context);
        this.setupPaint();
        this.setMinLines(MIN_LINES);
    }

    /**
     * A new textarea.
     * @param context The Context.
     * @param attrs The AttributeSet.
     */
    public TextArea(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setupPaint();
        this.setMinLines(MIN_LINES);
    }

    /**
     * A new textarea.
     * @param context The Context.
     * @param attrs The AttributeSet.
     * @param defStyleAttr The style attribute.
     */
    public TextArea(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setupPaint();
        this.setMinLines(MIN_LINES);
    }

    /**
     * Sets up the Paint object for use to draw lines on canvas.
     */
    private void setupPaint() {
        this.paint = new Paint();
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setColor(lineColor);
        this.paint.setStrokeWidth(TextArea.LINE_WIDTH);
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
        float sp = px / this.getResources().getDisplayMetrics().scaledDensity;
        return sp;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Draw lines.
        int startX = this.getLeft() + this.getPaddingLeft();
        int endX = this.getRight() - this.getPaddingRight();
        int paddingTop = this.getPaddingTop();
        int paddingBottom = this.getPaddingBottom();
        int numLines = (this.getHeight() - paddingTop - paddingBottom) / this.getLineHeight();

        for (int i = 0; i < numLines; i ++) {
            int y = this.getLineHeight() * (i + 1) + paddingTop;
            canvas.drawLine(startX, y, endX, y, this.paint);
        }

        super.onDraw(canvas);
    }

    /**
     * Sets the color to use to draw lines.
     * @param lineColor The color.
     */
    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
        this.setupPaint();
        this.invalidate();
    }
}
