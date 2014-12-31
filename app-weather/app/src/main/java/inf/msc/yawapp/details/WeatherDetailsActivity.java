package inf.msc.yawapp.details;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import inf.msc.yawapp.R;
import inf.msc.yawapp.common.BaseModuleActivity;
import inf.msc.yawapp.common.Intents;
import inf.msc.yawapp.model.WeatherData;
import inf.msc.yawapp.search.SearchActivity;

public class WeatherDetailsActivity extends BaseModuleActivity implements WeatherDetailsView {

    public static final String CONDITION_FORMAT = "%s, %s";
    public static final String BUNDLE_VIEW_STATE = "viewState";

    private enum ViewState {
        UNINITIALIZED, INITIALIZED
    }

    private ViewState viewState = ViewState.UNINITIALIZED;

    @Inject
    WeatherDetailsPresenter presenter;

    private Map<WeatherData.Condition, String> conditionTexts;

    private void initConditionTexts() {
        Resources res = getResources();
        conditionTexts = new HashMap<>();
        conditionTexts.put(WeatherData.Condition.CLEAR, res.getString(R.string.weather_condition_clear));
        conditionTexts.put(WeatherData.Condition.FEW_CLOUDS, res.getString(R.string.weather_condition_few_clouds));
        conditionTexts.put(WeatherData.Condition.SCATTERED_CLOUDS, res.getString(R.string.weather_condition_scattered_clouds));
        conditionTexts.put(WeatherData.Condition.BROKEN_CLOUDS, res.getString(R.string.weather_condition_broken_clouds));
        conditionTexts.put(WeatherData.Condition.SHOWER_RAIN, res.getString(R.string.weather_condition_shower_rain));
        conditionTexts.put(WeatherData.Condition.RAIN, res.getString(R.string.weather_condition_rain));
        conditionTexts.put(WeatherData.Condition.THUNDERSTORM, res.getString(R.string.weather_condition_thunderstorm));
        conditionTexts.put(WeatherData.Condition.SNOW, res.getString(R.string.weather_condition_snow));
        conditionTexts.put(WeatherData.Condition.MIST, res.getString(R.string.weather_condition_mist));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            viewState = (ViewState) savedInstanceState.getSerializable(BUNDLE_VIEW_STATE);
        }

        setContentView(R.layout.activity_detail);
        initConditionTexts();

        final Toolbar toolbar = getActionBarToolbar();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                toolbar.setTitle("");
            }
        });

        if (viewState == ViewState.UNINITIALIZED) {
            onNewIntent(getIntent());
            viewState = ViewState.INITIALIZED;
        } else {
            presenter.presentExistingData();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(BUNDLE_VIEW_STATE, viewState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent.getAction().equals(Intents.SEARCH_WEATHER)) {
            presenter.search(intent.getExtras().getString("query"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(this, SearchActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showSearchError(String message) {
    }

    @Override
    public void showCityName(final String city) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView cityName = (TextView) findViewById(R.id.city_name);
                cityName.setText(city);
            }
        });
    }

    @Override
    public void showWeatherCondition(final WeatherData.Condition condition, final boolean isDay) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView cityConditionWidget = (TextView) findViewById(R.id.city_condition);
                String conditionText = conditionTexts.get(condition);
                String daylightText = (isDay) ? getResources().getString(R.string.day) : getResources().getString(R.string.night);
                cityConditionWidget.setText(String.format(CONDITION_FORMAT, conditionText, daylightText));
            }
        });
    }

    @Override
    protected List<Object> getModules() {
        return Arrays.<Object>asList(new WeatherDetailsModule(this));
    }
}
