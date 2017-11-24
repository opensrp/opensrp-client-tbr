package org.smartregister.tbr.helper.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.lang3.text.WordUtils;
import org.smartregister.tbr.R;
import org.smartregister.tbr.repository.ResultDetailsRepository;
import org.smartregister.tbr.util.Constants;
import org.smartregister.tbr.util.Utils;

import java.util.Map;

/**
 * Created by ndegwamartin on 23/11/2017.
 */

public class RenderPatientDemographicCardHelper extends BaseRenderHelper {

    public RenderPatientDemographicCardHelper(Context context, ResultDetailsRepository detailsRepository) {
        super(context, detailsRepository);
    }


    @Override
    public void renderView(String baseEntityId, View view) {
        //Overriden
    }

    @Override
    public void renderView(String baseEntityId, View view, Map<String, String> patientDetails) {

        TextView tbReachIdTextView = (TextView) view.findViewById(R.id.tbReachIdTextView);
        tbReachIdTextView.setText(Utils.formatIdentifier(patientDetails.get(Constants.KEY.TBREACH_ID)));

        TextView clientAgeTextView = (TextView) view.findViewById(R.id.clientAgeTextView);
        String dobString = patientDetails.get(Constants.KEY.DOB);
        String formattedAge = Utils.getFormattedAgeString(dobString);
        clientAgeTextView.setText(formattedAge);

        TextView clientNameTextView = (TextView) view.findViewById(R.id.clientNameTextView);
        String fullName = patientDetails.get(Constants.KEY.FIRST_NAME) + " " + patientDetails.get(Constants.KEY.LAST_NAME);
        clientNameTextView.setText(fullName);
        TextView clientGenderTextView = (TextView) view.findViewById(R.id.clientGenderTextView);
        clientGenderTextView.setText(WordUtils.capitalize(patientDetails.get(Constants.KEY.GENDER)));

        TextView clientInitalsTextView = (TextView) view.findViewById(R.id.clientInitalsTextView);
        clientInitalsTextView.setText(Utils.getShortInitials(fullName));

        if (patientDetails.get(Constants.KEY.GENDER).equals(Constants.GENDER.MALE)) {
            clientInitalsTextView.setBackgroundColor(context.getResources().getColor(R.color.male_light_blue));
            clientInitalsTextView.setTextColor(context.getResources().getColor(R.color.male_blue));

        } else {
            clientInitalsTextView.setBackgroundColor(context.getResources().getColor(R.color.female_light_pink));
            clientInitalsTextView.setTextColor(context.getResources().getColor(R.color.female_pink));
        }
    }
}
