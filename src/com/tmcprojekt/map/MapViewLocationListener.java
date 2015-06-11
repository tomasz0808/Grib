package com.tmcprojekt.map;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class MapViewLocationListener implements LocationListener {
	MapView mapView;
	boolean stopped = false;

	public MapViewLocationListener(MapView mapView) {
		this.mapView = mapView;
	}

	@Override
	public void onLocationChanged(Location location) {
		if (!stopped && location != null) {
			mapView.setGpsLocation(location.getLongitude(), location.getLatitude());
			mapView.postInvalidate();
		}
	}

	public void stop() {
		stopped = true;
		mapView = null;
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

}