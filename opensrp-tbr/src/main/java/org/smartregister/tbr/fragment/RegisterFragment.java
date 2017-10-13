package org.smartregister.tbr.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.smartregister.tbr.R;
import org.smartregister.tbr.adapter.RegisterArrayAdapter;
import org.smartregister.tbr.model.Register;
import org.smartregister.tbr.util.Utils;

/**
 * Created by ndegwamartin on 12/10/2017.
 */

public class RegisterFragment extends ListFragment {
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Register[] values = new Register[]{new Register("Presumptive Patients", "presumptive_patients", 45, 6), new Register("Positive Patients", "positive_patients", 24, 16), new Register("In-treatment Patients", "in_treatment_patients", 15, 3)};
        RegisterArrayAdapter adapter = new RegisterArrayAdapter(getActivity(), R.layout.register_row_view, values);

        setListAdapter(adapter);
    }


    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        TextView registerTitle = (TextView) view.findViewById(R.id.registerTitleView);
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        Utils.showToast(getActivity(), registerTitle.getText().toString() + " Register!");
    }
}
