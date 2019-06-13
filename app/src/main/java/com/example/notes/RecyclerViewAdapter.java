package com.example.notes;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.github.chrisbanes.photoview.PhotoView;

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
            TextArea area = (TextArea) LayoutInflater.from(parent.getContext()).inflate(R.layout.test, parent, false);
            area.setLines(1);
            area.setClickable(false);
            area.setBackground(parent.getResources().getDrawable(R.drawable.text_area_unselected, null));
            return new TextAreaViewHolder(area);
        } else {
            CustomPhotoView area = (CustomPhotoView) LayoutInflater.from(parent.getContext()).inflate(R.layout.test2, parent, false);
            return new PhotoViewHolder(area);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (this.getItemViewType(position) == ItemViewData.TYPE_TEXT) {
            TextAreaViewHolder holder1 = (TextAreaViewHolder) holder;
            holder1.setData(this.data.get(position).getData(), 16);
        } else {
            PhotoViewHolder holder2 = (PhotoViewHolder) holder;
            holder2.setImage(data.get(position).getData());
        }

    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        Log.d("Main", "Moved");
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
    public void onRowSelected(TextAreaViewHolder myViewHolder) {
        myViewHolder.setBackground(this.context.getResources().getDrawable(R.drawable.text_area_selected, null));
    }

    @Override
    public void onRowClear(TextAreaViewHolder myViewHolder) {
        myViewHolder.setBackground(this.context.getResources().getDrawable(R.drawable.text_area_unselected, null));

    }

    @Override
    public void onRowSelected(PhotoViewHolder myViewHolder) {
        Log.d("Main", "Selected");
    }

    @Override
    public void onRowClear(PhotoViewHolder myViewHolder) {
        Log.d("Main", "Cleared");
    }

}