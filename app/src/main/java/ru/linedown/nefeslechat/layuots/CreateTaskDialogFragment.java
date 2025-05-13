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
import ru.linedown.nefeslechat.interfaces.CreateTaskListener;

@AllArgsConstructor
public class CreateTaskDialogFragment extends DialogFragment {
    CreateTaskListener createTaskListener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final EditText editText = new EditText(requireContext());
        editText.setBackgroundResource(R.drawable.bg);
        editText.setTextColor(Color.BLACK);
        editText.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()),
                0, 0, 0);
        editText.setHint("Введите новую задачу");
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme);

        builder.setTitle("Создание задачи").setMessage("Напишите новую задачу в поле ввода").setView(editText)
                .setPositiveButton("Создать", (dialog, which) -> {
                    String newText = editText.getText().toString();
                    if(createTaskListener != null) createTaskListener.onApplyCreate(newText);
                    dismiss();
                }).setNegativeButton("Отмена", (dialog, which) -> {
                    if(createTaskListener != null) createTaskListener.onCancelCreate();
                    dismiss();
                });
        return builder.create();
    }
}
