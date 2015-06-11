package com.tmcprojekt.tiles;

import java.util.Hashtable;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;


public class TilesProvider {
	protected SQLiteDatabase tilesDB;
	int minZoom;
	int minXpos;
	int minYpos;

	protected Hashtable<String, Tile> tiles = new Hashtable<String, Tile>();

	public TilesProvider(String dbPath) {
		tilesDB = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READONLY);
		fetchZoom();
	}

	public void fetchZoom() {
		String query = "SELECT x,y,z FROM tiles";
		Cursor cursor = tilesDB.rawQuery(query, null);
		cursor.moveToFirst();
		minXpos = cursor.getInt(0);
		minYpos = cursor.getInt(1);
		minZoom = 17 - (cursor.getInt(2));
		TilesManager.zoom = minZoom;
	}

	public void fetchTiles(Rect rect, int zoom) {
		String query = "SELECT x,y,image FROM tiles WHERE x >= " + rect.left + " AND x <= " + rect.right + " AND y >= " + rect.top + " AND y <=" + rect.bottom + " AND z == "
				+ (17 - zoom);
		Cursor cursor;
		cursor = tilesDB.rawQuery(query, null);

		Hashtable<String, Tile> temp = new Hashtable<String, Tile>();

		if (cursor.moveToFirst()) {
			do {
				
				int x = cursor.getInt(0);
				int y = cursor.getInt(1);
				Tile tile = tiles.get(x + ":" + y);

				if (tile == null) {
					byte[] img = cursor.getBlob(2);
					Bitmap tileBitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
					tile = new Tile(x, y, tileBitmap);
				}

				temp.put(x + ":" + y, tile);
			} while (cursor.moveToNext()); 

			tiles.clear();
			tiles = temp;
		}
	}

	public Hashtable<String, Tile> getTiles() {
		return tiles;
	}

	public void close() {
		tilesDB.close();
	}

	public void clear() {
		tiles.clear();
	}
}