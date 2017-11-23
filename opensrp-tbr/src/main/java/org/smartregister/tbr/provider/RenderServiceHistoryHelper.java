package org.smartregister.tbr.provider;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.smartregister.tbr.R;
import org.smartregister.tbr.adapter.ServiceHistoryAdapter;
import org.smartregister.tbr.model.ServiceHistory;
import org.smartregister.tbr.repository.ResultDetailsRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 23/11/2017.
 */

public class RenderServiceHistoryHelper extends BaseRenderHelper {

    public RenderServiceHistoryHelper(Context context, ResultDetailsRepository detailsRepository) {
        super(context, detailsRepository);
    }


    @Override
    public void renderView(String baseEntityId, View view) {
        //Overridden
    }

    @Override
    public void renderView(String baseEntityId, View view, Map<String, String> extra) {

        ListView listView = (ListView) view.findViewById(R.id.serviceHistoryListView);

        final List<ServiceHistory> serviceHistoryArrayList = new ArrayList<>();

        serviceHistoryArrayList.add(new ServiceHistory("07 Jun 2017", "GeneXpert Result"));
        serviceHistoryArrayList.add(new ServiceHistory("03 Jun 2017", "Smear Result"));
        serviceHistoryArrayList.add(new ServiceHistory("10 May 2017", "GeneXpert Result"));
        serviceHistoryArrayList.add(new ServiceHistory("12 Apr 2017", "Culture Result"));
        serviceHistoryArrayList.add(new ServiceHistory("24 Jan 2017", "X-Ray Result"));
        serviceHistoryArrayList.add(new ServiceHistory("16 Jan 2017", "Registration"));

        ServiceHistoryAdapter adapter = new ServiceHistoryAdapter(serviceHistoryArrayList, context);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ServiceHistory serviceHistory = serviceHistoryArrayList.get(position);

                Snackbar.make(view, serviceHistory.getFormName() + " Filled on : " + "\n" + serviceHistory.getDate(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });
    }
}
