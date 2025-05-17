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

import java.util.List;

import ru.linedown.nefeslechat.R;

public class DayOfWeekLayout extends LinearLayout {
    private TextView dayOfWeekView;

    public DayOfWeekLayout(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);

        dayOfWeekView = createTextView(context, R.font.inter_bold, 16);
        dayOfWeekView.setGravity(Gravity.CENTER);
        addView(dayOfWeekView);



        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getContext().getResources().getDisplayMetrics()),
                0, 0);
        setLayoutParams(layoutParams);
        setBackground(ContextCompat.getDrawable(context, R.drawable.bg_green));
        setOrientation(VERTICAL);
    }


    public void addLesson(LessonLayout lessonLayout){
        addView(lessonLayout);
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

}
