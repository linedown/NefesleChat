package ru.linedown.nefeslechat.ui.self;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ru.linedown.nefeslechat.databinding.FragmentSelfBinding;

public class SelfFragment extends Fragment {

    private FragmentSelfBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SelfViewModel selfViewModel =
                new ViewModelProvider(this).get(SelfViewModel.class);

        binding = FragmentSelfBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSelf;
        selfViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}