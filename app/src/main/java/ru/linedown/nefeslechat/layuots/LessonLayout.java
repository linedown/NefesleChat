package ru.linedown.nefeslechat.layuots;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import java.text.SimpleDateFormat;

import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.entity.Lesson;
import ru.linedown.nefeslechat.utils.RaspisanieUtils;

public class LessonLayout extends LinearLayout {
    TextView timeOfLessonView;
    TextView nameOfLessonView;
    TextView auditoryView;
    TextView typeOfLessonView;
    TextView prepodOrGroupView;
    Lesson lesson;
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    public LessonLayout(Context context, Lesson lesson){
        super(context);
        this.lesson = lesson;
        init(context);
    }

    void init(Context context){
        setOrientation(VERTICAL);
        LinearLayout.LayoutParams lessonLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        lessonLayoutParams.setMargins(0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getContext().getResources().getDisplayMetrics()),
                0, 0);

        setLayoutParams(lessonLayoutParams);

        timeOfLessonView = createTextView(context, R.font.inter_bold, 14);
        auditoryView = createTextView(context, R.font.inter_bold, 14);

        nameOfLessonView = createTextView(context, R.font.inter_bold, 14);
        typeOfLessonView = createTextView(context, R.font.inter_light, 14);
        prepodOrGroupView = createTextView(context, R.font.inter_light, 14);

        LinearLayout lessonLine1 = new LinearLayout(context);
        lessonLine1.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams lessonLine1Params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lessonLine1.setLayoutParams(lessonLine1Params);

        LinearLayout.LayoutParams timeOfLessonParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        timeOfLessonParams.setMarginStart((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getContext().getResources().getDisplayMetrics()));
        timeOfLessonView.setLayoutParams(timeOfLessonParams);
        String timeOrNumberPair = (RaspisanieUtils.by.equals("group")) ?
                sdf.format(lesson.getStartTime()) + " - " + sdf.format(lesson.getEndTime())
                : lesson.getParaNumber() + " пара";
        timeOfLessonView.setText(timeOrNumberPair);
        lessonLine1.addView(timeOfLessonView);

        LinearLayout.LayoutParams nameOfLessonParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        );
        nameOfLessonParams.setMarginEnd((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getContext().getResources().getDisplayMetrics()));
        nameOfLessonView.setGravity(Gravity.CENTER);
        nameOfLessonView.setLayoutParams(nameOfLessonParams);
        nameOfLessonView.setText(lesson.getSubject());
        lessonLine1.addView(nameOfLessonView);

        addView(lessonLine1);

        LinearLayout lessonLine2 = new LinearLayout(context);
        lessonLine2.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams lessonLine2Params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lessonLine2.setLayoutParams(lessonLine2Params);

        LinearLayout.LayoutParams auditoryParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        auditoryParams.setMarginStart((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getContext().getResources().getDisplayMetrics()));
        auditoryView.setLayoutParams(auditoryParams);
        auditoryView.setText(lesson.getRoom());
        lessonLine2.addView(auditoryView);

        LinearLayout.LayoutParams typeOfLessonParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        );
        typeOfLessonParams.setMarginEnd((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getContext().getResources().getDisplayMetrics()));
        typeOfLessonView.setGravity(Gravity.CENTER);
        typeOfLessonView.setText(lesson.getLessonType());
        typeOfLessonView.setLayoutParams(typeOfLessonParams);

        lessonLine2.addView(typeOfLessonView);
        addView(lessonLine2);

        prepodOrGroupView.setGravity(Gravity.CENTER);
        StringBuilder groups = new StringBuilder();;
        if(RaspisanieUtils.by.equals("teacher")){
            for(String group : lesson.getGroups()) groups.append(group).append(" ");
        }
        String teacherOrGroups = (RaspisanieUtils.by.equals("teacher")) ?
                String.valueOf(groups) : lesson.getTeacher();
        prepodOrGroupView.setText(teacherOrGroups);
        addView(prepodOrGroupView);
    }

    private TextView createTextView(Context context, int typefaceStyle, int textSizeSp) {
        TextView textView = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        textView.setLayoutParams(params);
        textView.setTextColor(Color.WHITE);

        Typeface typeface = ResourcesCompat.getFont(context, typefaceStyle);
        textView.setTypeface(typeface);

        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp);
        return textView;
    }
}
