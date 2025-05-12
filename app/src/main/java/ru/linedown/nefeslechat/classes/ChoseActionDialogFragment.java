package ru.linedown.nefeslechat.classes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import lombok.AllArgsConstructor;
import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.interfaces.ChoseActionListener;


@AllArgsConstructor
public class ChoseActionDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    ChoseActionListener actionListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        builder.setTitle("Действие");
        builder.setPositiveButton("Изменение", this);
        builder.setNeutralButton("Удаление", this);
        builder.setNegativeButton("Отмена", this);
        builder.setCancelable(true);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which){
            case Dialog.BUTTON_POSITIVE -> {
                if(actionListener != null) actionListener.onEditAction();
            }
            case Dialog.BUTTON_NEUTRAL -> {
                if(actionListener != null) actionListener.onDeleteAction();
            }
            case Dialog.BUTTON_NEGATIVE -> {
                if(actionListener != null) actionListener.onCancelAction();
            }
        }
        dismiss();
    }
}
