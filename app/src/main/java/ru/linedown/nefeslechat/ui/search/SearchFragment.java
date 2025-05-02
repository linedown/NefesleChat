package ru.linedown.nefeslechat.ui.search;

import static android.content.Context.MODE_PRIVATE;

import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import ru.linedown.nefeslechat.Activity.MainActivity;
import ru.linedown.nefeslechat.Activity.RegisterActivity;
import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.classes.OkHttpUtil;
import ru.linedown.nefeslechat.classes.UserInListDTO;
import ru.linedown.nefeslechat.databinding.FragmentSearchBinding;
import ru.linedown.nefeslechat.interfaces.MyCallbackForUserList;
import ru.linedown.nefeslechat.ui.search.SearchViewModel;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private Disposable disposable;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        root.post(() -> {
            int width = root.getWidth();
            int height = root.getHeight();

            Log.d("Размеры экрана", "Ширина: " + width + " Высота: " + height);
        });


        EditText searchEditText = binding.searchEditText;
        Button searchButton = binding.searchButton;
        LinearLayout results = binding.searchResultsLayout;

        searchButton.setOnClickListener(v -> {
            String searchText = searchEditText.getText().toString();
            List<UserInListDTO> users = Collections.emptyList();

            Observable<List<UserInListDTO>> observable = Observable.fromCallable(() -> {
                try {
                    return OkHttpUtil.getListUsers(searchText);
                } catch (IOException e) {
                    Log.d("GetUsersProblem", "Текст ошибки: " + e.getMessage());
                }

                return Collections.emptyList();
            });

            MyCallbackForUserList mcful = new MyCallbackForUserList() {
                @Override
                public void onSuccess(List<UserInListDTO> result) {
                    results.removeAllViews();
                    if(result.isEmpty()) {
                        TextView emptyResultInfoView = new TextView(getActivity());
                        emptyResultInfoView.setTextSize(20);
                        emptyResultInfoView.setTypeface(null, Typeface.BOLD);
                        emptyResultInfoView.setText("По данному запросу не удалось найти пользователей");
                        results.addView(emptyResultInfoView);
                        return;
                    }
                    for(UserInListDTO user: result){
                        TextView userView = new TextView(getActivity());
                        userView.setId(user.getId());
                        userView.setTextSize(20);
                        userView.setTypeface(null, Typeface.BOLD);

                        String resultStr = user.getName() + ". Роль: " + user.getRole();

                        userView.setText(resultStr);
                        userView.setOnClickListener(view -> {
                            if(userView.getId() == Integer.parseInt(getActivity()
                                    .getSharedPreferences("LoginInfo", MODE_PRIVATE)
                                    .getString("id", "0")))
                                Toast.makeText(getActivity(),
                                    "Это вы! Перейдите в раздел настроек для показа профиля!",
                                        Toast.LENGTH_SHORT).show();
                            else {
                                OkHttpUtil.setUserId(userView.getId());

                                // Далее будет описан на фрагмент профиля
                                NavController navController = Navigation.findNavController(view);
                                navController.navigate(R.id.action_nav_to_profile, null);
                            }
                            Log.d("Айди пользователя:", " " + OkHttpUtil.getUserId());
                        });

                        results.addView(userView);
                    }
                }
                @Override
                public void onError(String errorMessage) {
                    Log.d("ObserveError", "Текст ошибки: " + errorMessage);
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