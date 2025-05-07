package ru.linedown.nefeslechat.classes;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import lombok.Getter;
import ru.linedown.nefeslechat.R;

@Getter
public class LastMessageLayout extends LinearLayout {

    private ImageView iconChatView;
    private TextView chatNameView;
    private TextView messageView;
    private LinearLayout horizontalLayout;

    private int backgroundDraw;

    private final int icon;

    public static final int STUDENT = -1;
    public static final int PREPOD = -2;
    public static final int GROUP = -3;

    private final int mode;

    public LastMessageLayout(Context context, int icon, int mode) {
        super(context);
        this.icon = icon;
        this.mode = mode;
        init(context);
    }

    private void init(Context context) {
        int dimension = (icon == R.drawable.group) ? 35 : 25;

        if(mode == STUDENT) backgroundDraw = R.drawable.bg_student_settings;
        else if(mode == PREPOD) backgroundDraw = R.drawable.bg_prepod_settings;
        else backgroundDraw = R.drawable.bg_group_chat;

        setOrientation(LinearLayout.VERTICAL);
        setId(R.id.message_view); // <------------ id сообщения класть

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 384, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics())
        );

        layoutParams.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics()), 0, 0);

        setLayoutParams(layoutParams);

        setBackground(ContextCompat.getDrawable(context, backgroundDraw));

        horizontalLayout = new LinearLayout(context);
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams horizontalParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        horizontalParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics());
        horizontalLayout.setLayoutParams(horizontalParams);
        addView(horizontalLayout);

        iconChatView = new ImageView(context);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dimension, getResources().getDisplayMetrics()),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        iconParams.setMarginStart((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, getResources().getDisplayMetrics()));
        iconChatView.setLayoutParams(iconParams);
        iconChatView.setImageDrawable(ContextCompat.getDrawable(context, icon)); // <---- меняется
        horizontalLayout.addView(iconChatView);

        chatNameView = new TextView(context);
        chatNameView.setId(R.id.chat_name_view);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        nameParams.setMarginStart((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
        nameParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics());

        chatNameView.setLayoutParams(nameParams);
        chatNameView.setText("ИВБ-111"); // <---- меняется
        chatNameView.setTextColor(ContextCompat.getColor(context, R.color.white));
        chatNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        chatNameView.setTypeface(ResourcesCompat.getFont(context, R.font.inter_bold));
        horizontalLayout.addView(chatNameView);

        messageView = new TextView(context);

        LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        messageParams.setMarginStart((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, getResources().getDisplayMetrics()));
        messageParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        messageView.setLayoutParams(messageParams);

        messageView.setText("Привет сообщения"); // <---- меняется
        messageView.setTextColor(ContextCompat.getColor(context, R.color.white));
        messageView.setTypeface(ResourcesCompat.getFont(context, R.font.inter_light));
        addView(messageView);
    }

    public void setChatName(String name) {
        chatNameView.setText(name);
    }

    public void setMessage(String message) {
        messageView.setText(message);
    }

    public void setIcon(Drawable icon) {
        iconChatView.setImageDrawable(icon);
    }

    public void setIcon(int resId) {
        iconChatView.setImageResource(resId);
    }

}
