package org.smartregister.tbr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.smartregister.tbr.R;
import org.smartregister.tbr.model.ServiceHistory;
import org.smartregister.tbr.util.Utils;

import java.util.List;

/**
 * Created by ndegwamartin on 20/11/2017.
 */

public class ServiceHistoryAdapter extends ArrayAdapter<ServiceHistory> implements View.OnClickListener {

    private List<ServiceHistory> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView date;
        TextView formName;
    }

    public ServiceHistoryAdapter(List<ServiceHistory> data, Context context) {
        super(context, R.layout.row_service_history, data);
        this.dataSet = data;
        this.mContext = context;

    }

    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object object = getItem(position);
        ServiceHistory serviceHistory = (ServiceHistory) object;
        Utils.showToast(getContext(), "Clicked form " + serviceHistory.getFormName() + " filled on " + serviceHistory.getDate());


    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ServiceHistory serviceHistory = getItem(position);
        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_service_history, parent, false);
            viewHolder.date = (TextView) convertView.findViewById(R.id.formfillDateTextView);
            viewHolder.formName = (TextView) convertView.findViewById(R.id.formNameTextView);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.slide_from_bottom : R.anim.slide_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.date.setText(serviceHistory.getDate());
        viewHolder.formName.setText(serviceHistory.getFormName());
        viewHolder.formName.setOnClickListener(this);
        viewHolder.formName.setTag(position);
        return convertView;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }
}