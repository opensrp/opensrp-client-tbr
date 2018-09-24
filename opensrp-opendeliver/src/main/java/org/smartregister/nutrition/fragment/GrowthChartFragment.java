package org.smartregister.nutrition.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.LineChartView;
import org.smartregister.nutrition.R;
import org.smartregister.nutrition.application.OpenDeliverApplication;
import org.smartregister.nutrition.util.Utils;
import org.smartregister.view.fragment.SecuredFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lecho.lib.hellocharts.formatter.SimpleLineChartValueFormatter;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.renderer.AbstractChartRenderer;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.Chart;

import static lecho.lib.hellocharts.util.ChartUtils.COLOR_BLUE;
import static lecho.lib.hellocharts.util.ChartUtils.COLOR_GREEN;
import static lecho.lib.hellocharts.util.ChartUtils.COLOR_ORANGE;
import static lecho.lib.hellocharts.util.ChartUtils.COLOR_RED;
import static org.smartregister.util.Utils.getValue;

/**
 * Created by MAIMOONA on 9/4/2018.
 */

public class GrowthChartFragment extends SecuredFragment {
    private LineChartView chart;
    private ToggleButton toggleChart;
    private CommonPersonObjectClient data;

    private static int MAX_X = 25;
    private static int MAX_Y = 17;
    private static int MIN_X = 0;
    private static int MIN_Y = 0;

    private static final String[] LABELS = new String[]{"Month","i", "5th","10th","25th","50th (i)","75th","90th","95th"};
    private static final int[] COLORS = new int[]{0, COLOR_GREEN, COLOR_RED,COLOR_ORANGE,COLOR_BLUE,COLOR_GREEN,COLOR_BLUE,COLOR_ORANGE,COLOR_RED};

    private static final double[][] MONTH_HEIGHT_BOYS = new double[][]{
            //{Month,Height,5th,10th,25th,50th,75th,90th,95th},
            {0,49.8842,46.77032,47.45809,48.60732,49.8842,51.16108,52.31031,52.99808},
            {1,54.7244,51.52262,52.2298,53.41147,54.7244,56.03733,57.219,57.92618},
            {2,58.4249,55.13442,55.8612,57.0756,58.4249,59.7742,60.9886,61.71538},
            {3,61.4292,58.06652,58.80924,60.0503,61.4292,62.8081,64.04916,64.79188},
            {4,63.886,60.46344,61.21939,62.48254,63.886,65.28946,66.55261,67.30856},
            {5,65.9026,62.42946,63.19658,64.4784,65.9026,67.3268,68.60862,69.37574},
            {6,67.6236,64.10314,64.88071,66.18,67.6236,69.0672,70.36649,71.14406},
            {7,69.1645,65.5934,66.38216,67.70013,69.1645,70.62887,71.94684,72.7356},
            {8,70.5994,66.97163,67.77291,69.1118,70.5994,72.087,73.42589,74.22717},
            {9,71.9687,68.27886,69.09384,70.45564,71.9687,73.48176,74.84356,75.65854},
            {10,73.2812,69.52286,70.35297,71.74005,73.2812,74.82235,76.20943,77.03954},
            {11,74.5388,70.70738,71.55363,72.96769,74.5388,76.10991,77.52397,78.37022},
            {12,75.7488,71.84023,72.70353,74.14605,75.7488,77.35155,78.79407,79.65737},
            {13,76.9186,72.92816,73.80954,75.28228,76.9186,78.55492,80.02766,80.90904},
            {14,78.0497,73.97491,74.87492,76.37879,78.0497,79.72061,81.22448,82.12449},
            {15,79.1458,74.98384,75.9031,77.43914,79.1458,80.85246,82.3885,83.30776},
            {16,80.2113,75.96033,76.89925,78.46814,80.2113,81.95446,83.52335,84.46227},
            {17,81.2487,76.90533,77.86466,79.46765,81.2487,83.02975,84.63274,85.59207},
            {18,82.2587,77.8221,78.80202,80.43942,82.2587,84.07798,85.71538,86.6953},
            {19,83.2418,78.70973,79.71074,81.38338,83.2418,85.10022,86.77286,87.77387},
            {20,84.1996,79.57106,80.59338,82.30162,84.1996,86.09758,87.80582,88.82814},
            {21,85.1348,80.40724,81.45143,83.19621,85.1348,87.07339,88.81817,89.86236},
            {22,86.0477,81.22133,82.28734,84.06859,86.0477,88.02681,89.80806,90.87407},
            {23,86.941,82.01447,83.1026,84.92082,86.941,88.96118,90.7794,91.86753},
            {24,87.8161,82.79087,83.9008,85.75545,87.8161,89.87675,91.7314,92.84133},
    };

    private static final double[][] MONTH_WEIGHT_BOYS = new double[][]{
            //{Month,Weight,5th,10th,25th,50th,75th,90th,95th},
            {0,3.3464,2.603994,2.757621,3.027282,3.3464,3.686659,4.011499,4.214527},
            {1,4.4709,3.566165,3.752603,4.080792,4.4709,4.889123,5.290726,5.542933},
            {2,5.5675,4.522344,4.738362,5.117754,5.5675,6.048448,6.509323,6.798348},
            {3,6.3762,5.240269,5.475519,5.888058,6.3762,6.897306,7.395936,7.708329},
            {4,7.0023,5.797135,6.046988,6.484777,7.0023,7.554286,8.082087,8.412602},
            {5,7.5105,6.244465,6.507016,6.966941,7.5105,8.090161,8.644384,8.991445},
            {6,7.934,6.611702,6.885864,7.366195,7.934,8.539707,9.119041,9.481939},
            {7,8.297,6.922131,7.207057,7.706413,8.297,8.927371,9.530656,9.908738},
            {8,8.6151,7.19127,7.486158,8.003205,8.6151,9.268678,9.894622,10.28713},
            {9,8.9014,7.431644,7.735837,8.26946,8.9014,9.5769,10.22433,10.63055},
            {10,9.1649,7.651572,7.964565,8.5139,9.1649,9.861313,10.5293,10.94868},
            {11,9.4122,7.857229,8.178615,8.742959,9.4122,10.12867,10.81641,11.24845},
            {12,9.6479,8.052577,8.382077,8.960956,9.6479,10.38387,11.09087,11.53526},
            {13,9.8749,8.239848,8.577324,9.170505,9.8749,10.63014,11.35618,11.81281},
            {14,10.0953,8.421033,8.76637,9.373665,10.0953,10.86959,11.61449,12.08325},
            {15,10.3108,8.597424,8.950586,9.571948,10.3108,11.10416,11.86797,12.34891},
            {16,10.5228,8.770274,9.13126,9.7667,10.5228,11.33528,12.11808,12.61125},
            {17,10.7319,8.939942,9.308795,9.958406,10.7319,11.5637,12.36571,12.87128},
            {18,10.9385,9.107002,9.483736,10.14755,10.9385,11.7897,12.61101,13.12906},
            {19,11.143,9.27136,9.656076,10.33431,11.143,12.01396,12.855,13.38579},
            {20,11.3462,9.434095,9.826848,10.51961,11.3462,12.23713,13.09811,13.64181},
            {21,11.5486,9.595435,9.996335,10.70383,11.5486,12.45983,13.3411,13.89795},
            {22,11.7504,9.755556,10.16471,10.88716,11.7504,12.6823,13.58426,14.15453},
            {23,11.9514,9.914417,10.33191,11.06946,11.9514,12.90424,13.82718,14.41108},
            {24,12.1515,10.07194,10.49784,11.25065,12.1515,13.12555,14.06979,14.66753},
    };

    @Override
    protected void onCreation() {
        Log.i(this.getClass().getName(), "onCreation");
    }

    @Override
    protected void onResumption() {
        Log.i(this.getClass().getName(), "onResumption");

        chart.setVisibility(View.GONE);

        //chart.setOnValueTouchListener(new ValueTouchListener());
        if (!toggleChart.isChecked()){
            toggleChart.setChecked(true);
        }

        try {
            generateData(chart);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.graph_growth_monitoring, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);
//        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        activity.getSupportActionBar().setTitle(R.string.negative_screened_contact);

        chart = rootView.findViewById(R.id.graph_growth_monitoring_chart);
        toggleChart = rootView.findViewById(R.id.toggleGrowthChart);

        toggleChart.setTextOn("Show height chart");
        toggleChart.setTextOff("Show weight chart");
        toggleChart.setChecked(true);

        toggleChart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked) {
                        generateData(chart);
                    } else {
                        generateData(chart);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        ((AbstractChartRenderer)chart.getChartRenderer()).DEFAULT_LABEL_MARGIN_DP = 0;
        return rootView;
    }

    private void addLine(List<Line> lines, List<PointValue> values, String lineLabel, int color, boolean showPoints){

        if(StringUtils.isNotBlank(lineLabel)){
            List pl = new ArrayList<>();
            pl.add(new PointValue(values.get(values.size()-3)).setLabel(lineLabel));
            Line l = new Line(pl);
            l.setCubic(false);
            l.setFilled(false);
            l.setHasLabels(true);
            //l.setHasLabelsOnlyForSelected(true);
            l.setHasLines(false);
            l.setHasPoints(true);
            l.setPointRadius(0);
            l.setPointColor(color);

            lines.add(l);
        }

        Line line = new Line(values);
        line.setColor(color);
        //line.setShape(shape);
        line.setCubic(false);
        line.setFilled(false);
        line.setHasLabels(showPoints);

        //line.setHasLabelsOnlyForSelected(true);
        line.setHasLines(true);
        line.setHasPoints(showPoints);

        //line.setHasGradientToTransparent(hasGradientToTransparent);
            /*if (pointsHaveDifferentColor){
                line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
            }*/
        lines.add(line);
    }

    private void generateData(LineChartView chart) throws JSONException {
        double[][] dataArray = null;
        String labelY = "none";

        if(toggleChart.isChecked()){
            dataArray = MONTH_WEIGHT_BOYS;
            MAX_Y = 17;
            MIN_Y = 0;
            labelY = "Weight";
        }
        else {
            dataArray = MONTH_HEIGHT_BOYS;
            MAX_Y = 95;
            MIN_Y = 40;
            labelY = "Height";
        }

        List<Line> lines = new ArrayList<>();
        // 0 is age and others are lines
        for (int i = 1; i < 9; i++) { // 8 lines for 7 percentiles and ideal; skip month
            List<PointValue> values = new ArrayList<>();
            List<PointValue> values2 = new ArrayList<>();

            for (int j = 0; j < MAX_X; j++) {
                if(j > dataArray.length-1){
                    break;
                }

                // plot line
                double data = dataArray[j][i];
                values.add(new PointValue(j, (float) data));
                //values2.add(new PointValue(j, (float) height));
            }

            addLine(lines, values, LABELS[i], COLORS[i], false);
            //addLine(lines, values2);
        }

        if(data != null) {
            List<JSONObject> eves = OpenDeliverApplication.getInstance().getEventClientRepository().getEventsByBaseEntityId(data.entityId());

            List<PointValue> values = new ArrayList<>();
            for (JSONObject e : eves) {
                int age = Months.monthsBetween(new DateTime(e.getString("eventDate")), new DateTime(getValue(data.getColumnmaps(), "dob", false))).getMonths();
                double dataValue = Double.parseDouble(Utils.getValueFromObs(e.getJSONArray("obs"), labelY.toLowerCase()));

                values.add(new PointValue(Math.abs(age), (float) dataValue));
            }

            addLine(lines, values, "", ChartUtils.darkenColor(ChartUtils.COLOR_VIOLET), true);
        }

        LineChartData data = new LineChartData(lines);

        if (true) { //// TODO: 9/6/2018
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (true) {//// TODO: 9/6/2018
                axisX.setName("Age (months)");
                axisY.setName(labelY);
            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

        data.setBaseValue(Float.NEGATIVE_INFINITY);
        chart.setLineChartData(data);

        // Disable viewport recalculations, see toggleCubic() method for more info.
        chart.setViewportCalculationEnabled(true);
        chart.setVisibility(View.VISIBLE);

        resetViewport(chart, MIN_Y, MAX_Y, MIN_X, MAX_X);
    }

    private void resetViewport(LineChartView chart, int bottom, int top, int left, int right) {
        // Reset viewport height range to (0,100)
        final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = bottom;
        v.top = top;
        v.left = left;
        v.right = right;
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);
    }

    public CommonPersonObjectClient getData() {
        return data;
    }

    public void setData(CommonPersonObjectClient data) {
        this.data = data;
    }
}
