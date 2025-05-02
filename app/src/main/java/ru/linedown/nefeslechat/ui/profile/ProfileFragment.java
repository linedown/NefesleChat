package ru.linedown.nefeslechat.ui.profile;

import static android.view.View.VISIBLE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.classes.OkHttpUtil;
import ru.linedown.nefeslechat.classes.UserDetailsDTO;
import ru.linedown.nefeslechat.databinding.FragmentProfileBinding;
import ru.linedown.nefeslechat.interfaces.MyCallbackForUser;
import ru.linedown.nefeslechat.ui.raspisanie.RaspisanieViewModel;


public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private final int userId = OkHttpUtil.getUserId();

    Disposable disposable;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RaspisanieViewModel raspisanieViewModel =
                new ViewModelProvider(this).get(RaspisanieViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button chatButton = binding.transitionToChatButton;

        TextView statusStr = binding.statusStr;
        TextView roleStr = binding.roleStr;
        TextView mailStr = binding.mailStr;
        TextView fioStr = binding.fioStr;

        TextView groupOrAcademicTitle = binding.groupOrAcademicTitle;
        TextView groupOrAcademicTitleLabel = binding.groupOrAcademicTitleLabel;

        View underGroupOrAcademicTitleDivider = binding.underGroupOrAcademicDivider;
        TextView academicDegree = binding.academicDegree;
        TextView academicDegreeLabel = binding.academicDegreeLabel;

        Observable<UserDetailsDTO> observable = Observable.fromCallable(() -> {
            try{
                return OkHttpUtil.getOtherUser(userId);
            } catch (IOException e) {
                Log.d("IOException", "SettingsFragment. Текст сообщения: " + e.getMessage());
            }
            return null;
        });

        MyCallbackForUser mcfu = new MyCallbackForUser() {
            @Override
            public void onSuccess(UserDetailsDTO result) {
                // UserDetailsDTO result = OkHttpUtil.getCurrentUser();
                String role = result.getRole();
                String fio = result.getLastName() + " " + result.getFirstName() + " " + result.getPatronymic();
                fioStr.setText(fio);
                statusStr.setText("Статус-заглушка");
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

        chatButton.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("TitleToolBar", fioStr.getText().toString());
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