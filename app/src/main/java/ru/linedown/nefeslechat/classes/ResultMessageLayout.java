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
        layoutParams.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 25, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 25, getResources().getDisplayMetrics()),
                0);
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
        fioParams.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()),
                0, 0);
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
        infoParams.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 5, getResources().getDisplayMetrics()),
                0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
        infoLabel.setLayoutParams(infoParams);

        addView(fioLabel);
        addView(infoLabel);
    }

}
