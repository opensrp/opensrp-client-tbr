package org.smartregister.tbr.jsonspec;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.avocarrot.json2view.DynamicView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.smartregister.tbr.R;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.event.ViewConfigurationSyncCompleteEvent;
import org.smartregister.tbr.jsonspec.model.View;
import org.smartregister.tbr.jsonspec.model.ViewConfiguration;
import org.smartregister.tbr.repository.ConfigurableViewsRepository;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by samuelgithengi on 11/21/17.
 */

public class ConfigurableViewsHelper {

    private static final String TAG = "ConfigurableViewsHelper";

    private final ConfigurableViewsRepository configurableViewsRepository;

    private final JsonSpecHelper jsonSpecHelper;

    private final Context context;

    private boolean isTabletSize;

    private boolean jsonViewsEnabled;

    public ConfigurableViewsHelper(ConfigurableViewsRepository configurableViewsRepository, JsonSpecHelper jsonSpecHelper, Context context) {
        this.configurableViewsRepository = configurableViewsRepository;
        this.jsonSpecHelper = jsonSpecHelper;
        this.context = context;
        isTabletSize = context.getResources().getBoolean(R.bool.isTablet);
        if (TbrApplication.getJsonSpecHelper().getMainConfiguration() != null)
            jsonViewsEnabled = TbrApplication.getJsonSpecHelper().getMainConfiguration().isEnableJsonViews();
    }

    private final Map<String, ViewConfiguration> viewConfigurations = new ConcurrentHashMap<>();

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
        maxColumns = !isTabletSize && maxColumns > 3 ? 3 : maxColumns;
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
                                MATCH_PARENT,
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
        if (fallbackLayout != null) {
            viewParent.removeView(fallbackLayout);
        }
        if (isVisible) {
            json2View = json2View != null ? json2View : fallbackLayout;
            viewParent.addView(json2View);
        }

        return json2View;
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void setJsonViewsEnabled(ViewConfigurationSyncCompleteEvent syncCompleteEvent) {
        if (syncCompleteEvent != null && TbrApplication.getJsonSpecHelper().getMainConfiguration() != null)
            jsonViewsEnabled = TbrApplication.getJsonSpecHelper().getMainConfiguration().isEnableJsonViews();
    }

    public boolean isJsonViewsEnabled() {
        return jsonViewsEnabled;
    }
}
