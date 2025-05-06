package ru.linedown.nefeslechat.classes;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import lombok.Getter;
import ru.linedown.nefeslechat.R;

@Getter
public class LastMessageLayout extends ConstraintLayout {

    private ImageView iconChatView;
    private TextView chatNameView;
    private TextView messageView;

    public LastMessageLayout(Context context) {
        super(context);
        init(context, null);
    }

    public LastMessageLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LastMessageLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        // Устанавливаем параметры, как в XML
        setId(R.id.lastMessageLayout1);  //ВАЖНО: Убедитесь, что у вас нет конфликта ID. Лучше генерировать ID динамически, если это компонент для повторного использования.
        setLayoutParams(new ViewGroup.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 384, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics())
        ));
        setPadding(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics()), 0, 0);  // marginTop реализован через padding.
        setBackground(ContextCompat.getDrawable(context, R.drawable.bg_group_chat));

        // Создаем и настраиваем ImageView
        iconChatView = new ImageView(context);
        iconChatView.setId(R.id.icon_chat_view); //ВАЖНО: Убедитесь, что у вас нет конфликта ID. Лучше генерировать ID динамически, если это компонент для повторного использования.
        iconChatView.setLayoutParams(new ViewGroup.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 38, getResources().getDisplayMetrics())
        ));
        iconChatView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.group));
        addView(iconChatView);

        // Создаем и настраиваем TextView для chatName
        chatNameView = new TextView(context);
        chatNameView.setId(R.id.chat_name_view);  //ВАЖНО: Убедитесь, что у вас нет конфликта ID. Лучше генерировать ID динамически, если это компонент для повторного использования.
        chatNameView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        chatNameView.setText("ИВБ-111");
        chatNameView.setTextColor(ContextCompat.getColor(context, R.color.white));
        chatNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        chatNameView.setTypeface(ResourcesCompat.getFont(context, R.font.inter_bold));
        addView(chatNameView);

        // Создаем и настраиваем TextView для message
        messageView = new TextView(context);
        messageView.setId(R.id.message_view);  //ВАЖНО: Убедитесь, что у вас нет конфликта ID. Лучше генерировать ID динамически, если это компонент для повторного использования.
        messageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        messageView.setText("Привет сообщения");
        messageView.setTextColor(ContextCompat.getColor(context, R.color.white));
        messageView.setTypeface(ResourcesCompat.getFont(context, R.font.inter_light));
        addView(messageView);

        // Применяем ConstraintSet для позиционирования
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this);

        // Constraints для iconChatView
        constraintSet.connect(iconChatView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28, getResources().getDisplayMetrics()));
        constraintSet.connect(iconChatView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.connect(iconChatView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

        // Constraints для chatNameView
        constraintSet.connect(chatNameView.getId(), ConstraintSet.START, iconChatView.getId(), ConstraintSet.END, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));
        constraintSet.connect(chatNameView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));

        // Constraints для messageView
        constraintSet.connect(messageView.getId(), ConstraintSet.START, iconChatView.getId(), ConstraintSet.END, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));
        constraintSet.connect(messageView.getId(), ConstraintSet.TOP, chatNameView.getId(), ConstraintSet.BOTTOM, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));

        // Constraints для самого ConstraintLayout
        constraintSet.connect(getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.connect(getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);


        constraintSet.applyTo(this);

    }


    // Методы для динамического изменения данных
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

