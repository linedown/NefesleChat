package ru.linedown.nefeslechat.fragments;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.databinding.FragmentTasksBinding;
import ru.linedown.nefeslechat.entity.TaskDTO;
import ru.linedown.nefeslechat.interfaces.DeleteMessageActionListener;
import ru.linedown.nefeslechat.layuots.ConfirmDeleteMessageDialogFragment;
import ru.linedown.nefeslechat.layuots.TaskLayout;
import ru.linedown.nefeslechat.utils.OkHttpUtil;

public class TasksFragment extends Fragment implements DeleteMessageActionListener {

    private FragmentTasksBinding binding;
    Disposable disposable;
    Disposable disposableInner;
    Disposable disposableSwitch;
    LinearLayout activeTasksLayout;
    LinearLayout finishedTasksLayout;
    int taskId;
    TaskLayout currentTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentTasksBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button createButton = binding.createTaskButton;
        activeTasksLayout = binding.activeTasksLayout;
        finishedTasksLayout = binding.finishedTasksLayout;
        TextView tasksTextInput = binding.inputTaskText;

        Observable<List<TaskDTO>> observable = Observable.fromCallable(OkHttpUtil::getTasks);

        disposable = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            for(TaskDTO task : result) addTask(task);
                        }, error -> Log.e("Получение: исключение", "Текст исключения: " + error.getMessage(), error));

        createButton.setOnClickListener(view -> {
            String tasksText = tasksTextInput.getText().toString();
            tasksTextInput.setText("");
            Observable<TaskDTO> observableInner = Observable.fromCallable(() -> OkHttpUtil.createTask(tasksText));

            disposableInner = observableInner.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::addTask, error -> Log.e("Создание: исключение", "Текст исключения: " + error.getMessage(), error));
        });


        return root;
    }

    private void addTask(TaskDTO task){
        TaskLayout taskLayout = new TaskLayout(getContext(), task);
        if(task.isCompleted()) finishedTasksLayout.addView(taskLayout);
        else activeTasksLayout.addView(taskLayout);

        taskLayout.taskTextView.setOnClickListener(view -> {
            ConfirmDeleteMessageDialogFragment confirmDeleteMessageDialogFragment = new ConfirmDeleteMessageDialogFragment(this);
            confirmDeleteMessageDialogFragment.show(getChildFragmentManager(), "DeleteDialog");
            taskId = task.getId();
            currentTask = taskLayout;
        });

        taskLayout.statusTaskSwitch.setOnCheckedChangeListener((view, isChecked) -> {
            Observable<Boolean> observable = Observable.fromCallable(() -> OkHttpUtil.changeTaskStatus(task.getId()));
            disposableSwitch = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        if(result){
                            if(isChecked) {
                                activeTasksLayout.removeView(taskLayout);
                                finishedTasksLayout.addView(taskLayout);
                                taskLayout.setBackgroundResource(R.drawable.bg_read_chat);
                            }
                            else {
                                finishedTasksLayout.removeView(taskLayout);
                                activeTasksLayout.addView(taskLayout);
                                taskLayout.setBackgroundResource(R.drawable.bg_green);
                            }
                        } else Toast.makeText(getContext(), "Действие по изменению статуса задачи отменено", Toast.LENGTH_SHORT).show();
                    }, error -> Log.e("Изменение статуса: исключение", "Текст исключения: " + error.getMessage(), error));
        });
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
        if(disposableSwitch != null && !disposableSwitch.isDisposed()) disposableSwitch.dispose();
    }

    @Override
    public void onApplyDelete() {
        Observable<Boolean> observable = Observable.fromCallable(() -> OkHttpUtil.deleteTask(taskId));
        disposableInner = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if(result){
                        activeTasksLayout.removeView(currentTask);
                        finishedTasksLayout.removeView(currentTask);
                    }
                    else Toast.makeText(getContext(), "Действие по удалению задачи отменено", Toast.LENGTH_SHORT).show();
                }, error -> Log.e("Удаление задачи: исключение", "Текст исключения: " + error.getMessage(), error));
    }

    @Override
    public void onCancelDelete() {
        Toast.makeText(getContext(), "Действие по удалению отменено", Toast.LENGTH_SHORT).show();
    }
}