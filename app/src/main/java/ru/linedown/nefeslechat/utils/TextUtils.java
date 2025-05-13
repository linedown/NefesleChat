package ru.linedown.nefeslechat.utils;

import android.text.Html;
import android.widget.TextView;

public class TextUtils {
    public static void setUnderlinedText(TextView textView, String text) {
        String underlinedText = "<u>" + text + "</u>";
        textView.setText(Html.fromHtml(underlinedText, Html.FROM_HTML_MODE_LEGACY));
    }
}
