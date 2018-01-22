package org.smartregister.tbr.helper.view;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.google.common.collect.ImmutableMap;

import org.smartregister.tbr.R;
import org.smartregister.tbr.model.Result;
import org.smartregister.tbr.repository.ResultsRepository;
import org.smartregister.tbr.util.Constants;
import org.smartregister.tbr.util.Utils;

import java.util.Date;
import java.util.Map;

import util.TbrConstants;
import util.TbrSpannableStringBuilder;

/**
 * Created by ndegwamartin on 23/11/2017.
 */

public class RenderPositiveResultsCardHelper extends BaseRenderHelper {
    private TestResultsStringBuilderHelper testResultsStringBuilderHelper;

    public RenderPositiveResultsCardHelper(Context context, ResultsRepository detailsRepository) {
        super(context, detailsRepository);
        testResultsStringBuilderHelper = new TestResultsStringBuilderHelper(context);
    }

    @Override
    public void renderView(final View view, final Map<String, String> extra) {
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                String baseEntityId = extra.get(Constants.KEY._ID);
                view.setTag(R.id.BASE_ENTITY_ID, baseEntityId);


                if (view.getTag(R.id.VIEW_CONFIGURATION_ID) == Constants.CONFIGURATION.INTREATMENT_PATIENT_DETAILS) {

                    InitializeRenderParams params = new InitializeRenderParams(extra, view, true, true);//baseline
                    initializeRenderLayout(params);

                    params = new InitializeRenderParams(extra, view, true, false); //latest
                    initializeRenderLayout(params);
                    view.findViewById(R.id.baselineHorizontalDividerView).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.baselineTextView).setVisibility(View.VISIBLE);

                } else {

                    InitializeRenderParams params = new InitializeRenderParams(extra, view, false, false);
                    initializeRenderLayout(params);
                }

            }

        });

    }

    private void initializeRenderLayout(InitializeRenderParams params) {

        Map<String, Result> testResults = getTestResults(params);

        TextView firstEncounterDateView = getFirstEncounterDateView(params);

        if (hasFirstEncounter(params)) {

            String firstEncounterDate = Utils.formatDate(org.smartregister.util.Utils.toDate(params.extra.get(Constants.KEY.FIRST_ENCOUNTER).toString(), true), "dd MMM yyyy");
            String dateString = context.getString(R.string.first_encounter) + Constants.CHAR.SPACE + firstEncounterDate;
            firstEncounterDateView.setText(dateString);
        } else if (params.isIntreatment) {
            String baseLineText = "Baseline (treatment started " + Utils.getTimeAgo(params.extra.get(Constants.KEY.TREATMENT_INITIATION_DATE)) + ")";
            firstEncounterDateView.setText(params.isBaseline ? baseLineText : context.getString(R.string.latest));
        }

        TextView results = getBaselineTextView(params);

        TbrSpannableStringBuilder stringBuilder = getConstructedStringBuilder(testResults, params);

        if (stringBuilder.length() > 0) {
            results.setVisibility(View.VISIBLE);
            params.view.findViewById(params.isBaseline ? R.id.baseline_no_results_recorded : R.id.no_results_recorded).setVisibility(View.GONE);
            results.setText(stringBuilder);
        } else {
            results.setVisibility(View.GONE);
            params.view.findViewById(params.isBaseline ? R.id.baseline_result_details : R.id.no_results_recorded).setVisibility(View.VISIBLE);

        }
    }

    private TextView getBaselineTextView(InitializeRenderParams params) {
        return (TextView) params.view.findViewById(params.isBaseline ? R.id.baseline_result_details : R.id.result_details);
    }

    private TextView getFirstEncounterDateView(InitializeRenderParams params) {
        return (TextView) params.view.findViewById(params.isBaseline ? R.id.baselineTextView : R.id.firstEncounterDateTextView);
    }

    private boolean hasFirstEncounter(InitializeRenderParams params) {
        return !params.isIntreatment && params.extra.containsKey(Constants.KEY.FIRST_ENCOUNTER) && !params.extra.get(Constants.KEY.FIRST_ENCOUNTER).isEmpty();
    }

    private Map<String, Result> getTestResults(InitializeRenderParams params) {
        return params.isBaseline ? getBaselineTestResults(params) : getLatestResults(params);
    }

    private Map<String, Result> getLatestResults(InitializeRenderParams params) {
        return ((ResultsRepository) repository).getLatestResultsAll(params.view.getTag(R.id.BASE_ENTITY_ID).toString(), false, null);
    }

    private Map<String, Result> getBaselineTestResults(InitializeRenderParams params) {
        return ((ResultsRepository) repository).getLatestResultsAll(params.view.getTag(R.id.BASE_ENTITY_ID).toString(), false, Long.valueOf(params.extra.get(TbrConstants.KEY.BASELINE)));
    }

    private TbrSpannableStringBuilder getResultPrefixBuilder(InitializeRenderParams params, TbrSpannableStringBuilder stringBuilder, Date dateTestGiven) {
        if (params.isIntreatment && !params.isBaseline) {
            Date dateTreatmentInitiatied = org.smartregister.util.Utils.toDate(params.extra.get(TbrConstants.KEY.TREATMENT_INITIATION_DATE), true);
            stringBuilder.append(Utils.formatDate(dateTestGiven, "dd MMM") + " (M" + Utils.getMonthCountFromDate(dateTreatmentInitiatied, dateTestGiven) + ")" + Constants.CHAR.COLON + Constants.CHAR.SPACE);
        }
        return stringBuilder;
    }

    private TbrSpannableStringBuilder getConstructedStringBuilder(Map<String, Result> testResults, InitializeRenderParams params) {
        TbrSpannableStringBuilder stringBuilder = new TbrSpannableStringBuilder();

        for (Map.Entry<String, Result> entry : testResults.entrySet()) {
            if (testResults.containsKey(entry.getKey()) && !entry.getKey().equals(TbrConstants.RESULT.RIF_RESULT)) {
                stringBuilder = getResultPrefixBuilder(params, stringBuilder, testResults.get(entry.getKey()).getDate());
                switch (entry.getKey()) {
                    case TbrConstants.RESULT.MTB_RESULT:
                        if (testResults.get(TbrConstants.RESULT.RIF_RESULT) != null) {
                            testResultsStringBuilderHelper.getXpertResultStringBuilder(ImmutableMap.of(entry.getKey(), testResults.get(entry.getKey()).getValue1(), TbrConstants.RESULT.RIF_RESULT, testResults.get(TbrConstants.RESULT.RIF_RESULT).getValue1()), stringBuilder, false);

                        } else {
                            testResultsStringBuilderHelper.getXpertResultStringBuilder(ImmutableMap.of(entry.getKey(), testResults.get(entry.getKey()).getValue1()), stringBuilder, false);
                        }
                        break;
                    case TbrConstants.RESULT.TEST_RESULT:
                        stringBuilder = testResultsStringBuilderHelper.getSmearResultStringBuilder(ImmutableMap.of(entry.getKey(), testResults.get(entry.getKey()).getValue1()), stringBuilder);
                        break;
                    case TbrConstants.RESULT.XRAY_RESULT:
                        stringBuilder = testResultsStringBuilderHelper.getXRayResultStringBuilder(ImmutableMap.of(entry.getKey(), testResults.get(entry.getKey()).getValue1()), stringBuilder);
                        break;
                    case TbrConstants.RESULT.CULTURE_RESULT:
                        stringBuilder = testResultsStringBuilderHelper.getCultureResultStringBuilder(ImmutableMap.of(entry.getKey(), testResults.get(entry.getKey()).getValue1()), stringBuilder);
                        break;
                    default:
                        break;

                }
            }

        }
        return stringBuilder;
    }

    private class InitializeRenderParams {
        public Map<String, String> extra;
        public View view;
        public boolean isBaseline;
        public boolean isIntreatment;

        public InitializeRenderParams(Map<String, String> extra, View view, boolean isIntreatment, boolean isBaseline) {
            this.extra = extra;
            this.view = view;
            this.isBaseline = isBaseline;
            this.isIntreatment = isIntreatment;
        }

    }

}
