package jp.co.akiguchilab.healthcaremanagement.util;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

public class AutoshrinkTextView extends TextView {
    private static final String TAG = AutoshrinkTextView.class.getSimpleName();

    // 最小テキストサイズ
    private static final float MIN_TEXT_SIZE = 6.0f;

    // テキスト横幅計測用Paint
    private Paint mPaint = new Paint();

    public AutoshrinkTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        float measuredWidth = getMeasuredWidth();
        if (measuredWidth > 0) {
            shrinkTextSize();
        }
    }

    // テキストサイズの自動調整
    private void shrinkTextSize() {
        // テキストサイズの取得
        float tempTextSize = getTextSize();

        //
        while (getMeasuredWidth() < getTextWidth(tempTextSize)) {
            // テキストサイズの縮小
            tempTextSize--;

            if (tempTextSize <= MIN_TEXT_SIZE) {
                // 最小サイズより小さくなった場合、最小サイズをセットする
                tempTextSize = MIN_TEXT_SIZE;
                break;
            }
        }

        // 調整したテキストサイズをセットする
        setTextSize(TypedValue.COMPLEX_UNIT_PX, tempTextSize);
    }

    // テキスト幅の取得
    float getTextWidth(float textSize) {
        mPaint.setTextSize(textSize);
        float textWidth = mPaint.measureText(getText().toString());
        return textWidth;
    }
}
