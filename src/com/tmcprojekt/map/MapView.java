package com.tmcprojekt.map;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Location;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import com.tmcprojekt.main.TMCProjekt;
import com.tmcprojekt.main.UnGrib;
import com.tmcprojekt.tiles.PointD;
import com.tmcprojekt.tiles.Tile;
import com.tmcprojekt.tiles.TilesManager;
import com.tmcprojekt.tiles.TilesProvider;
import com.tmcprojekt.tmcprojekt.R;

public class MapView extends View {

	protected Context context;
	protected int viewWidth, viewHeight;
	protected TilesProvider tileProvider;
	protected TilesManager tileManager;
	protected Paint fontPaint;
	protected Paint bitmapPaint;
	protected Paint arrowPaint;
	protected Paint linePaint;

	protected Paint tempTextPaint;
	protected Paint pressureTextPaint;
	protected Paint circlePaint = new Paint();

	protected PointD setLoc = new PointD(54.385186, 18.617071);
	protected PointD seekLocation = new PointD(54.385186, 18.617071);
	protected Location gpsLocation = null;
	protected boolean autoFollow = false;

	protected Bitmap positionMarker;
	protected PointD lastTouchPos = new PointD(-1, -1);
	
	ArrayList<Float> coorX = UnGrib.getXCor();
	ArrayList<Float> coorY = UnGrib.getYCor();
	
	
	Bitmap arrowUp;
	int arrowX;
	int arrowY;

	public MapView(Context context, int viewWidth, int viewHeight, TilesProvider tilesProvider, Bitmap positionMarker) {
		super(context);
		this.context = context;
		
		arrowUp = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow_up);
		arrowX = arrowUp.getHeight();
		arrowY = arrowUp.getWidth();
		this.tileProvider = tilesProvider;

		this.viewWidth = viewWidth;
		this.viewHeight = viewHeight;

		this.positionMarker = positionMarker;
		tileManager = new TilesManager(256, viewWidth, viewHeight);
		initPaintStyles();
		getTiles();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (viewWidth != -1 && viewHeight != -1) {
			// ustawianie odpowiednich wymiarow
			setMeasuredDimension(viewWidth, viewHeight);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	void initPaintStyles() {
		
		tempTextPaint = new Paint();
		tempTextPaint.setColor(Color.RED);
		tempTextPaint.setTextSize(20);
		
		pressureTextPaint = new Paint();
		pressureTextPaint.setColor(Color.BLUE);
		pressureTextPaint.setTextSize(20);
		
		bitmapPaint = new Paint();
		arrowPaint = new Paint();
		linePaint = new Paint();
		linePaint.setStrokeWidth(5);
		linePaint.setColor(Color.RED);
		// Font paint is used to draw text
		fontPaint = new Paint();
		fontPaint.setColor(Color.DKGRAY);
		fontPaint.setShadowLayer(1, 1, 1, Color.BLACK);
		fontPaint.setTextSize(20);

		// Used to draw a semi-transparent circle at the phone's gps location
		circlePaint.setARGB(70, 170, 170, 80);
		circlePaint.setAntiAlias(true);
	}

	void getTiles() {
		tileManager.setLocation(seekLocation.x, seekLocation.y);
		Rect visibleRegion = tileManager.getVisibleRegion();
		tileProvider.fetchTiles(visibleRegion, tileManager.getZoom());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawARGB(255, 100, 100, 100);
		PointD pixRatio = TilesManager.calcRatio(seekLocation.x, seekLocation.y);

		int mapWidth = tileManager.mapSize() * 256;
		Point pix = new Point((int) (pixRatio.x * mapWidth), (int) (pixRatio.y * mapWidth));
		Point offset = new Point((int) (pix.x - viewWidth / 2f), (int) (pix.y - viewHeight / 2f));


		drawTiles(canvas, offset);

		drawMarker(canvas, offset);
		drawArrow(canvas, offset);
//		drawTemp(canvas);
	}

	public Bitmap myMatrix(int newSizeX, int newSizeY, float rotation) {

		Matrix myMatrix = new Matrix();

		float scaledX = ((float) newSizeX) / arrowX;
		float scaledY = ((float) newSizeY) / arrowY;

		myMatrix.postScale(scaledX, scaledY);
		myMatrix.postRotate(rotation);

		Bitmap reshapedArrow = Bitmap.createBitmap(arrowUp, 0, 0, arrowX, arrowY, myMatrix, true);
		return reshapedArrow;
	}

//	void drawTemp(Canvas canvas){
////		int xCount=0;
//		int Ycount=0;
//		
//		ArrayList<Float> temp = UnGrib.getTemp(); 
//		
//		for (Float s : coorX){
//				Ycount=0;
//			for(Float x: coorY){
//				for(Float tem: temp){
//					canvas.drawText(temp.get(Ycount)+" °C", s + 70, x + 15, tempTextPaint);
//				}
//			}	
//		}
//			}
	
	
	void drawArrow(Canvas canvas, Point offset) {

		Collection<Tile> tilesList = tileProvider.getTiles().values();


		for (Tile tile : tilesList) {

			int tileSize = tileManager.getTileSize();
			long tileX = tile.x * tileSize + tileSize / 4;
			long tileY = tile.y * tileSize + tileSize / 4;


			long finalX = tileX - offset.x;
			long finalY = tileY - offset.y;

			Double lon_rounded = new BigDecimal(tile2long(tile.x, getZoom())).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
			Double lat_rounded = new BigDecimal(tile2lat(tile.y, getZoom())).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();

			canvas.drawText(String.valueOf(lon_rounded) + "°", finalX + 1, 20, fontPaint);
			canvas.drawText(String.valueOf(lat_rounded) + "°", 5, finalY + 1, fontPaint);



			if (TMCProjekt.showWind) {
				canvas.drawBitmap(myMatrix(100, 100, 0), finalX, finalY, arrowPaint);
			}
			if (TMCProjekt.showTemperature) {
				canvas.drawText("Temperatura"+" °C", finalX + 70, finalY + 15, tempTextPaint);
			}
			if (TMCProjekt.showPressure) {
				canvas.drawText("Ciœnienie"+" hPa", finalX + 70, finalY + 80, pressureTextPaint);
			}

		}
	}

	double tile2long(int x, int z) {
		return (x / Math.pow(2, z) * 360 - 180);
	}

	double tile2lat(int y, int z) {
		double n = Math.PI - 2 * Math.PI * y / Math.pow(2, z);
		return (180 / Math.PI * Math.atan(0.5 * (Math.exp(n) - Math.exp(-n))));
	}

	void drawTiles(Canvas canvas, Point offset) {

		Collection<Tile> tilesList = tileProvider.getTiles().values();


		for (Tile tile : tilesList) {

			int tileSize = tileManager.getTileSize();
			long tileX = tile.x * tileSize;
			long tileY = tile.y * tileSize;


			long finalX = tileX - offset.x;
			long finalY = tileY - offset.y;

			canvas.drawBitmap(tile.img, finalX, finalY, bitmapPaint);
		}
	}

	void drawMarker(Canvas canvas, Point offset) {

		if (gpsLocation != null) {

			Point markerPos = tileManager.lonLatToPixelXY(gpsLocation.getLongitude(), gpsLocation.getLatitude());

			int markerX = markerPos.x - offset.x;
			int markerY = markerPos.y - offset.y;

			canvas.drawBitmap(positionMarker, markerX - positionMarker.getWidth() / 2, markerY - positionMarker.getHeight() / 2, bitmapPaint);

			float ground = (float) tileManager.calcGroundResolution(gpsLocation.getLatitude());

			float rad = gpsLocation.getAccuracy() / ground;

			canvas.drawCircle(markerX, markerY, rad, circlePaint);

			int pen = 1;
			canvas.drawText("lon:" + gpsLocation.getLongitude(), 0, 20 * pen++, fontPaint);
			canvas.drawText("lat:" + gpsLocation.getLatitude(), 0, 20 * pen++, fontPaint);
			canvas.drawText("Zoom:" + tileManager.getZoom(), 0, 20 * pen++, fontPaint);

//			canvas.drawText("100 * pixel = " + String.valueOf(ground*10) + " metrów", 0, 20 * pen++, fontPaint);
//			canvas.drawLine(10, 100, 110, 100, linePaint);

		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();

		if (action == MotionEvent.ACTION_DOWN) {

			lastTouchPos.x = (int) event.getX();
			lastTouchPos.y = (int) event.getY();

			return true;
		} else if (action == MotionEvent.ACTION_MOVE) {
			autoFollow = false;

			PointD current = new PointD(event.getX(), event.getY());

			PointD diff = new PointD(current.x - lastTouchPos.x, current.y - lastTouchPos.y);


			Point pixels1 = tileManager.lonLatToPixelXY(seekLocation.x, seekLocation.y);


			Point pixels2 = new Point(pixels1.x - (int) diff.x, pixels1.y - (int) diff.y);


			PointD newSeek = tileManager.pixelXYToLonLat((int) pixels2.x, (int) pixels2.y);


			seekLocation = newSeek;

			getTiles();
			invalidate(); 

			lastTouchPos.x = current.x;
			lastTouchPos.y = current.y;

			return true;
		}

		return super.onTouchEvent(event);
	}


	public void refresh() {
		getTiles();
		invalidate();
	}

	public void postRefresh() {
		getTiles();
		postInvalidate();
	}

	public void followMarker() {
		if (gpsLocation != null) {
			seekLocation.x = gpsLocation.getLongitude();
			seekLocation.y = gpsLocation.getLatitude();
			autoFollow = true;

			getTiles();
			invalidate();
		} else {
			seekLocation.x = setLoc.x;
			seekLocation.y = setLoc.y;
			autoFollow = true;

			getTiles();
			invalidate();
		}
	}

	public void zoomIn() {
		tileManager.zoomIn();
		onMapZoomChanged();
	}

	public void zoomOut() {
		tileManager.zoomOut();
		onMapZoomChanged();
	}

	protected void onMapZoomChanged() {
		tileProvider.clear();
		getTiles();
		invalidate();
	}


	public Location getGpsLocation() {
		return gpsLocation;
	}

	public PointD getSeekLocation() {
		return seekLocation;
	}

	public void setSeekLocation(double longitude, double latitude) {
		seekLocation.x = longitude;
		seekLocation.y = latitude;
	}

	public void setGpsLocation(Location location) {
		setGpsLocation(location.getLongitude(), location.getLatitude());
	}

	public void setGpsLocation(double longitude, double latitude) {
		if (gpsLocation == null)
			gpsLocation = new Location("");
		gpsLocation.setLongitude(longitude);
		gpsLocation.setLatitude(latitude);

		if (autoFollow)
			followMarker();

	}

	public int getZoom() {
		return tileManager.getZoom();
	}

	public void setZoom(int zoom) {
		tileManager.setZoom(zoom);
		onMapZoomChanged();
	}
}