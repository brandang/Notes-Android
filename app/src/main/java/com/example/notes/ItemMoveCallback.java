package com.example.notes;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Callback that is responsible for letting Adapter know whenever the user drags and drops an item.
 * Swiping is disabled.
 */
public class ItemMoveCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperContract adapter;

    /**
     * Create a new Callback for the Adapter. The Adapter will be notified if anything is dragged.
     * @param adapter The Adapter to callback for.
     */
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
        return;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // Only allow dragging up and down.
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        this.adapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder,
                                  int actionState) {

        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {

            // Determine which type of item was selected.
            if (viewHolder instanceof TextAreaHolder) {
                TextAreaHolder textAreaHolder =
                        (TextAreaHolder) viewHolder;
                this.adapter.onRowSelected(textAreaHolder);
            } else if (viewHolder instanceof RecyclerImageViewHolder) {
                RecyclerImageViewHolder recyclerImageViewHolder =
                        (RecyclerImageViewHolder) viewHolder;
                this.adapter.onRowSelected(recyclerImageViewHolder);
            }
        }

        super.onSelectedChanged(viewHolder, actionState);
    }
    @Override
    public void clearView(RecyclerView recyclerView,
                          RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        // Determine which type of item was deselected.
        if (viewHolder instanceof TextAreaHolder) {
            TextAreaHolder textAreaHolder =
                    (TextAreaHolder) viewHolder;
            this.adapter.onRowClear(textAreaHolder);
        } else if (viewHolder instanceof RecyclerImageViewHolder) {
            RecyclerImageViewHolder recyclerImageViewHolder =
                    (RecyclerImageViewHolder) viewHolder;
            this.adapter.onRowClear(recyclerImageViewHolder);
        }
    }

    /**
     * Interface that the Adapter should implement so that ItemMoveCallback can notify when
     * something happens.
     */
    public interface ItemTouchHelperContract {

        void onRowMoved(int fromPosition, int toPosition);
        void onRowSelected(TextAreaHolder myViewHolder);
        void onRowClear(TextAreaHolder myViewHolder);
        void onRowSelected(RecyclerImageViewHolder myViewHolder);
        void onRowClear(RecyclerImageViewHolder myViewHolder);
    }

}