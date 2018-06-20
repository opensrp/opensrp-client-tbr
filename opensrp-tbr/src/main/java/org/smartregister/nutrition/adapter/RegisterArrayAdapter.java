package org.smartregister.nutrition.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.smartregister.configurableviews.model.BaseConfiguration;
import org.smartregister.configurableviews.model.TestResultsConfiguration;
import org.smartregister.nutrition.R;
import org.smartregister.nutrition.model.Register;

import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 11/10/2017.
 */

public class RegisterArrayAdapter extends ArrayAdapter<Register> {

    private Context context;
    private final List<Register> items;
    private BaseConfiguration metaData;

    public RegisterArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Register> records, BaseConfiguration metaData) {
        super(context, resource, records);
        this.context = context;
        this.items = records;
        this.metaData = metaData;
    }

    @Override
    public View getView(int position, View convertView_, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder = null;
        View convertView = convertView_;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_register_view, null);
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
        if(register.getTitle().equalsIgnoreCase(context.getResources().getString(R.string.nutrition)) || register.getTitle().equalsIgnoreCase(context.getResources().getString(R.string.quiz))) {
            holder.patientCountTextView.setText("");
            holder.patientDueCountTextView.setVisibility(View.GONE);
        }
        else {
            holder.patientCountTextView.setText(" (" + String.valueOf(register.getTotalPatients()) + ") ");
            if (register.getTotalPatientsWithDueOverdue() > 0) {
                holder.patientDueCountTextView.setText(String.valueOf(register.getTotalPatientsWithDueOverdue()));
                holder.patientDueCountTextView.setVisibility(View.VISIBLE);
            } else {
                holder.patientDueCountTextView.setVisibility(View.GONE);
            }
        }

        final List list = ((List)((TestResultsConfiguration)metaData).getResultsConfig());
        for(int i=0; i < list.size(); i++){
            final int j = i;
            if( ((String)((Map)list.get(i)).get("item")).equalsIgnoreCase(register.getTitleToken()) ){
                register.setDigest(((String)((Map)list.get(i)).get("digest")));
            }
        }
        holder.registerIconView.setImageDrawable(getRegisterIcon(register.getTitleToken()));
        return convertView;
    }

    @Override
    public Register getItem(int i) {
        return items.get(i);
    }

    private Drawable getRegisterIcon(String registerToken) {
        if (registerToken.equalsIgnoreCase(Register.PRESUMPTIVE_PATIENTS)) {
            return ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.ic_presumptive_patients, getContext().getTheme());
        } else if (registerToken.equalsIgnoreCase(Register.POSITIVE_PATIENTS)) {
            return ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.ic_positive_patients, getContext().getTheme());
        } else if (registerToken.equalsIgnoreCase(Register.IN_TREATMENT_PATIENTS)) {
            return ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.ic_intreatment_patients, getContext().getTheme());
        } else {
            return ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.ic_presumptive_patients, getContext().getTheme());
        }
    }

    public class ViewHolder {
        private TextView titleTextView;
        private TextView patientCountTextView;
        private TextView patientDueCountTextView;
        private ImageView registerIconView;
        public String digest = "";
    }


}
