package com.example.notes;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

/**
 * Fragment containing GUI to obtain Voice notes.
 */
public class VoiceFragment extends Fragment {

    private EditText nameInput;

    private boolean attached = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment.
        View layout = inflater.inflate(R.layout.voice_content, container,false);
        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.nameInput = view.findViewById(R.id.name);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.attached = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.attached = false;
    }

}
