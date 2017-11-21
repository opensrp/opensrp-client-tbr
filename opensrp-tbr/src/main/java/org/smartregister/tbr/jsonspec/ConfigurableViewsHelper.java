package org.smartregister.tbr.jsonspec;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.jsonspec.model.View;
import org.smartregister.tbr.jsonspec.model.ViewConfiguration;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by samuelgithengi on 11/21/17.
 */

public class ConfigurableViewsHelper {

    private final Map<String, ViewConfiguration> viewConfigurations = new ConcurrentHashMap<>();

    public void registerViewConfiguration(String viewIdentifier) {
        String jsonString = TbrApplication.getInstance().getConfigurableViewsRepository().getConfigurableViewJson(viewIdentifier);
        if (jsonString == null)
            return;
        else
            viewConfigurations.put(viewIdentifier, TbrApplication.getJsonSpecHelper().getConfigurableView(jsonString));
    }

    public Set<View> getRegisterActiveColumns(String identifier) {
        Set<View> visibleColumns = new TreeSet<>(new ViewPositionComparator());
        for (org.smartregister.tbr.jsonspec.model.View view : viewConfigurations.get(identifier).getViews()) {
            if (view.isVisible())
                visibleColumns.add(view);
        }
        return visibleColumns;
    }

    public void processRegisterColumns(Map<String, Integer> columnMapping, android.view.View view, Set<View> visibleColumns, int parentComponent) {
        //Dont process  for more than 3 columns
        if (columnMapping.size() > 3) return;
        List<android.view.View> columns = new LinkedList<>();
        for (org.smartregister.tbr.jsonspec.model.View columnView : visibleColumns) {
            android.view.View column = view.findViewById(columnMapping.get(columnView.getIdentifier()));
            if (columnView.getResidence().getLayoutWeight() != null) {
                LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) column.getLayoutParams();
                param.weight = Float.valueOf(columnView.getResidence().getLayoutWeight());
                column.setLayoutParams(param);
            }
            columns.add(column);
        }
        ViewGroup allColumns = (ViewGroup) view.findViewById(parentComponent);
        allColumns.removeAllViews();
        for (android.view.View column : columns)
            allColumns.addView(column);

    }

    public void unregisterViewConfiguration(String viewIdentifier) {
        viewConfigurations.remove(viewIdentifier);
    }

    public ViewConfiguration getViewConfiguration(String identifier) {
        return viewConfigurations.get(identifier);
    }
}
