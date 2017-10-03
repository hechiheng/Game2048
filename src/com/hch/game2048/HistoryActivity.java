package com.hch.game2048;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class HistoryActivity extends Activity {
	private SQLiteDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_history);

		db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString()
				+ "score.db", null);
		Cursor cursor = db.rawQuery("select * from scoreTable order by time desc", null);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		while (cursor.moveToNext()) {
			int score = cursor.getInt(1);
			String time = cursor.getString(2);
			HashMap<String, Object> itemMap = new HashMap<String, Object>();
			itemMap.put("scores", score);
			itemMap.put("times", time);
			list.add(itemMap);
		}

		ListView historyListView = (ListView) findViewById(R.id.historyListView);
		SimpleAdapter adapter = new SimpleAdapter(this, list,
				R.layout.activity_history_item, new String[] { "scores",
						"times" }, new int[] { R.id.history_score_text,
						R.id.history_time_text });
		historyListView.setAdapter(adapter);
	}
}
