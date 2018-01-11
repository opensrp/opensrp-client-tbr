package org.smartregister.tbr.helper.view;

import android.content.Context;
import android.os.Handler;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.google.common.collect.ImmutableMap;

import org.apache.commons.lang3.text.WordUtils;
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
    private static final String DETECTED = "detected";
    private static final String NOT_DETECTED = "not_detected";
    private static final String INDETERMINATE = "indeterminate";

    public RenderPositiveResultsCardHelper(Context context, ResultsRepository detailsRepository) {
        super(context, detailsRepository);
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


                } else {

                    InitializeRenderParams params = new InitializeRenderParams(extra, view, false, false);
                    initializeRenderLayout(params);
                }

            }

        });

    }

    private void initializeRenderLayout(InitializeRenderParams params) {

        Map<String, Result> testResults = params.isBaseline ?
                ((ResultsRepository) repository).getLatestResultsAll(params.view.getTag(R.id.BASE_ENTITY_ID).toString(), false, Long.valueOf(params.extra.get(TbrConstants.KEY.BASELINE)))
                : ((ResultsRepository) repository).getLatestResultsAll(params.view.getTag(R.id.BASE_ENTITY_ID).toString(), false, null);


        TextView firstEncounterDateView = (TextView) params.view.findViewById(params.isBaseline ? R.id.baselineTextView : R.id.firstEncounterDateTextView);
        if (!params.isIntreatment) {
            String dateString = context.getString(R.string.first_encounter);
            if (params.extra.containsKey(Constants.KEY.FIRST_ENCOUNTER) && !params.extra.get(Constants.KEY.FIRST_ENCOUNTER).isEmpty()) {
                dateString += Constants.CHAR.SPACE + Utils.formatDate(org.smartregister.util.Utils.toDate(params.extra.get(Constants.KEY.FIRST_ENCOUNTER).toString(), true), "dd MMM yyyy");
            }
            firstEncounterDateView.setText(dateString);
        } else if (params.isIntreatment && !params.isBaseline) {
            firstEncounterDateView.setText(R.string.latest);

        } else if (params.isIntreatment && params.isBaseline) {
            firstEncounterDateView.setText("Baseline (treatment started " + Utils.getTimeAgo(params.extra.get(Constants.KEY.TREATMENT_INITIATION_DATE)) + ")");

        }
        TextView results = params.isBaseline ? (TextView) params.view.findViewById(R.id.baseline_result_details) : (TextView) params.view.findViewById(R.id.result_details);

        TbrSpannableStringBuilder stringBuilder = new TbrSpannableStringBuilder();

        if (testResults.containsKey(TbrConstants.RESULT.MTB_RESULT)) {
            stringBuilder = getResultPrefixBuilder(params, stringBuilder, testResults.get(TbrConstants.RESULT.MTB_RESULT).getDate());
            getXpertResultStringBuilder(ImmutableMap.of(TbrConstants.RESULT.MTB_RESULT, testResults.get(TbrConstants.RESULT.MTB_RESULT).getValue1(), TbrConstants.RESULT.RIF_RESULT, testResults.get(TbrConstants.RESULT.RIF_RESULT).getValue1()), stringBuilder, false);
            stringBuilder.append("\n");
        }

        if (testResults.containsKey(TbrConstants.RESULT.TEST_RESULT)) {
            stringBuilder = getResultPrefixBuilder(params, stringBuilder, testResults.get(TbrConstants.RESULT.TEST_RESULT).getDate());
            stringBuilder = getSmearResultStringBuilder(ImmutableMap.of(TbrConstants.RESULT.TEST_RESULT, testResults.get(TbrConstants.RESULT.TEST_RESULT).getValue1()), stringBuilder);
            stringBuilder.append("\n");

        }

        if (testResults.containsKey(TbrConstants.RESULT.CULTURE_RESULT)) {
            stringBuilder = getResultPrefixBuilder(params, stringBuilder, testResults.get(TbrConstants.RESULT.CULTURE_RESULT).getDate());
            stringBuilder = getCultureResultStringBuilder(ImmutableMap.of(TbrConstants.RESULT.CULTURE_RESULT, testResults.get(TbrConstants.RESULT.CULTURE_RESULT).getValue1()), stringBuilder);
            stringBuilder.append("\n");
        }
        if (testResults.containsKey(TbrConstants.RESULT.XRAY_RESULT)) {
            stringBuilder = getResultPrefixBuilder(params, stringBuilder, testResults.get(TbrConstants.RESULT.XRAY_RESULT).getDate());
            stringBuilder = getXRayResultStringBuilder(ImmutableMap.of(TbrConstants.RESULT.XRAY_RESULT, testResults.get(TbrConstants.RESULT.XRAY_RESULT).getValue1()), stringBuilder);
            stringBuilder.append("\n");

        }

        if (stringBuilder.length() > 0) {
            results.setVisibility(View.VISIBLE);
            params.view.findViewById(params.isBaseline ? R.id.baseline_no_results_recorded : R.id.no_results_recorded).setVisibility(View.GONE);
            results.setText(stringBuilder);
        } else {
            results.setVisibility(View.GONE);
            params.view.findViewById(params.isBaseline ? R.id.baseline_result_details : R.id.no_results_recorded).setVisibility(View.VISIBLE);

        }
    }

    private TbrSpannableStringBuilder getResultPrefixBuilder(InitializeRenderParams params, TbrSpannableStringBuilder stringBuilder, Date dateTestGiven) {
        if (params.isIntreatment && !params.isBaseline) {
            Date dateTreatmentInitiatied = org.smartregister.util.Utils.toDate(params.extra.get(TbrConstants.KEY.TREATMENT_INITIATION_DATE), true);
            stringBuilder.append(Utils.formatDate(dateTestGiven, "dd MMM") + " (M" + Utils.getMonthCountFromDate(dateTreatmentInitiatied, dateTestGiven) + "): ");
        }
        return stringBuilder;
    }

    private TbrSpannableStringBuilder getSmearResultStringBuilder(Map<String, String> testResults, TbrSpannableStringBuilder stringBuilder) {
        ForegroundColorSpan redForegroundColorSpan = getRedForegroundColorSpan();
        if (stringBuilder.length() > 0)
            stringBuilder.append("Smear ");
        switch (testResults.get(TbrConstants.RESULT.TEST_RESULT)) {
            case "one_plus":
                stringBuilder.append("1+", redForegroundColorSpan);
                break;
            case "two_plus":
                stringBuilder.append("2+", redForegroundColorSpan);
                break;
            case "three_plus":
                stringBuilder.append("3+", redForegroundColorSpan);
                break;
            case "scanty":
                stringBuilder.append("Scanty", redForegroundColorSpan);
                break;
            case "negative":
                stringBuilder.append("Negative", redForegroundColorSpan);
                break;
            default:
                stringBuilder.append(WordUtils.capitalize(testResults.get(TbrConstants.RESULT.TEST_RESULT).substring(0, 2)), redForegroundColorSpan);
                break;
        }
        return stringBuilder;
    }


    private TbrSpannableStringBuilder getCultureResultStringBuilder(Map<String, String> testResults, TbrSpannableStringBuilder stringBuilder) {
        ForegroundColorSpan blackForegroundColorSpan = getBlackForegroundColorSpan();
        if (stringBuilder.length() > 0)
            stringBuilder.append("Culture ");
        stringBuilder.append(WordUtils.capitalizeFully(testResults.get(TbrConstants.RESULT.CULTURE_RESULT).substring(0, 3)), blackForegroundColorSpan);
        return stringBuilder;
    }


    private TbrSpannableStringBuilder getXRayResultStringBuilder(Map<String, String> testResults, TbrSpannableStringBuilder stringBuilder) {
        ForegroundColorSpan blackForegroundColorSpan = getBlackForegroundColorSpan();
        if (stringBuilder.length() > 0)
            stringBuilder.append("Chest X-Ray ");
        if (testResults.get(TbrConstants.RESULT.XRAY_RESULT).equals("indicative"))
            stringBuilder.append("Indicative", blackForegroundColorSpan);
        else
            stringBuilder.append("Not Indicative", blackForegroundColorSpan);
        return stringBuilder;
    }

    private ForegroundColorSpan getRedForegroundColorSpan() {
        ForegroundColorSpan redForegroundColorSpan = new ForegroundColorSpan(
                context.getResources().getColor(android.R.color.holo_red_dark));
        return redForegroundColorSpan;
    }

    private ForegroundColorSpan getBlackForegroundColorSpan() {
        ForegroundColorSpan blackForegroundColorSpan = new ForegroundColorSpan(
                context.getResources().getColor(android.R.color.black));
        return blackForegroundColorSpan;
    }

    private TbrSpannableStringBuilder getXpertResultStringBuilder(Map<String, String> testResults, TbrSpannableStringBuilder stringBuilder, boolean withOtherResults) {

        ForegroundColorSpan blackForegroundColorSpan = getBlackForegroundColorSpan();
        ForegroundColorSpan redForegroundColorSpan = getRedForegroundColorSpan();
        ForegroundColorSpan colorSpan = withOtherResults ? redForegroundColorSpan : blackForegroundColorSpan;
        stringBuilder.append(withOtherResults ? "Xpe " : "MTB ");
        stringBuilder.append(processXpertResult(testResults.get(TbrConstants.RESULT.MTB_RESULT)), redForegroundColorSpan);
        stringBuilder.append(withOtherResults ? "/ " : " / RIF ");
        stringBuilder.append(processXpertResult(testResults.get(TbrConstants.RESULT.RIF_RESULT)), colorSpan);
        return stringBuilder;
    }

    private String processXpertResult(String result) {

        if (result == null)
            return "-ve";
        switch (result) {
            case DETECTED:
                return "+ve";
            case NOT_DETECTED:
                return "-ve";
            case INDETERMINATE:
                return "?";
            default:
                return result;
        }
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
