package com.example.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A custom Adapter for a RecyclerView that can hold TextAreas and PhotoViews.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        ItemMoveCallback.ItemTouchHelperContract {

    private ArrayList<ItemViewData> data;

    private Context context;

    /**
     * Adapter for displaying TextAreas and PhotoViews.
     * @param context The Context.
     * @param data The data.
     */
    public RecyclerViewAdapter(Context context, ArrayList<ItemViewData> data) {
        this.context = context;
        this.data = data;
    }

    public void addData(ItemViewData data) {
        // Add data to top.
        this.data.add(0, data);
        this.notifyDataSetChanged();
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
            textArea.setLines(1);
            textArea.setClickable(false);
            textArea.setBackground(parent.getResources().getDrawable(
                    R.drawable.text_area_unselected, null));
            return new TextAreaHolder(textArea);

        } else {
            View view = LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.recycler_imageview, parent, false);

            return new RecyclerImageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (this.getItemViewType(position) == ItemViewData.TYPE_TEXT) {
            TextAreaHolder holder1 = (TextAreaHolder) holder;
            holder1.setData(this.data.get(position).getData(), 16);
        } else {
            RecyclerImageViewHolder holder2 = (RecyclerImageViewHolder) holder;
            holder2.setImage(data.get(position).getData());
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
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
        viewHolder.setBackground(this.context.getResources().getDrawable(R.drawable.text_area_selected, null));
    }

    @Override
    public void onRowClear(TextAreaHolder viewHolder) {
        viewHolder.setBackground(this.context.getResources().getDrawable(R.drawable.text_area_unselected, null));
    }

    @Override
    public void onRowSelected(RecyclerImageViewHolder viewHolder) {
        viewHolder.setBackgroundColor(this.context.getResources().getColor(R.color.colorAccent));
    }

    @Override
    public void onRowClear(RecyclerImageViewHolder viewHolder) {
        viewHolder.setBackgroundColor(this.context.getResources().getColor(R.color.colorPrimaryLight));
    }

}