package com.automattic.simplenote.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;

import com.automattic.simplenote.R;
import com.automattic.simplenote.widgets.CenteredImageSpan;
import com.automattic.simplenote.widgets.CheckableSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChecklistUtils {

    public static String ChecklistRegex = "- (\\[([ |x])\\])";
    public static String ChecklistRegexLineStart = "^- (\\[([ |x])\\])";
    public static String CheckedMarkdown = "- [x]";
    public static String UncheckedMarkdown = "- [ ]";
    public static final int ChecklistOffset = 4;

    /***
     * Adds CheckableSpans for matching markdown formatted checklists.
     * @param context view content.
     * @param spannable the spannable string to run the regex against.
     * @param regex the regex pattern, use either ChecklistRegex or ChecklistRegexLineStart
     * @param color the resource id of the color to tint the checklist item
     * @return ChecklistResult - resulting spannable string and result boolean
     */
    public static ChecklistResult addChecklistSpansForRegexAndColor(
            Context context,
            SpannableStringBuilder spannable,
            String regex, int color) {
        Pattern p = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher m = p.matcher(spannable);

        int positionAdjustment = 0;
        boolean result = false;
        while(m.find()) {
            result = true;
            int start = m.start() - positionAdjustment;
            int end = m.end() - positionAdjustment;

            // Safety first!
            if (end >= spannable.length()) {
                continue;
            }

            String match = m.group(1);
            CheckableSpan checkableSpan = new CheckableSpan();
            checkableSpan.setChecked(match.contains("x"));
            spannable.replace(start, end, " ");

            Drawable iconDrawable = context.getResources().getDrawable(checkableSpan.isChecked() ? R.drawable.ic_checked : R.drawable.ic_unchecked);
            iconDrawable = DrawableUtils.tintDrawableWithResource(context, iconDrawable, color);
            int iconSize = DisplayUtils.getChecklistIconSize(context);
            iconDrawable.setBounds(0, 0, iconSize, iconSize);

            CenteredImageSpan imageSpan = new CenteredImageSpan(iconDrawable);
            spannable.setSpan(imageSpan, start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(checkableSpan, start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            positionAdjustment += (end - start) - 1;
        }

        return new ChecklistResult(spannable, result);
    }

    public static class ChecklistResult {
        public SpannableStringBuilder resultStringBuilder;
        public boolean addedChecklists;

        ChecklistResult(SpannableStringBuilder stringBuilder, boolean result) {
            resultStringBuilder = stringBuilder;
            addedChecklists = result;
        }
    }
}


