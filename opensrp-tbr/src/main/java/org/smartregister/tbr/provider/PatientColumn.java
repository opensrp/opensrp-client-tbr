package org.smartregister.tbr.provider;

import android.view.View;
import android.widget.TextView;

import org.smartregister.tbr.R;

import static org.smartregister.util.Utils.fillValue;

/**
 * Created by samuelgithengi on 11/20/17.
 */

public class PatientColumn {

    private String patientName;

    private String ageAndGender;

    private String participantId;

    private View view;

    public PatientColumn(View view) {
        this.view = view;
    }


    public View getView() {
        fillValue((TextView) view.findViewById(R.id.patient_name), patientName);
        fillValue((TextView) view.findViewById(R.id.participant_id), participantId);
        fillValue((TextView) view.findViewById(R.id.age_gender), ageAndGender);
        return view;
    }


}
