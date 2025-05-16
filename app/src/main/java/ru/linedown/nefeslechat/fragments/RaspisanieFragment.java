package ru.linedown.nefeslechat.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.linedown.nefeslechat.databinding.FragmentRaspisanieBinding;
import ru.linedown.nefeslechat.entity.UserDetailsDTO;
import ru.linedown.nefeslechat.utils.OkHttpUtil;
import ru.linedown.nefeslechat.utils.RaspisanieUtils;

public class RaspisanieFragment extends Fragment {
    private FragmentRaspisanieBinding binding;
    Disposable disposable;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRaspisanieBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Observable<String> observable = Observable.fromCallable(() -> {
            UserDetailsDTO user = OkHttpUtil.getCurrentUser();
            if(user.getRole().equals("Преподаватель")){
                RaspisanieUtils.title = user.getLastName();
                RaspisanieUtils.by = "teacher";
            } else if(user.getRole().equals("Студент")){
                RaspisanieUtils.title = user.getGroupName();
                RaspisanieUtils.by = "group";
            }
            RaspisanieUtils.parser();
            return "OK";
        });

        disposable = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> Log.d("Расписание", result), error -> Log.e("Расписание", "Ошибка: " + error.getMessage()));

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(disposable != null && !disposable.isDisposed()) disposable.dispose();
    }
}