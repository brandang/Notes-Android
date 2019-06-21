package com.example.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A custom Adapter for a RecyclerView that can hold TextAreas and PhotoViews. Allows user to drag
 * and drop the various items to change their positions. Use this for the Re-order screen.
 * However, users can not edit the notes here.
 */
public class ReorderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        ItemMoveCallback.ItemTouchHelperContract, AddLineListener, RemoveLineListener {

    private ArrayList<ItemViewData> data;

    // The size at which to display the text.
    private int textSize = 12;

    private Context context;

    // Item position in which to request focus.
    private int focusPosition = 0;

    // Where to put the cursor once focused.
    private int focusCursor = 0;

    // RecyclerView that this adapter should be used for.
    private RecyclerView recyclerView;

    /**
     * Adapter for displaying TextAreas and PhotoViews.
     * @param context The Context.
     * @param data The data.
     * @param recyclerView The View that this adapter is used for.
     */
    public ReorderAdapter(Context context, ArrayList<ItemViewData> data,
                          RecyclerView recyclerView) {
        this.context = context;
        this.data = data;
        this.recyclerView = recyclerView;
        if (this.data == null) {
            this.data = new ArrayList<>(0);
        }
        this.notifyDataSetChanged();
    }

    /**
     * Adds new data to display to the top.
     * @param data The data.
     */
    public void addData(ItemViewData data) {
        // Add data to top.
        this.data.add(0, data);
        this.focusPosition = 0;
        this.notifyItemInserted(0);
        this.recyclerView.scrollToPosition(0);
    }

    /**
     * Completely removes the previous data and instead display this data.
     * @param saveData The data to display.
     */
    public void setDisplayData(SaveData saveData) {
        this.data.clear();
        BufferedReader reader = new BufferedReader(new StringReader(saveData.getText()));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                ItemViewData item = new ItemViewData(line, ItemViewData.TYPE_TEXT);
                this.data.add(item);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.focusPosition = 0;
        this.setTextSize(saveData.getFontSize());
        this.notifyDataSetChanged();
    }

    /**
     * Returns the Data that is currently being displayed.
     * @return The SaveData.
     */
    public SaveData getSaveData() {
        String dataString = "";

        for (int i = 0; i < this.getItemCount(); i ++) {
            dataString += this.data.get(i).getData();
            // Don't add an extra new line at the end.
            if (i < this.getItemCount() - 1)
                dataString += "\n";
        }
        return new SaveData(dataString, this.getTextSize());
    }

    /**
     * Sets the text size.
     * @param textSize The new text size.
     */
    public void setTextSize(int textSize) {
        this.textSize = textSize;
        this.notifyDataSetChanged();
    }

    /**
     * Get the size of the text being displayed.
     * @return The size of the text.
     */
    public int getTextSize() {
        return this.textSize;
    }

    @Override
    public int getItemViewType(int position) {
        return this.data.get(position).getViewType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ItemViewData.TYPE_TEXT) {

            TextArea textArea = (TextArea) LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.textarea, parent, false);
            // Disable editing.
            textArea.setFocusable(false);
            textArea.setClickable(false);
            textArea.setCursorVisible(false);
            textArea.setBackground(parent.getResources().getDrawable(
                    R.drawable.text_area_unselected, null));
            return new TextAreaHolder(textArea);

        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.recycler_imageview, parent, false);
            return new RecyclerImageViewHolder(view, parent.getContext());
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (this.getItemViewType(position) == ItemViewData.TYPE_TEXT) {
            TextAreaHolder textHolder = (TextAreaHolder) holder;
            textHolder.setData(this.data.get(position).getData(), this.textSize,
                    this.data.get(position),this, this);

            // Request focus at the right position.
            if (position == this.focusPosition) {
                textHolder.requestFocus(this.focusCursor);
            }
            // Don't forcefully open keyboard because it does not work.

        } else {
            RecyclerImageViewHolder imageHolder = (RecyclerImageViewHolder) holder;
            imageHolder.setImage(data.get(position).getData());
        }
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    @Override
    public void addLine(int position, String newLine) {
        // Add new item because user pressed enter, so move on to next line.
        this.data.add(position + 1, new ItemViewData(newLine, ItemViewData.TYPE_TEXT));
        this.focusPosition = position + 1;
        this.focusCursor = 0;
        this.notifyItemInserted(position + 1);
        this.notifyItemChanged(position);
        // Scroll to the line so that it does not appear offscreen.
        this.recyclerView.scrollToPosition(position + 1);
    }

    @Override
    public void removeLine(int position, String line) {
        // We cant remove line because this one is already at the top.
        if (position - 1 < 0)
            return;
        // Set new focus.
        this.focusPosition = position - 1;
        this.focusCursor = this.data.get(position - 1).getData().length();
        // Remove line and transfer data to line above it.
        this.data.remove(position);
        this.data.get(position - 1).appendData(line);
        this.notifyItemRemoved(position);
        this.notifyItemChanged(position - 1);
        // Scroll to the line so that it does not appear offscreen.
        this.recyclerView.scrollToPosition(position - 1);
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(data, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(data, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(TextAreaHolder viewHolder) {
        viewHolder.setBackground(this.context.getResources().getDrawable(
                R.drawable.text_area_selected, null));
    }

    @Override
    public void onRowClear(TextAreaHolder viewHolder) {
        viewHolder.setBackground(this.context.getResources().getDrawable(
                R.drawable.text_area_unselected, null));
    }

    @Override
    public void onRowSelected(RecyclerImageViewHolder viewHolder) {
        viewHolder.setBackgroundColor(this.context.getResources().getColor(R.color.colorAccent));
        viewHolder.onSelected();
    }

    @Override
    public void onRowClear(RecyclerImageViewHolder viewHolder) {
        viewHolder.setBackgroundColor(this.context.getResources().getColor(
                R.color.colorPrimaryLight));
        viewHolder.onClear();
    }
}