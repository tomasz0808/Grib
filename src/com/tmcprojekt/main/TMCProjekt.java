/*
 * MapApp : Simple offline map application, made by Hisham Ghosheh for tutorial purposes only
 * Tutorial on my blog
 * http://ghoshehsoft.wordpress.com/2012/03/09/building-a-map-app-for-android/
 * 
 * Class tutorial:
 * http://ghoshehsoft.wordpress.com/2012/04/06/mapapp5-mapview-and-activity/
 */

package com.tmcprojekt.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.tmcprojekt.map.MapView;
import com.tmcprojekt.map.MapViewLocationListener;
import com.tmcprojekt.tiles.TilesProvider;
import com.tmcprojekt.tmcprojekt.R;

public class TMCProjekt extends Activity {

	public static String dbPath;
	
	public static boolean mapLoaded=true;
	public static boolean gribLoaded=true;
	public static boolean showTemperature=false;
	public static boolean showPressure=false;
	public static boolean showWind=false;
	
	
	MapView mapView;
	TilesProvider tilesProvider;
	MapViewLocationListener locationListener;
	Location savedGpsLocation;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		setContentView(R.layout.startlayout);
		mapLoaded = false;
		gribLoaded=false;
		setTitle("Menu g³ówne");
		
		
		
//		dbPath = "storage/emulated/0//mapapp/aaa.sqlitedb";
//		mapLoaded = true;
		
		super.onCreate(savedInstanceState);
	}	
	
	public void openMenu(View view) {
		openOptionsMenu();
	}
	public void openFileChooser(View view) {
		Intent myIntent = new Intent(getApplicationContext(), FileChooser.class);
		startActivity(myIntent);
	}
	
	@Override
	protected void onResume() {
		if (mapLoaded) {
			initViews(dbPath);

			// Location Listener
			locationListener = new MapViewLocationListener(mapView);
			LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

			// Set our MapView as the main view for the activity
			if (dbPath != null){
				setContentView(mapView);
			}
			mapView.refresh();
			mapView.followMarker();
		}
		super.onResume();
	}

	public void showMap() {
		setContentView(mapView);
	}
	
	public void openMenu(){
		openOptionsMenu();
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		menu.add("Open Map");
		menu.add("Open Grib");
		if(gribLoaded){
			menu.add("Wind");
			menu.add("Temperature");
			menu.add("Pressure");
		}
		menu.add("Center");
		menu.add("Fake");
		
		return super.onPrepareOptionsMenu(menu);
	}
	

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getTitle().toString().contains("Open Map")) {
			openFileChooser(null);
		} else if (item.getTitle().toString().equalsIgnoreCase("Open Grib")) {
			openFileChooser(null);
		} else if (item.getTitle().toString().equalsIgnoreCase("Temperature")) {
			showTemperature=!showTemperature;
		} else if (item.getTitle().toString().equalsIgnoreCase("Wind")) {
			showWind=!showWind;
		} else if (item.getTitle().toString().equalsIgnoreCase("Pressure")) {
			showPressure=!showPressure;
		} else if (item.getTitle().toString().equalsIgnoreCase("Center")) {
			mapView.followMarker();
		} else if (item.getTitle().toString().equalsIgnoreCase("Fake")) {
			mapView.setGpsLocation(18.612363, 54.371676);
			mapView.refresh();
		}
		return super.onMenuItemSelected(featureId, item);
	}

	public void initViews(String dbPath) {
		Bitmap marker = BitmapFactory.decodeResource(getResources(), R.drawable.point);
		String path;
		// Creating our database tilesProvider to pass it to our MapView
//		if (dbPath == null) {
//			path = Environment.getExternalStorageDirectory() + "/mapapp/aaa.sqlitedb";
//		} else {
			path = dbPath;
//		}
		tilesProvider = new TilesProvider(path);

		// Creating the mapView and make sure it fills the screen
		Display display = getWindowManager().getDefaultDisplay();

		mapView = new MapView(this, display.getWidth(), display.getHeight(), tilesProvider, marker);

		// If a location was saved while pausing the app then use it.
		if (savedGpsLocation != null)
			mapView.setGpsLocation(savedGpsLocation);

		// Update and draw the map view
		mapView.refresh();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Zooming
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_Z) {
			mapView.zoomIn();
			mapView.followMarker();
			return true;
		}
		// Zooming
		else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_X) {
			mapView.zoomOut();
			mapView.followMarker();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
}