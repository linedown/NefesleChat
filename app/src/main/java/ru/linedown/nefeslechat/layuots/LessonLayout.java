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

import ru.linedown.nefeslechat.R;

public class LessonLayout extends LinearLayout {
    TextView timeOfLessonView;
    TextView nameOfLessonView;
    TextView auditoryView;
    TextView typeOfLessonView;
    TextView prepodOrGroupView;

    public LessonLayout(Context context){
        super(context);
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
        lessonLine1.addView(timeOfLessonView);

        LinearLayout.LayoutParams nameOfLessonParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        );
        nameOfLessonParams.setMarginEnd((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getContext().getResources().getDisplayMetrics()));
        nameOfLessonView.setGravity(Gravity.CENTER);
        nameOfLessonView.setLayoutParams(nameOfLessonParams);
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
        lessonLine2.addView(auditoryView);

        LinearLayout.LayoutParams typeOfLessonParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        );
        typeOfLessonParams.setMarginEnd((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getContext().getResources().getDisplayMetrics()));
        typeOfLessonView.setGravity(Gravity.CENTER);
        typeOfLessonView.setLayoutParams(typeOfLessonParams);

        lessonLine2.addView(typeOfLessonView);
        addView(lessonLine2);

        prepodOrGroupView.setGravity(Gravity.CENTER);
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

    public void addLesson(String time, String name, String auditory, String type, String prepodOrGroup) {
        setTimeOfLessonText(time);
        setNameOfLessonText(name);
        setAuditoryText(auditory);
        setTypeOfLessonText(type);
        setPrepodOrGroupText(prepodOrGroup);
    }

    public void setTimeOfLessonText(String text) {
        timeOfLessonView.setText(text);
    }

    public void setNameOfLessonText(String text) {
        nameOfLessonView.setText(text);
    }

    public void setAuditoryText(String text) {
        auditoryView.setText(text);
    }

    public void setTypeOfLessonText(String text) {
        typeOfLessonView.setText(text);
    }

    public void setPrepodOrGroupText(String text) {
        prepodOrGroupView.setText(text);
    }
}
