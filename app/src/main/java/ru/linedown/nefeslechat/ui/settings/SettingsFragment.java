package ru.linedown.nefeslechat.ui.settings;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import org.w3c.dom.Text;

import ru.linedown.nefeslechat.Activity.LoginActivity;
import ru.linedown.nefeslechat.Activity.MainActivity;
import ru.linedown.nefeslechat.Activity.RegisterActivity;
import ru.linedown.nefeslechat.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Button exitButton = binding.exitButton;
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
        exitButton.setOnClickListener(v -> {
            confirmExitDialogFragment confirmExitDialogFragment = new confirmExitDialogFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            confirmExitDialogFragment.show(transaction, "dialog");
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static class confirmExitDialogFragment extends DialogFragment{
        SharedPreferences sharedPreferences;

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Подтверждение");
            builder.setMessage("Вы точно хотите выйти из аккаунта?");
            builder.setPositiveButton("Да", (dialog, which) -> {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                Toast.makeText(getActivity(), "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();
                sharedPreferences = getActivity().getSharedPreferences("LoginInfo", MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();
                dialog.cancel();
                startActivity(intent);
            });
            builder.setNegativeButton("Отмена", (dialog, which) -> {
                dialog.cancel();
            });
            builder.setCancelable(true);

            return builder.create();
        }
    }
}
