package org.smartregister.configurableviews;

import android.content.Context;
import android.util.AttributeSet;

public class LineChartView extends lecho.lib.hellocharts.view.LineChartView {
    public LineChartView(Context context) {
        this(context, (AttributeSet)null, 0);
    }

    public LineChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setChartRenderer(new LineChartRenderer(context, this, this));
    }
}
