package ru.linedown.nefeslechat.ui.createchat;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.databinding.FragmentCreateChatBinding;

public class CreateChatFragment extends Fragment {

    private FragmentCreateChatBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CreateChatViewModel createChatViewModel =
                new ViewModelProvider(this).get(CreateChatViewModel.class);

        binding = FragmentCreateChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.createChatText;
        createChatViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}