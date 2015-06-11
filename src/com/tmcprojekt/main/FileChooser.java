package com.tmcprojekt.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tmcprojekt.tmcprojekt.R;

public class FileChooser extends ListActivity {

	private List<String> item = null;
	private List<String> path = null;
	private String root;
	private TextView myPath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_chooser);
		myPath = (TextView) findViewById(R.id.path);

		root = Environment.getExternalStorageDirectory().getPath();
		getDir(root);
	}

	@Override
	public void onBackPressed() {
		TMCProjekt.mapLoaded = false;
		super.onBackPressed();
	}

	private void getDir(String dirPath) {
		myPath.setText("   Location: " + dirPath);
		myPath.setTextColor(Color.RED);
		item = new ArrayList<String>();
		path = new ArrayList<String>();
		File f = new File(dirPath);
		File[] files = f.listFiles();

		if (!dirPath.equals(root)) {
			item.add("   .../");
			path.add(f.getParent());
		}

		for (int i = 0; i < files.length; i++) {
			File file = files[i];

			if (!file.isHidden() && file.canRead()) {
				path.add(file.getPath());
				if (file.isDirectory()) {
					item.add("   " + file.getName() + "/");
				} else {
					item.add("   " + file.getName());
				}
			}
		}

		ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, R.layout.row, item);
		setListAdapter(fileList);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		File file = new File(path.get(position));
		if (file.isDirectory()) {
			if (file.canRead()) {
				getDir(path.get(position));
			} else {
				new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher).setTitle("[" + file.getName() + "] folder can't be read!").setPositiveButton("OK", null).show();
			}
		} else {
			if (file.getName().endsWith(".sqlitedb")) {
				TMCProjekt.dbPath = file.getPath();
				TMCProjekt.mapLoaded = true;
				TMCProjekt.gribLoaded = true;

				finish();
			} 
//			else if (file.getName().endsWith("")) {
//				 try {
//					new UnGrib(file.getPath());
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				// new UnGrib(file.getPath());

//				finish();
//			}
		}
	}

}