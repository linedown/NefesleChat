package ru.linedown.nefeslechat.classes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import lombok.AllArgsConstructor;
import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.interfaces.DeleteMessageActionListener;


@AllArgsConstructor
public class ConfirmDeleteMessageDialogFragment extends DialogFragment {
    DeleteMessageActionListener deleteMessageActionListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        builder.setTitle("Подтверждение");
        builder.setMessage("Вы точно хотите удалить сообщение?");
        builder.setPositiveButton("Да", (dialog, which) -> {
            if(deleteMessageActionListener != null) deleteMessageActionListener.onApplyDelete();
        });
        builder.setNegativeButton("Нет", (dialog, which) -> {
            if(deleteMessageActionListener != null) deleteMessageActionListener.onCancelDelete();
        });
        builder.setCancelable(true);

        return builder.create();
    }


}
