package ru.linedown.nefeslechat.ui.search;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
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
                    for(UserInListDTO user: result){
                        results.removeAllViews();
                        TextView userView = new TextView(getActivity());
                        userView.setTextSize(20);
                        String resultStr = user.getName() + ". Роль: " + user.getRole() + ". Кафедра: " + user.getDepartment();

                        userView.setText(resultStr);

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