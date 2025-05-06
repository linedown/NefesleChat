package ru.linedown.nefeslechat.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.linedown.nefeslechat.databinding.FragmentNotesBinding;

public class NotesFragment extends Fragment {
    private FragmentNotesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentNotesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }
}