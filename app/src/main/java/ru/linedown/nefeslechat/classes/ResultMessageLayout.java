package ru.linedown.nefeslechat.classes;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import lombok.Getter;
import lombok.Setter;
import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.entity.ResultMessageAttributes;


@Getter
public class ResultMessageLayout extends LinearLayout {

    private TextView fioLabel;
    private TextView infoLabel;
    @Setter
    private ResultMessageAttributes attributes;

    public ResultMessageLayout(Context context, ResultMessageAttributes attributes) {
        super(context);
        this.attributes = attributes;
        init();
    }

    public ResultMessageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ResultMessageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        fioLabel = new TextView(getContext());
        infoLabel = new TextView(getContext());

        setOrientation(LinearLayout.VERTICAL);

        Drawable background = ContextCompat.getDrawable(getContext(), attributes.getBackgroundResource());
        setBackground(background);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(dpToPx(25), dpToPx(10), dpToPx(25), 0);
        setLayoutParams(layoutParams);

        fioLabel.setId(attributes.getId());
        fioLabel.setText(attributes.getFio());
        fioLabel.setTextColor(Color.WHITE);
        fioLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        Typeface fioTypeface = ResourcesCompat.getFont(getContext(), R.font.inter_medium);
        fioLabel.setTypeface(fioTypeface);
        LinearLayout.LayoutParams fioParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        fioParams.setMargins(dpToPx(20), dpToPx(10), 0, 0);
        fioLabel.setLayoutParams(fioParams);

        infoLabel.setId(generateViewId());
        infoLabel.setText(attributes.getInfo());
        infoLabel.setTextColor(Color.WHITE);
        infoLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        Typeface infoTypeface = ResourcesCompat.getFont(getContext(), R.font.inter_light);
        infoLabel.setTypeface(infoTypeface);

        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        infoParams.setMargins(dpToPx(20), dpToPx(5), 0, dpToPx(10));
        infoLabel.setLayoutParams(infoParams);

        addView(fioLabel);
        addView(infoLabel);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getContext().getResources().getDisplayMetrics().density);
    }

}
