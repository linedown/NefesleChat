package ru.linedown.nefeslechat.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
import ru.linedown.nefeslechat.interfaces.MyCallback;
import ru.linedown.nefeslechat.layuots.DayOfWeekLayout;
import ru.linedown.nefeslechat.layuots.LessonLayout;
import ru.linedown.nefeslechat.utils.OkHttpUtil;
import ru.linedown.nefeslechat.utils.RaspisanieUtils;

public class RaspisanieFragment extends Fragment {
    private FragmentRaspisanieBinding binding;
    Disposable disposable;
    LinearLayout raspisanieLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRaspisanieBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        raspisanieLayout = binding.raspisanieLayout;

        DayOfWeekLayout dayOfWeekLayout = new DayOfWeekLayout(getContext());
        dayOfWeekLayout.setDayOfWeekText("Понедельник (12.05)");
        for(int i = 0; i < 4; i++){
            LessonLayout lessonLayout = new LessonLayout(getContext());
            lessonLayout.addLesson("9:00 - 10:30", "Человеко-машинное взаимодействие", "1-216", "Практические занятия", "Забродин Андрей Владимирович");
            dayOfWeekLayout.addView(lessonLayout);
        }

        raspisanieLayout.addView(dayOfWeekLayout);

        Observable<List<DaySchedule>> observable = Observable.fromCallable(this::getListDaysOfSchedule);

        MyCallback<List<DaySchedule>> raspisanieCallback = new MyCallback<>() {
            @Override
            public void onSuccess(List<DaySchedule> result) {
                if (result != null) {
                    for (DaySchedule day : result) {
                        Log.d("Расписание", "День недели: " + day.getDayOfWeek() + " (" + day.getDate() + ")");
                        for (Lesson lesson : day.getLessons()) {
                            Log.d("Расписание", "  Пара: " + lesson.getParaNumber());
                            if (lesson.getStartTime() != null && lesson.getEndTime() != null) {
                                Log.d("Расписание", "  Время: " + lesson.getStartTime() + " - " + lesson.getEndTime());
                            } else {
                                Log.i("Расписание", "  Время: Не определено");
                            }
                            Log.d("Расписание", "  Кабинет: " + lesson.getRoom());
                            Log.d("Расписание", "  Предмет: " + lesson.getSubject());
                            Log.d("Расписание", "  Преподаватель: " + lesson.getTeacher());
                            Log.d("Расписание", "  Тип занятия: " + lesson.getLessonType());
                        }
                    }
                } else {
                    Log.w("Расписание", "Не удалось получить расписание");
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("Расписание", "Ошибка получения: " + errorMessage);
            }
        };

        disposable = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(raspisanieCallback::onSuccess, error -> raspisanieCallback.onError(error.getMessage()));

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(disposable != null && !disposable.isDisposed()) disposable.dispose();
    }

    private List<DaySchedule> getListDaysOfSchedule(){
        List<DaySchedule> scheduleParse = null;
        if(RaspisanieUtils.schedule != null) scheduleParse = RaspisanieUtils.schedule;
        else{
            try {
            scheduleParse = RaspisanieUtils.parseSchedulePage();
            } catch (IOException e) {
                Log.e("Расписание", "Ошибка: " + e.getMessage());
            } finally {
                RaspisanieUtils.schedule = scheduleParse;
            }
        }

        return scheduleParse;
    }
}