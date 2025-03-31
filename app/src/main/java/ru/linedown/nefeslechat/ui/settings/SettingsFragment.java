package ru.linedown.nefeslechat.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.w3c.dom.Text;

import ru.linedown.nefeslechat.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        TextView fioStr = binding.fioStr;
        TextView statusStr = binding.statusStr;
        TextView phoneStr = binding.phoneStr;
        TextView mailStr = binding.mailStr;
        settingsViewModel.getSettings().observe(getViewLifecycleOwner(), settings -> {
            fioStr.setText(settings.getFio());
            statusStr.setText(settings.getStatus());
            phoneStr.setText(settings.getPhoneStr());
            mailStr.setText(settings.getMail());
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}