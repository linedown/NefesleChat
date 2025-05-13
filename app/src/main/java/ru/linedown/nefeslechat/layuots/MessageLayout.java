package ru.linedown.nefeslechat.layuots;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    private TextView senderNameView;
    private TextView messageView;
    private ImageView fileView;
    private TextView timeView;
    private MessageLayoutAttributes messageLayoutAttributes;
    private int typeUser;
    private final SimpleDateFormat formatDate = new SimpleDateFormat("hh:mm");

    public MessageLayout(Context context, int typeUser, MessageLayoutAttributes messageLayoutAttributes) {
        super(context);
        this.typeUser = typeUser;
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
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 209, getContext().getResources().getDisplayMetrics()),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getContext().getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getContext().getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getContext().getResources().getDisplayMetrics()),
                0);
        layoutParams.gravity = (typeUser == ME) ? Gravity.END : Gravity.START;
        setLayoutParams(layoutParams);

        senderNameView = new TextView(getContext());
        senderNameView.setText(messageLayoutAttributes.getSenderName());
        senderNameView.setTextColor(Color.WHITE);
        senderNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        senderNameView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.inter_bold));

        LinearLayout.LayoutParams senderNameParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        senderNameParams.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getContext().getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 6, getContext().getResources().getDisplayMetrics()),
                0, 0);

        senderNameView.setLayoutParams(senderNameParams);
        if(messageLayoutAttributes.getChatType().equals("Single")) senderNameView.setVisibility(GONE);

        addView(senderNameView);

        messageView = new TextView(getContext());
        messageView.setText(messageLayoutAttributes.getMessage());
        messageView.setTextColor(Color.WHITE);
        messageView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        messageView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.inter_light));

        LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        messageParams.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getContext().getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 5, getContext().getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 27, getContext().getResources().getDisplayMetrics()),
                0);
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
        fileParams.setMargins(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getContext().getResources().getDisplayMetrics()), 0, 0);
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
        timeParams.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 160, getContext().getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 5, getContext().getResources().getDisplayMetrics()),
                0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 5, getContext().getResources().getDisplayMetrics()));
        timeView.setLayoutParams(timeParams);
        addView(timeView);
    }

    public void setTextInMessage(String text){
        messageView.setText(text);
    }
}
