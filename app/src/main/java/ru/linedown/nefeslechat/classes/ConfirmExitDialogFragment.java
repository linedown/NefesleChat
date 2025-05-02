package ru.linedown.nefeslechat.classes;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ru.linedown.nefeslechat.Activity.LoginActivity;
import ru.linedown.nefeslechat.R;

public class ConfirmExitDialogFragment extends DialogFragment {
    SharedPreferences sharedPreferences;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        builder.setTitle("Подтверждение");
        builder.setMessage("Вы точно хотите выйти из аккаунта?");
        builder.setPositiveButton("Выйти", (dialog, which) -> logoutAction(getActivity(), dialog));
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
        builder.setCancelable(true);

        return builder.create();
    }

    private void logoutAction(Context context, DialogInterface dialog){
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Toast.makeText(getActivity(), "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();
        sharedPreferences = getActivity().getSharedPreferences("LoginInfo", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        dialog.cancel();
        startActivity(intent);
    }
}
