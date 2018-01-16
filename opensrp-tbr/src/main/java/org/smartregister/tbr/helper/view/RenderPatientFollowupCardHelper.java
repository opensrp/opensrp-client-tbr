package org.smartregister.tbr.helper.view;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

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
                if (followUpView != null) {
                    followUpView.setText(context.getString(R.string.followup) + " - due " + Utils.formatDate(org.smartregister.util.Utils.toDate(patientDetails.get(Constants.KEY.NEXT_VISIT_DATE).toString(), true), "dd/MM"));

                }
            }

        });
    }
}
