package com.sid.jiolauncher.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.sid.jiolauncher.R;
import com.sid.sdk.DeviceApplicationUtility;
import com.sid.sdk.INotifyAppList;
import com.sid.sdk.PackageReceiver;
import com.sid.sdk.models.ApplicationModel;
import com.sid.ui.Adapters.RvApplicationListAdapter;

import java.util.ArrayList;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;


public class MainActivity extends AppCompatActivity {

    RecyclerView rvApplication;
    LinearLayoutManager layoutManager;
    DeviceApplicationUtility deviceApplicationUtility;
    EditText etSearch;
    ArrayList<ApplicationModel> lstApplication;
    RvApplicationListAdapter rvApplicationListAdapter;
    TextView tvNoDataFound;
    PackageReceiver packageReceiver;
    AppBarLayout appBarLayout;
    Toolbar toolbar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    void init() {
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);

        rvApplication = findViewById(R.id.rvApplication);
        etSearch = findViewById(R.id.etSearch);
        tvNoDataFound = findViewById(R.id.tvNoDataFound);
        layoutManager = new GridLayoutManager(this, 4);
        rvApplication.setLayoutManager(layoutManager);
        deviceApplicationUtility = DeviceApplicationUtility.getInstance();
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("TAG", "Search Text  = " + s);
                try {
                    if (lstApplication != null && !lstApplication.isEmpty()) {
                        ArrayList<ApplicationModel> temp = new ArrayList<>();
                        if (s.toString().isEmpty()) {
                            temp = lstApplication;
                        } else {
                            for (ApplicationModel data : lstApplication) {
                                if (data.getAppName().toLowerCase().contains(s.toString().toLowerCase())) {
                                    temp.add(data);
                                }
                            }
                        }

                        tvNoDataFound.setVisibility(temp.isEmpty() ? View.VISIBLE : View.GONE);
                        rvApplication.setVisibility(temp.isEmpty() ? View.GONE : View.VISIBLE);
                        rvApplicationListAdapter.filteredList(temp);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        try {
            //Package Install Uninstall package Receiver Start
            packageReceiver = new PackageReceiver(new INotifyAppList() {
                @Override
                public void appListUpdated() {
                    etSearch.setText("");
                    new GetAllInstalledApps().execute();
                }
            });
            IntentFilter packageReceiverfilter = new IntentFilter();
            packageReceiverfilter.addAction("android.intent.action.PACKAGE_ADDED");
            packageReceiverfilter.addAction("android.intent.action.PACKAGE_REMOVED");
            packageReceiverfilter.addDataScheme("package");
            registerReceiver(packageReceiver, packageReceiverfilter);
            //Package Install Uninstall package Receiver End
        } catch (Exception e) {
            e.printStackTrace();
        }


//        if ((!deviceApplicationUtility.isMyLauncherDefault(this) || !deviceApplicationUtility.isMyLauncherDefault2(this))) {
//            Intent startMain = new Intent(Intent.ACTION_MAIN);
//            startMain.addCategory(Intent.CATEGORY_HOME);
//            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(startMain);
//        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final MaterialTapTargetPrompt.Builder tapTargetPromptBuilder = new MaterialTapTargetPrompt.Builder(MainActivity.this)
                        .setPrimaryText("Exit Kiosk/Launcher")
                        .setSecondaryText("If you exit, Jio Launcher will be disabled and this app will no longer visible in settings home app\n" +
                                "However you can launch the jio launcher app to re-enable it in settings.")
                        .setAnimationInterpolator(new FastOutSlowInInterpolator())
                        .setMaxTextWidth(R.dimen.tap_target_menu_max_width).setCaptureTouchEventOnFocal(true)
                        .setCaptureTouchEventOutsidePrompt(false)
                        .setBackgroundColour(getResources().getColor(R.color.black))
                        .setFocalColour(getResources().getColor(R.color.focul_color1))
                        .setCaptureTouchEventOutsidePrompt(false)
                        .setTarget(R.id.menu_exit);
                tapTargetPromptBuilder.show();
                tapTargetPromptBuilder.setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                    @Override
                    public void onPromptStateChanged(@NonNull MaterialTapTargetPrompt prompt, int state) {
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED ||
                                state == MaterialTapTargetPrompt.STATE_DISMISSED) {
                            // User has pressed the prompt target
                            new GetAllInstalledApps().execute();
                        }
                    }
                });
            }
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (packageReceiver != null) {
                unregisterReceiver(packageReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class GetAllInstalledApps extends AsyncTask<Void, Void, ArrayList<ApplicationModel>> {

        @Override
        protected void onPreExecute() {
            deviceApplicationUtility.showProgressDialog(MainActivity.this, "", "Loading Application List\nPlease wait.");
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<ApplicationModel> applicationModels) {
            super.onPostExecute(applicationModels);
            lstApplication = applicationModels;
            rvApplicationListAdapter = new RvApplicationListAdapter(MainActivity.this, applicationModels);
            rvApplication.setAdapter(rvApplicationListAdapter);
            deviceApplicationUtility.hideProgress(this);
        }

        @Override
        protected ArrayList<ApplicationModel> doInBackground(Void... voids) {
            return deviceApplicationUtility.getInstalledApps(MainActivity.this.getPackageManager());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_launcher, menu);
        return true;
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_exit:
                finish();
                getPackageManager().setComponentEnabledSetting(new ComponentName(getPackageName(),
                        MainActivity.class.getName()), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}