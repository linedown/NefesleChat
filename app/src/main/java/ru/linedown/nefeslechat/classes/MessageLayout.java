package ru.linedown.nefeslechat.classes;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;
import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.entity.MessageLayoutAttributes;

@Getter
public class MessageLayout extends LinearLayout {
    public static final int ME = -1;
    public static final int COMPANION = -2;

    private TextView messageView;
    private ImageView fileView;
    private TextView timeView;
    private int marginStart;
    private MessageLayoutAttributes messageLayoutAttributes;
    private final SimpleDateFormat formatDate = new SimpleDateFormat("hh:mm");

    public MessageLayout(Context context, int typeUser, MessageLayoutAttributes messageLayoutAttributes) {
        super(context);
        marginStart = (typeUser == ME) ? 190 : 10;
        this.messageLayoutAttributes = messageLayoutAttributes;
        init();
    }

    public MessageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MessageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        boolean isText = !messageLayoutAttributes.getMessage().isBlank();
        setId(messageLayoutAttributes.getId());
        setOrientation(VERTICAL);
        setBackgroundResource(R.drawable.bg_green);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                dpToPx(209),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(dpToPx(marginStart), dpToPx(15), dpToPx(10), 0);
        setLayoutParams(layoutParams);

        messageView = new TextView(getContext());
        messageView.setText(messageLayoutAttributes.getMessage());
        messageView.setTextColor(Color.WHITE);
        messageView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        messageView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.inter_light));

        LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        messageParams.setMargins(dpToPx(20), dpToPx(5), dpToPx(27), 0);
        messageView.setLayoutParams(messageParams);
        addView(messageView);

        fileView = new ImageView(getContext());
        fileView.setImageResource(R.drawable.file);
        if(isText) fileView.setVisibility(GONE);
        else fileView.setVisibility(VISIBLE);

        LinearLayout.LayoutParams fileParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        fileParams.setMargins(0, dpToPx(10), 0, 0);
        fileView.setLayoutParams(fileParams);
        addView(fileView);

        Date timeSend = messageLayoutAttributes.getSendDate();
        String timeSendStr = formatDate.format(timeSend);

        timeView = new TextView(getContext());
        timeView.setText(timeSendStr);
        timeView.setTextColor(Color.WHITE);
        timeView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        timeView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.inter_bold));
        LinearLayout.LayoutParams timeParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        timeParams.setMargins(dpToPx(160), dpToPx(5), 0, dpToPx(5));
        timeView.setLayoutParams(timeParams);
        addView(timeView);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
