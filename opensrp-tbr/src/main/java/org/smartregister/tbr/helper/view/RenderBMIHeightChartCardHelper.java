package org.smartregister.tbr.helper.view;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.smartregister.tbr.R;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.model.BMIRecord;
import org.smartregister.tbr.model.BMIRecordWrapper;
import org.smartregister.tbr.repository.ResultDetailsRepository;
import org.smartregister.tbr.util.Constants;
import org.smartregister.tbr.util.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by ndegwamartin on 23/11/2017.
 */

public class RenderBMIHeightChartCardHelper extends BaseRenderHelper {

    public static final String TAG = RenderBMIHeightChartCardHelper.class.getCanonicalName();

    public RenderBMIHeightChartCardHelper(Context context, ResultDetailsRepository detailsRepository) {
        super(context, detailsRepository);
    }

    @Override
    public void renderView(final View view, final Map<String, String> patientDetails) {
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                try {
                    BMIRecordWrapper bmiRecordWrapper = getData(view.getTag(R.id.BASE_ENTITY_ID).toString());
                    if (bmiRecordWrapper.getBmiRecords().size() > 0) {
                        List<BMIRecord> bmiList = bmiRecordWrapper.getBmiRecords();
                        boolean isWeightOnly = bmiRecordWrapper.getRecordType().equals(BMIRecordWrapper.BMIRecordsTYPE.WEIGHTS);
                        if (isWeightOnly) {
                            TextView title = (TextView) view.findViewById(R.id.bmi);
                            title.setText(R.string.Weight);
                        }

                        Integer maxValue = 0;

                        List<PointValue> values = new ArrayList<>();
                        for (int i = 0; i < bmiList.size(); i++) {
                            if (isWeightOnly) {
                                values.add(new PointValue(i, bmiList.get(i).getWeight()));
                                maxValue = bmiList.get(i).getWeight() > maxValue ? bmiList.get(i).getWeight().intValue() : maxValue;

                            } else {
                                values.add(new PointValue(i, bmiList.get(i).getBmi()));
                                maxValue = bmiList.get(i).getBmi() > maxValue ? bmiList.get(i).getBmi().intValue() : maxValue;
                            }
                        }

                        processBMILineChartView(bmiList, isWeightOnly, maxValue, values, view, patientDetails);

                    } else {
                        View bmiView = ((View) view.getParent()).findViewById(R.id.clientBMIHeightChartCardView);
                        if (bmiView != null) {
                            bmiView.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

            }

        });
    }

    private void processBMILineChartView(List<BMIRecord> bmiList, boolean isWeightOnly, Integer maxValue, List<PointValue> values, View view, Map<String, String> patientDetails) {
        LineChartView bmiLineChartView = (LineChartView) view.findViewById(R.id.bmiHeightLineChartView);
        if (bmiLineChartView != null) {
            Line line = new Line(values).setColor(context.getResources().getColor(R.color.line_chart_blue)).setCubic(true);
            List<Line> lines = new ArrayList<>();
            lines.add(line);

            LineChartData data = new LineChartData();
            data.setLines(lines);
            bmiLineChartView.setLineChartData(data);

            TextView bmiStartTextView = (TextView) view.findViewById(R.id.bmiStartTextView);
            bmiStartTextView.setText(isWeightOnly ? String.valueOf(bmiList.get(0).getWeight()) + " kg" : "BMI: " + String.format("%.1f", bmiList.get(0).getBmi()));
            TextView treatmentStartDateTextView = (TextView) view.findViewById(R.id.treatmentStartDateTextView);
            treatmentStartDateTextView.setText(Utils.formatDate(org.smartregister.util.Utils.toDate(patientDetails.get(Constants.KEY.TREATMENT_INITIATION_DATE), true), "MMM yyyy") + " (" + context.getString(R.string.treatment_start) + ")");
            TextView bmiLastTextView = (TextView) view.findViewById(R.id.bmiLastTextView);

            TextView treatmentEndDateTextView = (TextView) view.findViewById(R.id.treatmentEndDateTextView);
            if (values.size() > 1) {

                bmiLastTextView.setText(isWeightOnly ? String.valueOf(bmiList.get(bmiList.size() - 1).getWeight()) + " kg" : "BMI: " + String.format("%.1f", bmiList.get(bmiList.size() - 1).getBmi()));
                treatmentEndDateTextView.setText(Utils.formatDate(Calendar.getInstance().getTime(), "dd MMM yyyy"));
                bmiLastTextView.setVisibility(View.VISIBLE);
                treatmentEndDateTextView.setVisibility(View.VISIBLE);
                resetViewport(bmiLineChartView, maxValue, values.size());
            } else {
                bmiLastTextView.setVisibility(View.GONE);
                treatmentEndDateTextView.setVisibility(View.GONE);
                resetViewport(bmiLineChartView, 100, 100);
            }
        }
    }

    private BMIRecordWrapper getData(String baseEntityId) {
        return TbrApplication.getInstance().getBmiRepository().getBMIRecords(baseEntityId);
    }

    private void resetViewport(LineChartView chart, long largestValue, long itemsCount) {
        Viewport v = chart.getMaximumViewport();
        v.set(0, largestValue + 10, itemsCount - 1, 0);
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);
        chart.setViewportCalculationEnabled(false);
    }
}
