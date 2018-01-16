package org.smartregister.tbr.helper.view;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import org.smartregister.tbr.R;
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
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by ndegwamartin on 23/11/2017.
 */

public class RenderBMIHeightChartCardHelper extends BaseRenderHelper {

    public RenderBMIHeightChartCardHelper(Context context, ResultDetailsRepository detailsRepository) {
        super(context, detailsRepository);
    }

    @Override
    public void renderView(final View view, final Map<String, String> patientDetails) {
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                List<Float> bmiList = getDummyBMIData();
                List<PointValue> values = new ArrayList<>();
                for (int i = 0; i < bmiList.size(); i++) {
                    values.add(new PointValue(i, bmiList.get(i)));
                }

                LineChartView bmiLineChartView = (LineChartView) view.findViewById(R.id.bmiHeightLineChartView);
                Line line = new Line(values).setColor(context.getResources().getColor(R.color.line_chart_blue)).setCubic(true);
                List<Line> lines = new ArrayList<>();
                lines.add(line);

                LineChartData data = new LineChartData();
                data.setLines(lines);
                bmiLineChartView.setLineChartData(data);

                TextView bmiStartTextView = (TextView) view.findViewById(R.id.bmiStartTextView);
                bmiStartTextView.setText("BMI: " + String.valueOf(bmiList.get(0)));
                TextView bmiLastTextView = (TextView) view.findViewById(R.id.bmiLastTextView);
                bmiLastTextView.setText("BMI: " + String.valueOf(bmiList.get(bmiList.size() - 1)));
                TextView treatmentStartDateTextView = (TextView) view.findViewById(R.id.treatmentStartDateTextView);
                TextView treatmentEndDateTextView = (TextView) view.findViewById(R.id.treatmentEndDateTextView);
                treatmentStartDateTextView.setText(Utils.formatDate(org.smartregister.util.Utils.toDate(patientDetails.get(Constants.KEY.TREATMENT_INITIATION_DATE).toString(), true), "MMM yyyy") + " (" + context.getString(R.string.treatment_start) + ")");
                treatmentEndDateTextView.setText(Utils.formatDate(Calendar.getInstance().getTime(), "dd MMM yyyy"));

            }

        });
    }

    private List<Float> getDummyBMIData() {

        List<Float> bmiList = new ArrayList<>();
        bmiList.add(17.6f);
        bmiList.add(17.7f);
        bmiList.add(17.8f);
        bmiList.add(17.9f);
        bmiList.add(18.1f);
        bmiList.add(18.3f);
        bmiList.add(18.5f);
        bmiList.add(18.9f);
        return bmiList;
    }
}
