package com.example.notes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

/**
 * A custom EditText that Overrides methods related to text size, allowing for easier usage.
 * Has a unique custom look. If added EnterKeyPressedListener, text after the cursor will be removed
 * and returned to the listener when user clicks enter. If added BackKeyPressedListener, text
 * after the cursor will be removed and returned to the listener when user clicks backspace.
 */
public class TextArea extends androidx.appcompat.widget.AppCompatEditText {

    final private static float LINE_WIDTH = 2.0f;

    final private static int MIN_LINES = 1;

    // Default color to use when drawing lines.
    final private static int DEFAULT_LINE_COLOR = Color.rgb(141, 141, 141);

    private int lineColor = DEFAULT_LINE_COLOR;

    // Listeners.
    private EnterKeyPressedListener enterListener;

    private BackKeyPressedListener backListener;

    private TextChangeListener textChangeListener;

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
        this.setupTextChangeListener();
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
        this.setupTextChangeListener();
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
        this.setupTextChangeListener();
    }

    /**
     * Set up a listener to watch for changes in the text.
     */
    private void setupTextChangeListener() {
        /*
        Attach listener. Note that we cannot use the below for backspace because it does not have
        a character change. Also note that we should only create the listener once because doing it
        multiply times costs resources and old listeners would not stop functioning, leading to
        bugs.
         */
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                return;
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (before == 0 && count == 1 && charSequence.charAt(start) == '\n') {
                    TextArea.this.onEnterPressed();
                } else {
                    TextArea.this.onTextChange();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                return;
            }
        });
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

        for (int i = 0; i <= numLines; i ++) {
            int y = this.getLineHeight() * (i) + paddingTop;
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

    /**
     * Set textChangeListener that listens in and gets notified whenever the text in TextArea gets
     * changed.
     * @param listener The textChangeListener that listens in.
     */
    public void setTextChangeListener(TextChangeListener listener) {
        this.textChangeListener = listener;
    }

    /**
     * Add a new EnterKeyPressedListener.
     * @param enterListener The listener to add.
     */
    public void setEnterListener(EnterKeyPressedListener enterListener) {
        this.enterListener = enterListener;
    }

    /**
     * Add a new BackKeyPressedListener.
     * @param backListener The listener to add.
     */
    public void setBackListener(BackKeyPressedListener backListener) {
        this.backListener = backListener;
    }

    /**
     * Notify listeners that the user just pressed enter.
     */
    private void onEnterPressed() {
        if (this.enterListener == null) {
            return;
        }
        this.enterListener.onEnterPressed(this.getSelectionStart(), this.getTextAfterCursor());
    }

    /**
     * Notify listeners that the user just pressed back button.
     */
    private void onBackPressed() {
        if (this.backListener == null) {
            return;
        }
        this.backListener.onBackPressed(this.getTextAfterCursor());
    }

    /**
     * Notify listeners that the next was just changed.
     */
    private void onTextChange() {
        if (this.textChangeListener == null)
            return;
        this.textChangeListener.onTextChanged(this.getText().toString());
    }

    /**
     * Returns the text after the current position of the cursor.
     * @return The text.
     */
    private String getTextAfterCursor() {
        int start = this.getSelectionStart();
        return this.getText().toString().substring(start);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new TextAreaInputConnection(super.onCreateInputConnection(outAttrs),
                true);
    }

    /**
     * Input connection that allows TextArea to notify listeners whenever a user pressed
     * enter or backspace.
     */
    private class TextAreaInputConnection extends InputConnectionWrapper {

        public TextAreaInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                TextArea.this.onBackPressed();
            }
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() ==
                    KeyEvent.KEYCODE_ENTER) {
                return false;
            }
            return super.sendKeyEvent(event);
        }
    }
}
