package com.example.sorter2;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;

import adapters.RecyclerViewAdapter;
import models.ClusterMarker;
import models.Gym;
import models.GymManager;
import models.UserManager;
import utils.MyClusterManagerRenderer;
import utils.ViewWeightAnimationWrapper;

import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static models.Constants.ERROR_DIALOG_REQUEST;
import static models.Constants.MAPVIEW_BUNDLE_KEY;
import static models.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static models.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnGymListener, SortDialogListener, OnMapReadyCallback, View.OnClickListener, GoogleMap.OnInfoWindowClickListener {

    private static final String TAG = "MainActivity";
    // vars

    private static final int MAP_LAYOUT_STATE_CONTRACTED = 0;
    private static final int MAP_LAYOUT_STATE_EXPANDED = 1;
    private int mMapLayoutState = 0;

    private GymManager gm;
    private UserManager um;
    private MapView mMapView;
    private FloatingActionButton fab;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap mGoogleMap;

    private Gym focus = null;

    private ClusterManager mClusterManager;
    private MyClusterManagerRenderer mClusterManagerRenderer;

    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();

    private RecyclerView mGymListRecyclerView;
    private RelativeLayout mMapContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // init recycler
        Log.d(TAG, "onCreate: building GM");
        gm = GymManager.getInstance();
        um = UserManager.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mGymListRecyclerView = findViewById(R.id.recyclerView);
        mMapContainer = findViewById(R.id.map_container);

        findViewById(R.id.btn_full_screen_map).setOnClickListener(this);
        // init sort button
        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SortDialogFragment d = new SortDialogFragment();
                d.show(getSupportFragmentManager(), TAG);
            }
        });
        getLocationPermission();
        if (checkMapServices()) {
            initRecyclerView();
            initGoogleMap(savedInstanceState);
            getLastKnownLocation();
        }
    }

    private void moveCamera(LatLng location, double dist){
        double bottom = location.latitude - dist;
        double left = location.longitude - dist;
        double top = location.latitude + dist;
        double right = location.longitude + dist;
        LatLngBounds bound = new LatLngBounds(new LatLng(bottom, left), new LatLng(top, right));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bound, 0));
    }

    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: called");
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLastKnownLocation: failed");
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    Log.d(TAG, "onSuccess: Got location");
                    LatLng curLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    moveCamera(curLocation, 0.001);
                    um.getCurrentUser().setLocation(curLocation);
                }
            }
        });
    }

    private void initGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = findViewById(R.id.gym_map);
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }

    private boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void addMapMarkers(){
        if (mGoogleMap == null){
            Log.d(TAG, "addMapMarkers: Map is null");
            return;
        }
        if(mClusterManager == null){
            mClusterManager = new ClusterManager<ClusterMarker>(getApplicationContext(), mGoogleMap);
        }
        if(mClusterManagerRenderer == null){
            mClusterManagerRenderer = new MyClusterManagerRenderer(
                    this,
                    mGoogleMap,
                    mClusterManager
            );
            mClusterManager.setRenderer(mClusterManagerRenderer);
        }

        for (int i = 0; i < gm.size(); i++) {
            Gym cur = gm.get(i);
            Log.d(TAG, "addMapMarkers: Adding: " + cur.getName());
            ClusterMarker marker = new ClusterMarker(cur);
            mClusterManager.addItem(marker);
            mClusterMarkers.add(marker);
        }
        mClusterManager.cluster();
    }

    private void goToGym(Gym gym){
        Intent intent = new Intent(this, GymViewActivity.class);
        intent.putExtra("icon_path", gym.getImage());
        intent.putExtra("title_path", gym.getName());
        intent.putExtra("score_path", gym.getScore());
        intent.putExtra("type_path", gym.getType().ordinal());
        intent.putExtra("hourly_path", gym.getHourlyCapacity());
        startActivity(intent);
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (mLocationPermissionGranted) {
                    // populate recycler view
                    return;
                } else {
                    getLocationPermission();
                }
            }
        }

    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: creating recycler view");
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(gm, this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onGymClick(int idx) {
        focus = gm.get(idx);
        if (focus.isFocus()){
            focus.setFocus(false);
            goToGym(focus);
        } else {
            LatLng location = gm.get(idx).getLocation();
            moveCamera(location, 0.0005);
            focus.setFocus(true);
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // getLastKnownLocation();
        SortDialogFragment df = (SortDialogFragment) dialog;
        gm.sort(GymManager.SortFunction.values()[df.spinnerFunc.getSelectedItemPosition()]);
        initRecyclerView();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Toast.makeText(this, "CANCEL", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_full_screen_map:{

                if(mMapLayoutState == MAP_LAYOUT_STATE_CONTRACTED){
                    mMapLayoutState = MAP_LAYOUT_STATE_EXPANDED;
                    expandMapAnimation();
                }
                else if(mMapLayoutState == MAP_LAYOUT_STATE_EXPANDED){
                    mMapLayoutState = MAP_LAYOUT_STATE_CONTRACTED;
                    contractMapAnimation();
                }
                break;
            }

        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        goToGym(gm.getGymFromName(marker.getTitle()));
    }

    public static class SortDialogFragment extends DialogFragment {

        private String[] funcs = {"Name", "Capacity", "Current capacity", "Score", "Distance"};
        public Spinner spinnerFunc;

        SortDialogListener listener;

        public interface NoticeDialogListener {
            public void onDialogPositiveClick(DialogFragment dialog);

            public void onDialogNegativeClick(DialogFragment dialog);
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            listener = (SortDialogListener) context;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View view = inflater.inflate(R.layout.layout_sort, null);

            spinnerFunc = view.findViewById(R.id.func_spinner);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, funcs);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerFunc.setAdapter(dataAdapter);

            builder.setView(view)
                    .setTitle("Choose sorting function")
                    .setPositiveButton("Sort", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            listener.onDialogPositiveClick(SortDialogFragment.this);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            listener.onDialogNegativeClick(SortDialogFragment.this);
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onMapReady: Map failed");
            return;
        }
        // save map
        mGoogleMap = map;
        // show location
        map.setMyLocationEnabled(true);
        map.setOnInfoWindowClickListener(this);
        addMapMarkers();
        Log.d(TAG, "onMapReady: Map created");
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void expandMapAnimation(){
        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mMapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                50,
                100);
        mapAnimation.setDuration(800);

        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(mGymListRecyclerView);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                50,
                0);
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();
    }

    private void contractMapAnimation() {
        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mMapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                100,
                50);
        mapAnimation.setDuration(800);

        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(mGymListRecyclerView);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                0,
                50);
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();
    }
}

interface SortDialogListener{
    public void onDialogPositiveClick(DialogFragment dialog);
    public void onDialogNegativeClick(DialogFragment dialog);
}
