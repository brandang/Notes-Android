package com.example.notes;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemMoveCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperContract adapter;

    public ItemMoveCallback(ItemTouchHelperContract adapter) {
        this.adapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        adapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder,
                                  int actionState) {

        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof TextAreaViewHolder) {
                TextAreaViewHolder myViewHolder =
                        (TextAreaViewHolder) viewHolder;
                adapter.onRowSelected(myViewHolder);
            } else if (viewHolder instanceof PhotoViewHolder) {
                PhotoViewHolder myViewHolder =
                        (PhotoViewHolder) viewHolder;
                adapter.onRowSelected(myViewHolder);
            }

        }

        super.onSelectedChanged(viewHolder, actionState);
    }
    @Override
    public void clearView(RecyclerView recyclerView,
                          RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof TextAreaViewHolder) {
            TextAreaViewHolder myViewHolder=
                    (TextAreaViewHolder) viewHolder;
            adapter.onRowClear(myViewHolder);
        } else if (viewHolder instanceof PhotoViewHolder) {
            PhotoViewHolder myViewHolder=
                    (PhotoViewHolder) viewHolder;
            adapter.onRowClear(myViewHolder);
        }
    }

    public interface ItemTouchHelperContract {

        void onRowMoved(int fromPosition, int toPosition);
        void onRowSelected(TextAreaViewHolder myViewHolder);
        void onRowClear(TextAreaViewHolder myViewHolder);
        void onRowSelected(PhotoViewHolder myViewHolder);
        void onRowClear(PhotoViewHolder myViewHolder);
    }

}