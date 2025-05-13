package ru.linedown.nefeslechat.fragments;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.linedown.nefeslechat.databinding.FragmentTasksBinding;
import ru.linedown.nefeslechat.entity.TaskDTO;
import ru.linedown.nefeslechat.interfaces.CreateTaskListener;
import ru.linedown.nefeslechat.interfaces.EditMessageActionListener;
import ru.linedown.nefeslechat.layuots.CreateTaskDialogFragment;
import ru.linedown.nefeslechat.utils.OkHttpUtil;

public class TasksFragment extends Fragment implements CreateTaskListener {

    private FragmentTasksBinding binding;
    Disposable disposable;
    Disposable disposableInner;
    LinearLayout tasksLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentTasksBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImageButton createButton = binding.addTaskButton;
        tasksLayout = binding.tasksLayout;

        Observable<List<TaskDTO>> observable = Observable.fromCallable(OkHttpUtil::getTasks);

        disposable = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            for(TaskDTO task : result) addTask(task);
                        }, error -> Log.e("Получение: исключение", "Текст исключения: " + error.getMessage(), error));

        createButton.setOnClickListener(view -> {
            CreateTaskDialogFragment createTaskDialogFragment = new CreateTaskDialogFragment(this);
            createTaskDialogFragment.show(getChildFragmentManager(), "CreateDialog");
        });


        return root;
    }
    @Override
    public void onApplyCreate(String taskText) {
        Observable<TaskDTO> observable = Observable.fromCallable(() -> OkHttpUtil.createTask(taskText));

        disposableInner = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::addTask, error -> Log.e("Создание: исключение", "Текст исключения: " + error.getMessage(), error));
    }

    @Override
    public void onCancelCreate() {
        Toast.makeText(getContext(), "Действие по созданию задачи отменено", Toast.LENGTH_SHORT).show();
    }

    private void addTask(TaskDTO task){
        TextView taskView = new TextView(getContext());
        taskView.setText(task.getText());
        taskView.setId(task.getId());

        TextView statusView = new TextView(getContext());
        String statusStr = task.isCompleted() ? "Выполнено" : "Не выполнено";
        statusView.setText(statusStr);

        TextView changeStatusView = new TextView(getContext());
        String changeStatusTaskText = "Изменить статус задания с айди: " + task.getId();
        changeStatusView.setText(changeStatusTaskText);

        TextView deleteTaskView = new TextView(getContext());
        String deleteTaskText = "Удалить задание с айди: " + task.getId();
        deleteTaskView.setText(deleteTaskText);

        changeStatusView.setOnClickListener(view -> {
            Observable<Boolean> observable = Observable.fromCallable(() -> OkHttpUtil.changeTaskStatus(task));
            disposableInner = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        if(result){
                            if(statusView.getText().equals("Выполнено")) statusView.setText("Не выполнено");
                            else statusView.setText("Выполнено");
                        } else Toast.makeText(getContext(), "Действие по изменению статуса задачи отменено", Toast.LENGTH_SHORT).show();
                    }, error -> Log.e("Изменение статуса: исключение", "Текст исключения: " + error.getMessage(), error));
        });
        deleteTaskView.setOnClickListener(view -> {
            Observable<Boolean> observable = Observable.fromCallable(() -> OkHttpUtil.deleteTask(task.getId()));
            disposableInner = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        if(result){
                            tasksLayout.removeView(taskView);
                            tasksLayout.removeView(statusView);
                            tasksLayout.removeView(changeStatusView);
                            tasksLayout.removeView(deleteTaskView);
                        }
                            else Toast.makeText(getContext(), "Действие по удалению задачи отменено", Toast.LENGTH_SHORT).show();
                    }, error -> Log.e("Удаление задачи: исключение", "Текст исключения: " + error.getMessage(), error));
        });

        tasksLayout.addView(taskView);
        tasksLayout.addView(statusView);
        tasksLayout.addView(changeStatusView);
        tasksLayout.addView(deleteTaskView);
    }

    private void clearAllDialogFragment(){
        FragmentManager fragmentManager = getChildFragmentManager();
        List<Fragment> dialogFragments = fragmentManager.getFragments();
        for(Fragment fragment : dialogFragments){
            if(fragment instanceof DialogFragment dialogFragment){
                dialogFragment.dismiss();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clearAllDialogFragment();
        if(disposable != null && !disposable.isDisposed()) disposable.dispose();
        if(disposableInner != null && !disposableInner.isDisposed()) disposableInner.dispose();
    }
}