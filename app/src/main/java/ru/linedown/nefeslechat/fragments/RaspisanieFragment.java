package ru.linedown.nefeslechat.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.linedown.nefeslechat.databinding.FragmentRaspisanieBinding;
import ru.linedown.nefeslechat.entity.DaySchedule;
import ru.linedown.nefeslechat.entity.Lesson;
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
            parserRaspisaniya();
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

    private void parserRaspisaniya(){
        List<DaySchedule> scheduleParse = null;
        if(RaspisanieUtils.schedule != null) scheduleParse = RaspisanieUtils.schedule;
        try {
            scheduleParse = RaspisanieUtils.parseSchedulePage();
        } catch (IOException e) {
            Log.e("Расписание", "Ошибка" + e.getMessage());
        } finally {
            RaspisanieUtils.schedule = scheduleParse;
        }

        if (scheduleParse != null) {
            for (DaySchedule day : scheduleParse) {
                Log.d("Расписание", "День недели: " + day.getDayOfWeek() + " (" + day.getDate() + ")");
                for (Lesson lesson : day.getLessons()) {
                    Log.d("Расписание","  Пара: " + lesson.getParaNumber());
                    if (lesson.getStartTime() != null && lesson.getEndTime() != null) {
                        Log.d("Расписание","  Время: " + lesson.getStartTime() + " - " + lesson.getEndTime());
                    } else {
                        Log.i("Расписание","  Время: Не определено");
                    }
                    Log.d("Расписание","  Кабинет: " + lesson.getRoom());
                    Log.d("Расписание","  Предмет: " + lesson.getSubject());
                    Log.d("Расписание","  Преподаватель: " + lesson.getTeacher());
                    Log.d("Расписание","  Тип занятия: " + lesson.getLessonType());
                }
            }
        } else {
            Log.w("Расписание", "Не удалось получить расписание");
        }
    }
}