package org.smartregister.tbr.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
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
    public RegisterArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull Register[] objects) {
        super(context, resource, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

       /* if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_entry, null);
            holder = new ViewHolder();
            holder.nameTextView = (TextView) convertView.findViewById(R.id.person_name);
            holder.surnameTextView = (TextView) convertView.findViewById(R.id.person_surname);
            holder.personImageView = (ImageView) convertView.findViewById(R.id.person_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }*/

        Register person = getItem(position);

        holder.titleTextView.setText(person.getTitle());
        holder.patientCountTextView.setText(person.getTotalPatients());
        holder.registerIconView.setImageDrawable(getRegisterIcon(person.getTitleToken()));
        //holder.personImageView.setImageBitmap(person.getImage());

        return convertView;
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
        private ImageView registerIconView;
    }


}
