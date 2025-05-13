package ru.linedown.nefeslechat.fragments;

import static android.view.View.VISIBLE;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.utils.OkHttpUtil;
import ru.linedown.nefeslechat.utils.TextUtils;
import ru.linedown.nefeslechat.entity.UserDetailsDTO;
import ru.linedown.nefeslechat.databinding.FragmentProfileBinding;
import ru.linedown.nefeslechat.interfaces.MyCallback;


public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private final int userId = OkHttpUtil.getUserId();
    private String chatId;
    String firstLastName;

    Disposable disposable;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImageButton chatButton = binding.goToChatButton;

        LinearLayout profileOtherUserLayout = binding.profileOtherUserLayout;

        TextView statusLabel = binding.statusLabel;

        TextView fioLabel = binding.fioLabel;
        ImageView avatar = binding.avatarView;
        TextView roleLabel = binding.roleLabel;

        TextView facultetOrDepartamentLabel = binding.facultetOrDepartamentLabel;
        TextView facultetOrDepartament = binding.facultetOrDepartament;

        TextView stepenOrGroupLabel = binding.stepenOrGroupLabel;
        TextView stepenOrGroup = binding.stepenOrGroup;

        TextView zvanieLabel = binding.zvanieLabel;
        TextView zvanie = binding.zvanie;


        Observable<UserDetailsDTO> observable = Observable.fromCallable(() -> {
            chatId = OkHttpUtil.getIdInChatForProfile(userId);
            return OkHttpUtil.getOtherUser(userId);
        });

        MyCallback<UserDetailsDTO> mcfu = new MyCallback<>() {
            @Override
            public void onSuccess(UserDetailsDTO result) {
                String statusText = OkHttpUtil.getStatusStr();
                if(statusText.equals("В сети")) statusLabel.setBackgroundResource(R.drawable.bg_status_online);
                statusLabel.setText(statusText);
                statusLabel.setPadding(12, 6, 12, 6);
                String role = result.getRole();
                firstLastName = result.getFirstName() + " " + result.getLastName();
                String fio = result.getLastName() + " " + result.getFirstName() + " " + result.getPatronymic();
                fioLabel.setText(fio);
                roleLabel.setText(role);

                if (role.equals("Преподаватель")) {
                    profileOtherUserLayout.setBackgroundResource(R.drawable.bg_prepod_settings);
                    facultetOrDepartamentLabel.setText("Кафедра");
                    TextUtils.setUnderlinedText(facultetOrDepartament, result.getDepartment());
                    stepenOrGroupLabel.setText("Ученая степень");
                    TextUtils.setUnderlinedText(stepenOrGroup, result.getAcademicDegree());

                    zvanieLabel.setVisibility(VISIBLE);
                    zvanieLabel.setText("Учёное звание");
                    zvanie.setVisibility(VISIBLE);
                    TextUtils.setUnderlinedText(zvanie, result.getAcademicTitle());
                } else {
                    profileOtherUserLayout.setBackgroundResource(R.drawable.bg_student_settings);
                    facultetOrDepartamentLabel.setText("Факультет");
                    TextUtils.setUnderlinedText(facultetOrDepartament, result.getFaculty());
                    stepenOrGroupLabel.setText("Учебная группа");
                    TextUtils.setUnderlinedText(stepenOrGroup, result.getGroupName());
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("ObserveError", "Текст ошибки: " + errorMessage);
            }
        };

        disposable = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        mcfu::onSuccess, error -> mcfu.onError(error.getMessage())
                );

        chatButton.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("TitleToolBar", firstLastName);
            bundle.putString("UserId", String.valueOf(userId));
            bundle.putString("ChatType", "Single");
            bundle.putString("ChatId", chatId);
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_global_to_nav_chat, bundle);
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