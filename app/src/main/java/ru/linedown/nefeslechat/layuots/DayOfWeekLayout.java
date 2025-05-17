package ru.linedown.nefeslechat.layuots;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import ru.linedown.nefeslechat.R;

public class DayOfWeekLayout extends LinearLayout {

    private TextView dayOfWeekView;
    private LinearLayout lessonLayout;
    private TextView timeOfLessonView;
    private TextView nameOfLessonView;
    private TextView auditoryView;
    private TextView typeOfLessonView;
    private TextView prepodView;

    public DayOfWeekLayout(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);

        // Создаем TextView для дня недели
        dayOfWeekView = createTextView(context, R.font.inter_bold, 16);
        dayOfWeekView.setGravity(Gravity.CENTER);
        addView(dayOfWeekView);

        // Создаем LinearLayout для информации об уроке
        lessonLayout = new LinearLayout(context);
        lessonLayout.setOrientation(VERTICAL);
        LinearLayout.LayoutParams lessonLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lessonLayoutParams.setMargins(0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getContext().getResources().getDisplayMetrics()),
                0, 0); // marginTop: 10sp
        lessonLayout.setLayoutParams(lessonLayoutParams);
        addView(lessonLayout);

        // Создаем элементы LinearLayout для информации об уроке
        timeOfLessonView = createTextView(context, R.font.inter_bold, 14);
        auditoryView = createTextView(context, R.font.inter_bold, 14);

        nameOfLessonView = createTextView(context, R.font.inter_bold, 14);
        typeOfLessonView = createTextView(context, R.font.inter_light, 14);
        prepodView = createTextView(context, R.font.inter_light, 14);

        //Добавляем layout для первой линии урока
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

        lessonLayout.addView(lessonLine1);

        //Добавляем layout для второй линии урока
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
        lessonLayout.addView(lessonLine2);

        prepodView.setGravity(Gravity.CENTER);
        lessonLayout.addView(prepodView);


        // Настройка внешнего вида LinearLayout
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getContext().getResources().getDisplayMetrics()),
                0, 0); // marginTop: 10dp
        setLayoutParams(layoutParams);
        setMinimumHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 200, getContext().getResources().getDisplayMetrics())); // minHeight: 200dp
        setBackground(ContextCompat.getDrawable(context, R.drawable.bg_green)); // background: @drawable/bg_green
        setOrientation(VERTICAL);

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

    public void setDayOfWeekText(String text) {
        dayOfWeekView.setText(text);
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

    public void setPrepodText(String text) {
        prepodView.setText(text);
    }

    // Метод для добавления информации об уроках.  Можно перегрузить, чтобы принимать объекты данных Lesson

    public void addLesson(String time, String name, String auditory, String type, String prepod) {
        setTimeOfLessonText(time);
        setNameOfLessonText(name);
        setAuditoryText(auditory);
        setTypeOfLessonText(type);
        setPrepodText(prepod);
    }

}
