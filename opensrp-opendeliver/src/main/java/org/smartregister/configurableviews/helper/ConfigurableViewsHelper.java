package org.smartregister.configurableviews.helper;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avocarrot.json2view.DynamicView;

import org.json.JSONObject;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.configurableviews.model.View;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.configurableviews.repository.ConfigurableViewsRepository;
import org.smartregister.nutrition.R;

import java.util.Arrays;
import java.util.Iterator;
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

    private static final String TAG = ConfigurableViewsHelper.class.getCanonicalName();

    private final ConfigurableViewsRepository configurableViewsRepository;

    private final JsonSpecHelper jsonSpecHelper;

    private final Context context;

    private boolean isTabletSize;

    private boolean jsonViewsEnabled;

    private final Map<String, ViewConfiguration> viewConfigurations = new ConcurrentHashMap<>();

    public ConfigurableViewsHelper(ConfigurableViewsRepository configurableViewsRepository, JsonSpecHelper jsonSpecHelper, Context context) {
        this.configurableViewsRepository = configurableViewsRepository;
        this.jsonSpecHelper = jsonSpecHelper;
        this.context = context;
        isTabletSize = context.getResources().getBoolean(R.bool.isTablet);
        if (ConfigurableViewsLibrary.getJsonSpecHelper().getMainConfiguration() != null)
            jsonViewsEnabled = ConfigurableViewsLibrary.getJsonSpecHelper().getMainConfiguration().isEnableJsonViews();
    }

    public void registerViewConfigurations(List<String> viewIdentifiers) {
        for (String viewIdentifier : viewIdentifiers) {
            String jsonString = configurableViewsRepository.getConfigurableViewJson(viewIdentifier);
            if (jsonString == null)
                continue;
            else
                viewConfigurations.put(viewIdentifier, jsonSpecHelper.getConfigurableView(jsonString));
        }
    }

    public Set<View> getRegisterActiveColumns(String identifier) {
        Set<View> visibleColumns = new TreeSet<>(new ViewPositionComparator());
        int maxColumns = viewConfigurations.get(identifier).getViews().size();
        maxColumns = !isTabletSize && maxColumns > 4 ? 4 : maxColumns;
        for (View view : viewConfigurations.get(identifier).getViews()) {
            if (view.isVisible())
                visibleColumns.add(view);
        }
        if (!isTabletSize && visibleColumns.size() > maxColumns) {
            View[] columnsArray = visibleColumns.toArray(new View[]{});
            visibleColumns.clear();
            visibleColumns.addAll(Arrays.asList(Arrays.copyOf(columnsArray, 3)));
        }
        return visibleColumns;
    }

    public void processRegisterColumns(Map<String, Integer> columnMapping, android.view.View view, Set<View> visibleColumns, int parentComponent) {
        List<android.view.View> columns = new LinkedList<>();
        for (View columnView : visibleColumns) {
            try {
                android.view.View column = view.findViewById(columnMapping.get(columnView.getIdentifier()));
                if (columnView.getResidence().getLayoutWeight() != null) {
                    LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) column.getLayoutParams();
                    param.weight = Float.valueOf(columnView.getResidence().getLayoutWeight());
                    column.setLayoutParams(param);
                }
                column.setVisibility(android.view.View.VISIBLE);
                columns.add(column);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                return;
            }
        }
        ViewGroup allColumns = (ViewGroup) view.findViewById(parentComponent);
        allColumns.removeAllViews();
        for (android.view.View column : columns)
            allColumns.addView(column);

    }

    public void unregisterViewConfiguration(List<String> viewIdentifiers) {
        for (String viewIdentifier : viewIdentifiers)
            viewConfigurations.remove(viewIdentifier);
    }

    public ViewConfiguration getViewConfiguration(String identifier) {
        return viewConfigurations.get(identifier);
    }

    public void inflateDynamicHeaderView(Set<View> visibleColumns, ViewConfiguration viewConfiguration, android.view.View parent) {
        try {
            ViewGroup container = (ViewGroup) parent;
            container.removeAllViews();

            JSONObject jsonView = new JSONObject(viewConfiguration.getJsonView());

            Iterator<View> vcit = visibleColumns.iterator();
            while (vcit.hasNext()) {
                View column = vcit.next();

                android.view.View view = DynamicView.createView(context, jsonView);
                TextView textview = (TextView) view.findViewById(R.id.register_header_item);
                textview.setText(column.getLabel());
                textview.setTextColor(Color.BLACK);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                try {
                    lp.weight = Float.parseFloat(column.getResidence().getLayoutWeight());
                }catch (Exception e){e.printStackTrace();}

                view.setLayoutParams(lp);

                container.addView(view);
            }
        } catch (Exception e) {
            Log.e(TAG, "inflateDynamicView: ", e);
        }
    }

    public android.view.View inflateDynamicView(ViewConfiguration viewConfiguration, ViewConfiguration commonConfiguration, android.view.View fallback, int parentViewId, boolean isHeader) {
        try {
            JSONObject jsonView = new JSONObject(viewConfiguration.getJsonView());
            android.view.View view = DynamicView.createView(context, jsonView);
            ViewGroup registerColumns = (ViewGroup) view.findViewById(parentViewId);
            if (commonConfiguration != null && !commonConfiguration.getJsonView().isEmpty()) {
                jsonView = new JSONObject(commonConfiguration.getJsonView());
                android.view.View commonRegisterColumns = DynamicView.createView(context, jsonView);
                ViewGroup commonColumns = (ViewGroup) commonRegisterColumns.findViewById(parentViewId);
                while (registerColumns.getChildCount() > 0) {
                    android.view.View column = registerColumns.getChildAt(0);
                    registerColumns.removeView(column);
                    commonColumns.addView(column);
                }
                registerColumns = commonColumns;
            }
            if (isHeader)
                registerColumns.setLayoutParams(
                        new AbsListView.LayoutParams(
                                AbsListView.LayoutParams.MATCH_PARENT,
                                AbsListView.LayoutParams.WRAP_CONTENT));
            else {
                registerColumns.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                        (int) context.getResources().getDimension(R.dimen.list_item_height)));
            }
            return registerColumns;
        } catch (Exception e) {
            Log.e(TAG, "inflateDynamicView: ", e);
            return fallback;
        }
    }

    public android.view.View getDynamicView(ViewConfiguration viewConfiguration, ViewGroup viewParent) {
        try {
            JSONObject jsonView = new JSONObject(viewConfiguration.getJsonView());
            android.view.View view = DynamicView.createView(context, jsonView, viewParent);
            return view;
        } catch (Exception e) {
            return null;
        }
    }

    public android.view.View inflateDynamicView(ViewConfiguration viewConfiguration, ViewGroup viewParent, android.view.View fallbackLayout, boolean isVisible) {

        android.view.View json2View = getDynamicView(viewConfiguration, viewParent);
        if (isVisible) {
            json2View = json2View != null ? json2View : fallbackLayout;
            viewParent.addView(json2View);
        }

        return json2View;
    }

    public boolean isJsonViewsEnabled() {
        return jsonViewsEnabled;
    }
}
