package com.example.notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Fragment that contains RecyclerView of items that is responsible for allowing user to rearrange
 * each item by dragging and dropping or swiping.
 */
public class ReorderFragment extends Fragment {

    private ReorderAdapter adapter;

    private RecyclerView recycler;

    // Data to hold on to until parent Activity is created.
    private SaveData saveData;
    private SnackbarDisplayer displayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment.
        View layout = inflater.inflate(R.layout.reorder_content, container,false);
        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // Have to attain child views here.
        this.recycler = view.findViewById(R.id.reorder_recycler);

        if (this.saveData != null || this.displayer != null) {
            this.setupRecycler();
        }
    }

    /**
     * Sets the data to display. Not guaranteed to do right away, since parent Activity may not
     * have been created yet.
     * @param saveData The SaveData.
     * @param displayer The component to use to display the Snackbar.
     */
    public void setData(SaveData saveData, SnackbarDisplayer displayer) {
        this.saveData = saveData;
        this.displayer = displayer;
        if (this.recycler != null) {
            this.setupRecycler();
        }
    }

    /**
     * Sets up the RecyclerView with ItemTouchHelpers and an Adapter.
     */
    private void setupRecycler() {

        this.adapter = new ReorderAdapter(this.getActivity(), this.saveData, this.recycler,
                this.displayer);
        this.recycler.setAdapter(this.adapter);

        // Assign callback so that we can handle dragging and dropping.
        ItemTouchHelper.Callback callback = new ItemMoveCallback(this.adapter, this.getActivity());
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(this.recycler);
    }

    /**
     * Returns the SaveData that the user may have modified.
     * @return The SaveData.
     */
    public SaveData getData() {
        if (this.adapter == null)
            return null;
        return this.adapter.getSaveData();
    }
}
