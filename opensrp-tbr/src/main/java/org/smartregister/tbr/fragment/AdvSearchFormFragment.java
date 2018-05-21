package org.smartregister.tbr.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import org.smartregister.tbr.R;

public class AdvSearchFormFragment extends Fragment{
    View view;
    public Button searchButton;
    public CheckBox chkPresumptive,chkPositive,chkTreatment;
    public EditText participantId, firstName, lastName, phoneNumber;
    public Spinner spGender, spAgeGroup;
    public RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_advanced_search, container, false);
        searchButton = (Button) view.findViewById(R.id.btn_adv_search);
        searchButton.setOnClickListener((View.OnClickListener) getActivity());
        chkPresumptive = (CheckBox) view.findViewById(R.id.chk_presumptive);
        chkPositive = (CheckBox) view.findViewById(R.id.chk_positive);
        chkTreatment = (CheckBox) view.findViewById(R.id.chk_treatment);
        participantId = (EditText) view.findViewById(R.id.et_participant_id);
        firstName = (EditText) view.findViewById(R.id.et_first_name);
        lastName = (EditText) view.findViewById(R.id.et_last_name);
        phoneNumber = (EditText) view.findViewById(R.id.et_phone);
        spAgeGroup = (Spinner) view.findViewById(R.id.et_age_group);
        spGender = (Spinner) view.findViewById(R.id.sp_gender);

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(getActivity(),
        R.array.gender, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(genderAdapter);

        ArrayAdapter<CharSequence> ageGroupAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.age_group, android.R.layout.simple_spinner_item);
        ageGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAgeGroup.setAdapter(ageGroupAdapter);

        return view;
    }
}
