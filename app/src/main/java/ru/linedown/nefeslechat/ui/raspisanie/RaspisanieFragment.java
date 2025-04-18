package ru.linedown.nefeslechat.ui.raspisanie;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.databinding.FragmentRaspisanieBinding;

public class RaspisanieFragment extends Fragment {
    private FragmentRaspisanieBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RaspisanieViewModel raspisanieViewModel =
                new ViewModelProvider(this).get(RaspisanieViewModel.class);

        binding = FragmentRaspisanieBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }
}