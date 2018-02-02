package org.smartregister.tbr.helper.view;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.smartregister.tbr.R;
import org.smartregister.tbr.repository.ResultDetailsRepository;
import org.smartregister.tbr.util.Constants;
import org.smartregister.tbr.util.Utils;

import java.util.Map;

/**
 * Created by ndegwamartin on 23/11/2017.
 */

public class RenderPatientFollowupCardHelper extends BaseRenderHelper {

    public RenderPatientFollowupCardHelper(Context context, ResultDetailsRepository detailsRepository) {
        super(context, detailsRepository);
    }

    @Override
    public void renderView(final View view, final Map<String, String> patientDetails) {
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                Button followUpView = (Button) view.findViewById(R.id.follow_up_button);
                if (followUpView != null && patientDetails.get(Constants.KEY.NEXT_VISIT_DATE) != null) {
                    ((View) followUpView.getParent().getParent()).setVisibility(View.VISIBLE);
                    followUpView.setText(context.getString(R.string.followup) + " - due " + Utils.formatDate(org.smartregister.util.Utils.toDate(patientDetails.get(Constants.KEY.NEXT_VISIT_DATE).toString(), true), "dd/MM"));
                    DateTime treatmentStartDate = DateTime.parse(patientDetails.get(Constants.KEY.NEXT_VISIT_DATE).toString());
                    int due = Days.daysBetween(new DateTime(), treatmentStartDate).getDays();
                    if (due < 0)
                        followUpView.setBackgroundResource(R.drawable.due_vaccine_red_bg);
                    else if (due == 0) {
                        followUpView.setBackgroundResource(R.drawable.due_vaccine_blue_bg);
                    } else {
                        followUpView.setTextColor(context.getResources().getColor(R.color.client_list_grey));
                        followUpView.setBackgroundResource(R.drawable.due_vaccine_na_bg);
                    }


                } else {
                    ((View) followUpView.getParent().getParent()).setVisibility(View.GONE);
                }
            }

        });
    }
}
