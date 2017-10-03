package com.hch.game2048;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.hch.game2048.Game2048Layout.OnGame2048Listener;

public class MainActivity extends Activity {

	private TextView labelText, scoreText, bestScoreText;
	private Game2048Layout game2048Layout;
	private Typeface font;
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor sharedPreferences_Editor;
	private int bestScore;
	private SQLiteDatabase db;
	private SoundPool soundPool;
	private int clickSoundID;// 点击音效ID
	private float volumnCurrent;
	private float volumnRatio;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		sharedPreferences = getSharedPreferences("scores", MODE_PRIVATE);
		sharedPreferences_Editor = sharedPreferences.edit();
		bestScore = sharedPreferences.getInt("bestScore", 0);

		createDBTable();

		font = Typeface.createFromAsset(getAssets(), "fonts/ClearSans.ttf");
		labelText = (TextView) findViewById(R.id.labelText);
		scoreText = (TextView) findViewById(R.id.scoreText);
		bestScoreText = (TextView) findViewById(R.id.bestScoreText);
		setFont();
		bestScoreText.setText(String.valueOf(bestScore));
		game2048Layout = (Game2048Layout) findViewById(R.id.game2048Layout);
		game2048Layout.setOnGame2048Listener(new OnGame2048Listener() {
			@Override
			public void onScoreChange(int score) {
				scoreText.setText(String.valueOf(score));
				if (score > bestScore) {
					bestScore = score;
					sharedPreferences_Editor.putInt("bestScore", bestScore);
					sharedPreferences_Editor.commit();
					bestScoreText.setText(String.valueOf(bestScore));
				}
			}

			@Override
			public void onGameOver() {
				insertScore(Integer.valueOf(scoreText.getText().toString()));
				AlertDialog.Builder builder = new Builder(MainActivity.this);
				builder.setMessage("你获得分数：" + scoreText.getText());
				builder.setTitle("游戏结束");
				builder.setPositiveButton("重新开始",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								game2048Layout.restart();
							}
						});
				builder.setNegativeButton("退出",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								System.exit(0);
							}
						});
				builder.create().show();
			}

			@Override
			public void onMoveHappen() {
				playClickSound(clickSoundID);
			}
		});

		Button restartBtn = (Button) findViewById(R.id.restartBtn);
		restartBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				game2048Layout.restart();
			}
		});

		Button historyBtn = (Button) findViewById(R.id.historyBtn);
		historyBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						HistoryActivity.class);
				startActivity(intent);
			}
		});

		AudioManager am = (AudioManager) this
				.getSystemService(Context.AUDIO_SERVICE);
		float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		volumnCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		volumnRatio = volumnCurrent / audioMaxVolumn;
		initClickSound();
	}

	private void setFont() {
		labelText.setTypeface(font);
		scoreText.setTypeface(font);
		bestScoreText.setTypeface(font);
		labelText.getPaint().setFlags(Paint.FAKE_BOLD_TEXT_FLAG);
		scoreText.getPaint().setFlags(Paint.FAKE_BOLD_TEXT_FLAG);
		bestScoreText.getPaint().setFlags(Paint.FAKE_BOLD_TEXT_FLAG);
	}

	private void createDBTable() {
		db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString()
				+ "score.db", null);
		String sql = "create table if not exists scoreTable(_id integer primary key autoincrement, score integer,time text)";
		db.execSQL(sql);
	}

	private void insertScore(int score) {
		Calendar c = Calendar.getInstance();
		Date date = c.getTime();
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String time = format.format(date);
		ContentValues values = new ContentValues();
		values.put("score", score);
		values.put("time", time);
		db.insert("scoreTable", null, values);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialog.Builder builder = new Builder(MainActivity.this);
			builder.setMessage("是否退出游戏？");
			builder.setTitle("提示");
			builder.setPositiveButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.setNegativeButton("退出",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							int score = Integer.valueOf(scoreText.getText()
									.toString());
							if (score > 0) {
								insertScore(Integer.valueOf(scoreText.getText()
										.toString()));
							}
							dialog.dismiss();
							System.exit(0);
						}
					});
			builder.create().show();
		}
		return true;
	}

	/**
	 * 初始化点击音效
	 */
	private void initClickSound() {
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
		clickSoundID = soundPool.load(this, R.raw.button1, 1);
	}

	/**
	 * 播放点击音效
	 */
	private void playClickSound(int soundID) {
		soundPool.play(soundID, volumnRatio, // 左耳道音量【0~1】
				volumnRatio, // 右耳道音量【0~1】
				0, // 播放优先级【0表示最低优先级】
				1, // 循环模式【0表示循环一次，-1表示一直循环，其他表示数字+1表示当前数字对应的循环次数】
				1 // 播放速度【1是正常，范围从0~2】
				);
	}

}
