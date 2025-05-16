package ru.linedown.nefeslechat.layuots;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import java.text.SimpleDateFormat;

import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.entity.MessageAllInfoDTO;
import ru.linedown.nefeslechat.entity.MessageLayoutAttributes;
// ChatTypeEnum.SELF
public class NotesLayout extends LinearLayout {
    MessageAllInfoDTO messageAllInfoDTO;
    private TextView noteView;
    private TextView timeView;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY hh:mm");

    public NotesLayout(Context context, MessageAllInfoDTO messageAllInfoDTO) {
        super(context);
        this.messageAllInfoDTO = messageAllInfoDTO;
        init();
    }

    private void init(){
        noteView = new TextView(getContext());
        timeView = new TextView(getContext());
        setId(messageAllInfoDTO.getId());
        setOrientation(VERTICAL);
        setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_green));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        layoutParams.setMargins(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()), 0
        );

        setLayoutParams(layoutParams);

        noteView.setText(messageAllInfoDTO.getMessage());
        noteView.setTextColor(Color.WHITE);
        noteView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.noto_sans_medium));

        LinearLayout.LayoutParams noteParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        noteParams.setMargins(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 9, getResources().getDisplayMetrics()), 0
        );
        noteView.setLayoutParams(noteParams);

        String createOnStr = "";
        if (messageAllInfoDTO.getCreatedAt() != null) createOnStr = sdf.format(messageAllInfoDTO.getCreatedAt());
        timeView.setText(createOnStr);
        timeView.setTextColor(Color.WHITE);
        timeView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.noto_sans_medium));

        LinearLayout.LayoutParams timeParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        timeParams.setMargins(
                0, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()), 0
        );
        layoutParams.gravity = Gravity.END;
        timeView.setLayoutParams(timeParams);

        addView(noteView);
        addView(timeView);
    }

    public void setTextInMessage(String text){
        noteView.setText(text);
    }
}
