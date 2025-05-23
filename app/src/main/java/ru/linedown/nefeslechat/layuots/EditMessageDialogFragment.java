package ru.linedown.nefeslechat.layuots;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import lombok.AllArgsConstructor;
import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.interfaces.EditMessageActionListener;

@AllArgsConstructor
public class EditMessageDialogFragment extends DialogFragment {
    EditMessageActionListener editMessageActionListener;
    String actualMessageText;
    public static boolean isCreate;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final EditText editText = new EditText(requireContext());
        editText.setBackgroundResource(R.drawable.bg);
        editText.setTextColor(Color.BLACK);
        editText.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()),
                0, 0, 0);
        isCreate = actualMessageText.isEmpty();
        editText.setText(actualMessageText);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme);

        builder.setTitle("Действие").setMessage("Введите новый текст").setView(editText)
                .setPositiveButton("Применить", (dialog, which) -> {
                    String newText = editText.getText().toString();
                    if(editMessageActionListener != null) editMessageActionListener.onApplyEdit(newText);
                    dismiss();
                }).setNegativeButton("Отмена", (dialog, which) -> {
                    if(editMessageActionListener != null) editMessageActionListener.onCancelEdit();
                    dismiss();
                });
        return builder.create();
    }
}
