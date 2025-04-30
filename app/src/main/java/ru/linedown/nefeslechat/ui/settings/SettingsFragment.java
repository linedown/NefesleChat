package ru.linedown.nefeslechat.ui.settings;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
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

import java.io.IOException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.linedown.nefeslechat.Activity.LoginActivity;
import ru.linedown.nefeslechat.Activity.MainActivity;
import ru.linedown.nefeslechat.Activity.RegisterActivity;
import ru.linedown.nefeslechat.classes.OkHttpUtil;
import ru.linedown.nefeslechat.classes.UserDetailsDTO;
import ru.linedown.nefeslechat.databinding.FragmentSettingsBinding;
import ru.linedown.nefeslechat.interfaces.MyCallbackForUser;

public class SettingsFragment extends Fragment {
    //UserDetailsDTO currentUser;
    private FragmentSettingsBinding binding;
    private Disposable disposable;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Button exitButton = binding.exitButton;
        TextView fioStr = binding.fioStr;
        TextView statusStr = binding.statusStr;
        TextView roleStr = binding.roleStr;
        TextView mailStr = binding.mailStr;

        TextView groupOrAcademicTitle = binding.groupOrAcademicTitle;
        TextView groupOrAcademicTitleLabel = binding.groupOrAcademicTitleLabel;

        View underGroupOrAcademicTitleDivider = binding.underGroupOrAcademicDivider;
        TextView academicDegree = binding.academicDegree;
        TextView academicDegreeLabel = binding.academicDegreeLabel;


        Observable<UserDetailsDTO> observable = Observable.fromCallable(() -> {
            try{
                return OkHttpUtil.getCurrentUser();
            } catch (IOException e) {
                Log.d("IOException", "SettingsFragment. Текст сообщения" + e.getMessage());
            }
            return null;
        });

        MyCallbackForUser mcfu = new MyCallbackForUser() {
            @Override
            public void onSuccess(UserDetailsDTO result) {
                String role = result.getRole();
                String fio = result.getLastName() + " " + result.getFirstName() + " " + result.getPatronymic();
                fioStr.setText(fio);
                statusStr.setText("Статус-заглушка");
                Log.d("Роль: ", role);
                Log.d("ФИО: ", fio);
                roleStr.setText(role);
                mailStr.setText(result.getEmail());

                if(role.equals("Преподаватель")) {
                    groupOrAcademicTitleLabel.setText("Учёное звание");
                    groupOrAcademicTitle.setText(result.getAcademicTitle());

                    underGroupOrAcademicTitleDivider.setVisibility(VISIBLE);
                    academicDegree.setVisibility(VISIBLE);
                    academicDegreeLabel.setVisibility(VISIBLE);

                    academicDegree.setText(result.getAcademicDegree());
                    academicDegreeLabel.setText("Учёная должность");
                } else {
                    groupOrAcademicTitleLabel.setText("Группа");
                    groupOrAcademicTitle.setText(result.getGroupName());
                }
            }
            @Override
            public void onError(String errorMessage) {
                Log.d("ObserveError", "Текст ошибки: " + errorMessage);
            }
        };

        disposable = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        mcfu::onSuccess, error -> mcfu.onError(error.getMessage())
                );

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
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
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
            builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
            builder.setCancelable(true);

            return builder.create();
        }
    }
}
