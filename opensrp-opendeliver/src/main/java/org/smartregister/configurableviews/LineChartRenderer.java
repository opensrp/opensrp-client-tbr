package org.smartregister.configurableviews;

import android.content.Context;

import lecho.lib.hellocharts.provider.LineChartDataProvider;
import lecho.lib.hellocharts.view.Chart;

public class LineChartRenderer extends lecho.lib.hellocharts.renderer.LineChartRenderer {
    public LineChartRenderer(Context context, Chart chart, LineChartDataProvider dataProvider) {
        super(context, chart, dataProvider);
        this.labelMargin = 0;
        this.labelOffset = 0;
    }
}
