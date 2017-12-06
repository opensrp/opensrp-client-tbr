package org.smartregister.tbr.fragment;

import android.view.View;
import android.widget.TextView;

import org.smartregister.tbr.R;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.helper.view.RenderPatientDemographicCardHelper;
import org.smartregister.tbr.helper.view.RenderPositiveResultsCardHelper;
import org.smartregister.tbr.helper.view.RenderServiceHistoryCardHelper;
import org.smartregister.tbr.util.Utils;
import org.smartregister.view.fragment.SecuredFragment;

import java.util.Map;

/**
 * Created by ndegwamartin on 06/12/2017.
 */

public class BasePatientDetailsFragment extends SecuredFragment {

    protected void renderPositiveResultsView(View view, Map<String, String> patientDetails) {
        RenderPositiveResultsCardHelper renderPositiveResultsHelper = new RenderPositiveResultsCardHelper(getActivity(), TbrApplication.getInstance().getResultDetailsRepository());
        renderPositiveResultsHelper.renderView(view.findViewById(R.id.clientPositiveResultsCardView), patientDetails);
    }

    protected void renderDemographicsView(View view, Map<String, String> patientDetails) {

        RenderPatientDemographicCardHelper renderPatientDemographicCardHelper = new RenderPatientDemographicCardHelper(getActivity(), TbrApplication.getInstance().getResultDetailsRepository());
        renderPatientDemographicCardHelper.renderView(view, patientDetails);

    }

    protected void renderServiceHistoryView(View view, Map<String, String> patientDetails) {
        RenderServiceHistoryCardHelper renderServiceHistoryHelper = new RenderServiceHistoryCardHelper(getActivity(), TbrApplication.getInstance().getResultDetailsRepository());
        renderServiceHistoryHelper.renderView(view.findViewById(R.id.clientServiceHistoryCardView), patientDetails);
    }

    protected void processLanguageTokens(Map<String, String> viewLabelsMap, Map<String, String> languageTranslations, View parentView) {
        //Process token translations
        if (!viewLabelsMap.isEmpty()) {
            for (Map.Entry<String, String> entry : viewLabelsMap.entrySet()) {
                String uniqueIdentifier = entry.getKey();
                TextView textView = (TextView) parentView.findViewById(Utils.getLayoutIdentifierResourceId(getActivity(), uniqueIdentifier));
                if (textView != null && !languageTranslations.isEmpty() && languageTranslations.containsKey(entry.getKey())) {
                    textView.setText(languageTranslations.get(entry.getKey()));
                }
            }
        }
    }


    @Override
    protected void onCreation() {

    }

    @Override
    protected void onResumption() {

    }
}
