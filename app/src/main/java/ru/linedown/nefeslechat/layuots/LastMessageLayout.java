
package ru.linedown.nefeslechat.layuots;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import java.text.SimpleDateFormat;

import lombok.Getter;
import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.entity.LastMessageAttributes;

@Getter
public class LastMessageLayout extends LinearLayout {

    private ImageView iconChatView;
    private TextView chatNameView;
    private TextView messageView;
    private TextView dateView;
    private LinearLayout horizontalLayout;

    private int icon;

    public static final int STUDENT = -1;
    public static final int PREPOD = -2;
    public static final int GROUP = -3;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY hh:mm");

    LastMessageAttributes lma;

    public LastMessageLayout(Context context, LastMessageAttributes lma) {
        super(context);
        this.lma = lma;
        init(context);
    }

    public LastMessageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LastMessageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        String createOnStr;
        if (lma.getCreatedOn() != null) createOnStr = sdf.format(lma.getCreatedOn());
        else createOnStr = "";

        Log.d("Дата отправки соо: ", createOnStr);

        if (lma.getChatType() == STUDENT) icon = R.drawable.man;
        else if (lma.getChatType() == PREPOD) icon = R.drawable.prepod;
        else icon = R.drawable.group;

        int dimension = (icon == R.drawable.group) ? 35 : 25;
        int marginTop = (icon == R.drawable.prepod) ? 5 : 0;

        setOrientation(LinearLayout.VERTICAL);
        setId(lma.getChatId());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics())
        );

        layoutParams.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()),
                0);

        setLayoutParams(layoutParams);

        if (lma.isReadChat())
            setBackground(ContextCompat.getDrawable(context, R.drawable.bg_read_chat));
        else setBackground(ContextCompat.getDrawable(context, R.drawable.bg_green));

        horizontalLayout = new LinearLayout(context);
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams horizontalParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, // <-- Изменено здесь!
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        horizontalParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics());
        horizontalLayout.setLayoutParams(horizontalParams);
        addView(horizontalLayout);

        iconChatView = new ImageView(context);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dimension, getResources().getDisplayMetrics()),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        iconParams.setMarginStart((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 35, getResources().getDisplayMetrics()));
        iconParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, marginTop, getResources().getDisplayMetrics());
        iconChatView.setLayoutParams(iconParams);
        iconChatView.setImageDrawable(ContextCompat.getDrawable(context, icon));
        horizontalLayout.addView(iconChatView);

        chatNameView = new TextView(context);
        chatNameView.setId(R.id.chat_name_view);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        nameParams.setMarginStart((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 5, getResources().getDisplayMetrics()));
        nameParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 6, getResources().getDisplayMetrics());

        chatNameView.setLayoutParams(nameParams);
        chatNameView.setText(lma.getNameChat());
        chatNameView.setTextColor(ContextCompat.getColor(context, R.color.white));
        chatNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        chatNameView.setTypeface(ResourcesCompat.getFont(context, R.font.inter_bold));
        horizontalLayout.addView(chatNameView);

        LinearLayout.LayoutParams spaceParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT,1f
        );

        LinearLayout space = new LinearLayout(context);
        space.setLayoutParams(spaceParams);
        horizontalLayout.addView(space);

        dateView = new TextView(context);
        LinearLayout.LayoutParams dateParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        dateParams.setMargins(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 4, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()), 0);
        dateView.setLayoutParams(dateParams);

        dateView.setText(createOnStr);
        dateView.setTextColor(Color.WHITE);
        dateView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        dateView.setTypeface(ResourcesCompat.getFont(context, R.font.inter_bold));

        horizontalLayout.addView(dateView);

        messageView = new TextView(context);

        LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        messageParams.setMarginStart((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 35, getResources().getDisplayMetrics()));
        messageParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 4, getResources().getDisplayMetrics());
        messageView.setLayoutParams(messageParams);

        messageView.setText(lma.getText());
        messageView.setTextColor(ContextCompat.getColor(context, R.color.white));
        messageView.setTypeface(ResourcesCompat.getFont(context, R.font.inter_light));
        addView(messageView);
    }
}
