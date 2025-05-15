package ru.linedown.nefeslechat.layuots;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.utils.OkHttpUtil;

public class TaskLayout extends ConstraintLayout {
    public TextView taskTextView;
    public Switch statusTaskSwitch;
    private int taskId;
    private String taskText;
    private boolean taskStatus;

    public TaskLayout(Context context, int taskId, String taskText, boolean taskStatus) {
        super(context);
        this.taskStatus = taskStatus;
        this.taskId = taskId;
        this.taskText = taskText;
        init();
    }

    private void init(){
        taskTextView = new TextView(getContext());
        statusTaskSwitch = new Switch(getContext());

        setId(taskId);
        statusTaskSwitch.setChecked(taskStatus);
        taskTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        int backgroundResource = !taskStatus ? R.drawable.bg_green : R.drawable.bg_read_chat;
        Drawable background = ContextCompat.getDrawable(getContext(), backgroundResource);
        setBackground(background);

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 32, getResources().getDisplayMetrics()
        ));

        layoutParams.setMargins(0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 5, getResources().getDisplayMetrics()),
                0, 0);

        setLayoutParams(layoutParams);

        taskTextView.setText(taskText);
        taskTextView.setTextColor(Color.WHITE);
        ConstraintLayout.LayoutParams textViewLayoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        textViewLayoutParams.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()), 0, 0 ,0);
        textViewLayoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        textViewLayoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        textViewLayoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        taskTextView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.noto_sans_medium));

        taskTextView.setLayoutParams(textViewLayoutParams);

        ConstraintLayout.LayoutParams switchLayoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        switchLayoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        switchLayoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        switchLayoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;

        statusTaskSwitch.setLayoutParams(switchLayoutParams);

        addView(taskTextView);
        addView(statusTaskSwitch);
    }

}
