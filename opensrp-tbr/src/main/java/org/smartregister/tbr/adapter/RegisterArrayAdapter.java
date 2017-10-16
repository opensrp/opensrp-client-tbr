package org.smartregister.tbr.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.smartregister.tbr.R;
import org.smartregister.tbr.model.Register;

/**
 * Created by ndegwamartin on 11/10/2017.
 */

public class RegisterArrayAdapter extends ArrayAdapter<Register> {

    private Context context;
    private final Register[] items;

    public RegisterArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull Register[] records) {
        super(context, resource, records);
        this.context = context;
        this.items = records;
    }

    @Override
    public View getView(int position, View convertView_, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder = null;
        View convertView = convertView_;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.register_row_view, null);
            holder = new ViewHolder();
            holder.titleTextView = (TextView) convertView.findViewById(R.id.registerTitleView);
            holder.patientCountTextView = (TextView) convertView.findViewById(R.id.patientCountView);
            holder.patientDueCountTextView = (TextView) convertView.findViewById(R.id.patientDueOverdueCountView);
            holder.registerIconView = (ImageView) convertView.findViewById(R.id.registerIconView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Register register = getItem(position);

        holder.titleTextView.setText(register.getTitle());
        holder.patientCountTextView.setText(" (" + String.valueOf(register.getTotalPatients()) + ") ");
        holder.patientDueCountTextView.setText(String.valueOf(register.getTotalPatientsWithDueOverdue()));
        holder.registerIconView.setImageDrawable(getRegisterIcon(register.getTitleToken()));

        return convertView;
    }

    @Override
    public Register getItem(int i) {
        return items[i];
    }

    private Drawable getRegisterIcon(String registerToken) {
        if (registerToken.equalsIgnoreCase(Register.PRESUMPTIVE_PATIENTS)) {
            return getContext().getResources().getDrawable(R.drawable.ic_presumptive);

        } else if (registerToken.equalsIgnoreCase(Register.POSITIVE_PATIENTS)) {
            return getContext().getResources().getDrawable(R.drawable.ic_positive);

        } else if (registerToken.equalsIgnoreCase(Register.IN_TREATMENT_PATIENTS)) {
            return getContext().getResources().getDrawable(R.drawable.ic_intreatment);

        } else {
            return getContext().getResources().getDrawable(R.drawable.ic_presumptive);
        }
    }

    static class ViewHolder {
        private TextView titleTextView;
        private TextView patientCountTextView;
        private TextView patientDueCountTextView;
        private ImageView registerIconView;
    }


}
