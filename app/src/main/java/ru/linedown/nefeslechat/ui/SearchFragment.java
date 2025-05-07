package ru.linedown.nefeslechat.ui;

import static android.content.Context.MODE_PRIVATE;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.classes.OkHttpUtil;
import ru.linedown.nefeslechat.classes.ResultMessageLayout;
import ru.linedown.nefeslechat.entity.ResultMessageAttributes;
import ru.linedown.nefeslechat.entity.UserInListDTO;
import ru.linedown.nefeslechat.databinding.FragmentSearchBinding;
import ru.linedown.nefeslechat.interfaces.MyCallback;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private Disposable disposable;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        EditText searchEditText = binding.searchEditText;
        ImageView searchButton = binding.searchButton;
        LinearLayout results = binding.searchResultsLayout;

        searchButton.setOnClickListener(v -> {
            String searchText = searchEditText.getText().toString();

            Observable<List<UserInListDTO>> observable = Observable.fromCallable(() -> {
                try {
                    return OkHttpUtil.getListUsers(searchText);
                } catch (IOException e) {
                    Log.e("GetUsersProblem", "Текст ошибки: " + e.getMessage());
                }

                return Collections.emptyList();
            });

            MyCallback<List<UserInListDTO>> mcful = new MyCallback<>() {
                @Override
                public void onSuccess(List<UserInListDTO> result) {
                    results.removeAllViews();
                    if (result.isEmpty()) {
                        TextView emptyResultInfoView = new TextView(getActivity());
                        emptyResultInfoView.setTextSize(20);
                        emptyResultInfoView.setTypeface(null, Typeface.BOLD);
                        emptyResultInfoView.setText("По данному запросу не удалось найти пользователей");
                        emptyResultInfoView.setTextColor(getResources().getColor(R.color.otherSettingsColor));
                        emptyResultInfoView.setPadding(50, 0, 0, 0);
                        results.addView(emptyResultInfoView);
                        return;
                    }
                    for (UserInListDTO user : result) {
                        String role = user.getRole();
                        int resourceBackground = (role.equals("Преподаватель"))
                                ? R.drawable.bg_prepod_settings : R.drawable.bg_student_settings;
                        String departamentOrGroup = (role.equals("Преподаватель")) ? user.getDepartment() : user.getGroup();
                        ResultMessageAttributes rma =
                                new ResultMessageAttributes(user.getId(), user.getName(), departamentOrGroup, resourceBackground);
                        ResultMessageLayout resultMessageLayout = new ResultMessageLayout(getActivity(), rma);
                        resultMessageLayout.setOnClickListener(view -> {
                            if (rma.getId() == Integer.parseInt(getActivity()
                                    .getSharedPreferences("LoginInfo", MODE_PRIVATE)
                                    .getString("id", "0")))
                                Toast.makeText(getActivity(),
                                        "Это вы! Перейдите в раздел настроек для показа профиля!",
                                        Toast.LENGTH_SHORT).show();
                            else {
                                OkHttpUtil.setUserId(rma.getId());

                                NavController navController = Navigation.findNavController(view);
                                navController.navigate(R.id.action_nav_to_profile, null);
                            }
                            Log.d("Айди пользователя:", " " + OkHttpUtil.getUserId());
                        });

                        results.addView(resultMessageLayout);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e("ObserveError", "Текст ошибки: " + errorMessage);
                }
            };

            disposable = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            mcful::onSuccess, error -> mcful.onError(error.getMessage())
                    );
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