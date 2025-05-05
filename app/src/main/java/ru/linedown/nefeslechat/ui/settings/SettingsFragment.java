package ru.linedown.nefeslechat.ui.settings;

import static android.view.View.VISIBLE;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.classes.ConfirmExitDialogFragment;
import ru.linedown.nefeslechat.classes.OkHttpUtil;
import ru.linedown.nefeslechat.classes.TextUtils;
import ru.linedown.nefeslechat.entity.UserDetailsDTO;
import ru.linedown.nefeslechat.databinding.FragmentSettingsBinding;
import ru.linedown.nefeslechat.interfaces.MyCallback;

public class SettingsFragment extends Fragment {
    //UserDetailsDTO currentUser;
    private FragmentSettingsBinding binding;
    private Disposable disposable;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        ImageButton exitButton = binding.exitButton;
        TextView fioStr = binding.fioStr;
        TextView facultetStr = binding.facultetStr;
        TextView roleStr = binding.roleStr;
        TextView mailStr = binding.mailStr;

        TextView groupOrAcademicTitleLabel = binding.groupOrAcademicTitleLabel;
        TextView groupOrAcademicTitle = binding.groupOrAcademicTitle;

        TextView formaOrZvanieLabel = binding.formaOrZvanieLabel;
        TextView formaOrZvanie = binding.formaOrZvanie;

        TextView expireDateStr = binding.expireStr;

        LinearLayout profileLayout = binding.profileLayout;

        Observable<UserDetailsDTO> observable = Observable.fromCallable(() -> {
            try{
                return OkHttpUtil.getCurrentUser();
            } catch (IOException e) {
                Log.d("IOException", "SettingsFragment. Текст сообщения: " + e.getMessage());
            }
            return null;
        });

        MyCallback<UserDetailsDTO> mcfu = new MyCallback<>() {
            @Override
            public void onSuccess(UserDetailsDTO result) {
                // UserDetailsDTO result = OkHttpUtil.getCurrentUser();
                String role = result.getRole();
                String fio = result.getLastName() + " " + result.getFirstName() + " " + result.getPatronymic();
                fioStr.setText(fio);
                Log.d("Роль: ", role);
                Log.d("ФИО: ", fio);
                roleStr.setText(role);
                TextUtils.setUnderlinedText(mailStr, result.getEmail());
                TextUtils.setUnderlinedText(facultetStr, result.getFaculty());
                facultetStr.setText(result.getFaculty());
                if(role.equals("Преподаватель")) {
                    profileLayout.setBackgroundResource(R.drawable.bg_prepod_settings);
                    groupOrAcademicTitleLabel.setText("Ученая степень");
                    TextUtils.setUnderlinedText(groupOrAcademicTitle, result.getAcademicDegree());
                    formaOrZvanieLabel.setText("Ученое звание");
                    TextUtils.setUnderlinedText(formaOrZvanie, result.getAcademicTitle());
                } else {
                    profileLayout.setBackgroundResource(R.drawable.bg_student_settings);
                    groupOrAcademicTitleLabel.setText("Учебная группа");
                    TextUtils.setUnderlinedText(groupOrAcademicTitle, result.getGroupName());
                    formaOrZvanieLabel.setText("Форма возмещения");
                    TextUtils.setUnderlinedText(formaOrZvanie, result.getReimbursement());
                }
                Date expireDate = result.getEnabledUntil();
                SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy");
                TextUtils.setUnderlinedText(expireDateStr, formatDate.format(expireDate));
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
            ConfirmExitDialogFragment confirmExitDialogFragment = new ConfirmExitDialogFragment();
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
}
