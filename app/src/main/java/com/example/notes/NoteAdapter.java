package com.example.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A custom Adapter for a RecyclerView that can hold TextAreas and PhotoViews. Acts like a ListView.
 * Does not allow user to drag and drop. However, allows users to edit the notes.
 */
public class NoteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        ItemMoveCallback.ItemTouchHelperContract, AddLineListener, RemoveLineListener {

    private ArrayList<ItemData> data;

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
     * @param data The SaveData. If it is null, nothing will be displayed.
     * @param recyclerView The View that this adapter is used for.
     */
    public NoteAdapter(Context context, SaveData data,
                          RecyclerView recyclerView) {
        this.context = context;
        if (data == null)
            this.data = new ArrayList<>();
        else {
            this.data = data.getData();
            this.setTextSize(data.getFontSize());
        }
        this.recyclerView = recyclerView;
        this.notifyDataSetChanged();
    }

    /**
     * Adds new data to display to the top.
     * @param data The data.
     */
    public void addData(ItemData data) {
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
//        this.data.clear();
        this.data = saveData.getData();
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
            dataString += "\n";

            // We have reached the end.
            if (i == this.getItemCount() - 1) {

                /*
                If the last line is empty, must insert a extra new line character. This is because
                BufferedReader will ignore any instances of the last line being empty. Thus, we must
                work around this by inserting an extra new line.
                 */
                if (this.data.get(i).getData().equals(""))
                    dataString += "\n";
            }
        }
        return new SaveData(this.data, this.getTextSize());
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
        if (viewType == ItemData.TYPE_TEXT) {

            TextArea textArea = (TextArea) LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.textarea, parent, false);
            textArea.setClickable(false);
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
        if (this.getItemViewType(position) == ItemData.TYPE_TEXT) {
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
        this.data.add(position + 1, new ItemData(newLine, ItemData.TYPE_TEXT));
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

    @Override
    public void onRowSwiped(int position) {
        return;
    }
}