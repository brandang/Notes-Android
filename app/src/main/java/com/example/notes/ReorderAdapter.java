package com.example.notes;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A custom Adapter for a RecyclerView that can hold TextAreas and PhotoViews. Allows user to drag
 * and drop the various items to change their positions. Use this for the Re-order screen.
 * However, users can not edit the notes here.
 */
public class ReorderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        ItemMoveCallback.ItemTouchHelperContract, AddLineListener, RemoveLineListener {

    // How long to wait until an item is removed.
    final private static long REMOVAL_DELAY = 2500;

    private ArrayList<ItemData> data;

    // The size at which to display the text.
    private int textSize = 12;

    private Context context;

    // RecyclerView that this adapter should be used for.
    private RecyclerView recyclerView;

    private SnackbarDisplayer displayer;

    /**
     * Adapter for displaying TextAreas and PhotoViews.
     * @param context The Context.
     * @param data The data.
     * @param recyclerView The View that this adapter is used for.
     * @param displayer The SnackbarDisplayer to use to show Snackbars.
     */
    public ReorderAdapter(Context context, ArrayList<ItemData> data,
                          RecyclerView recyclerView, SnackbarDisplayer displayer) {
        this.context = context;
        this.data = data;
        this.recyclerView = recyclerView;
        if (this.data == null) {
            this.data = new ArrayList<>(0);
        }
        this.displayer = displayer;
        this.notifyDataSetChanged();
    }

    /**
     * Adds new data to display to the top.
     * @param data The data.
     */
    public void addData(ItemData data) {
        // Add data to top.
        this.addData(data, 0);
    }

    /**
     * Adds the specified data to the position.
     * @param data The data.
     * @param position The position.
     */
    private void addData(ItemData data, int position) {
        if (position > this.getItemCount())
            position = this.getItemCount();
        this.data.add(position, data);
        this.notifyItemInserted(position);
        this.recyclerView.scrollToPosition(position);
    }

    /**
     * Completely removes the previous data and instead display this data.
     * @param saveData The data to display.
     */
    public void setDisplayData(SaveData saveData) {
        this.data.clear();
        this.data = saveData.getData();
        this.setTextSize(saveData.getFontSize());
        this.notifyDataSetChanged();
    }

    /**
     * Returns the Data that is currently being displayed.
     * @return The SaveData.
     */
    public SaveData getSaveData() {
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
            // Prevent user from editing.
            textArea.setInputType(InputType.TYPE_NULL);
            textArea.setFocusable(false);
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
        } else {
            RecyclerImageViewHolder imageHolder = (RecyclerImageViewHolder) holder;
            imageHolder.setImage(null);
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
        this.notifyItemInserted(position + 1);
        this.notifyItemChanged(position);
    }

    @Override
    public void removeLine(int position, String line) {
        // We cant remove line because this one is already at the top.
        if (position - 1 < 0)
            return;
        // Remove line and transfer data to line above it.
        this.data.remove(position);
        this.data.get(position - 1).appendData(line);
        this.notifyItemRemoved(position);
        this.notifyItemChanged(position - 1);
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
    public void onRowSwiped(final int position) {
        final ItemData item = this.data.get(position);
        this.remove(position);

        // Show Snackbar with the option of adding the item back.
        String msg;
        if (item.getViewType() == ItemData.TYPE_TEXT)
            msg = this.context.getResources().getString(R.string.text_item_delete_msg);
        else
            msg = this.context.getResources().getString(R.string.image_item_delete_msg);

        String undo = this.context.getResources().getString(R.string.undo_delete_msg);
        int length = Snackbar.LENGTH_LONG;
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReorderAdapter.this.addData(item, position);
            }
        };
        this.displayer.showSnackbar(msg, undo, length, listener);
    }

    /**
     * Removes item at the position.
     * @param position The position to be removed.
     */
    private void remove(int position) {
        this.data.remove(position);
        this.notifyItemRemoved(position);
    }
}