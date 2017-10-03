package com.hch.game2048;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class Game2048Item extends View {

	private int number;
	private String numberVal;
	private Paint paint;
	private Rect bound;

	public Game2048Item(Context context) {
		this(context, null);
	}

	public Game2048Item(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		paint = new Paint();
	}

	public Game2048Item(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
		numberVal = String.valueOf(number);
		paint.setTextSize(67.0f);
		Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
		paint.setTypeface(font);
		bound = new Rect();
		paint.getTextBounds(numberVal, 0, numberVal.length(), bound);
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		String bgColor = "";
		String textColor = "";
		switch (number) {
		case 0:
			bgColor = "#cdc1b4";
			break;
		case 2:
			bgColor = "#eee4da";
			break;
		case 4:
			bgColor = "#ede0c8";
			break;
		case 8:
			bgColor = "#f2b179";
			break;
		case 16:
			bgColor = "#f59563";
			break;
		case 32:
			bgColor = "#f67c5f";
			break;
		case 64:
			bgColor = "#f65e3b";
			break;
		case 128:
			bgColor = "#edcf72";
			break;
		case 256:
			bgColor = "#edcc61";
			break;
		case 512:
			bgColor = "#edc850";
			break;
		case 1024:
			bgColor = "#edc53f";
			break;
		case 2048:
			bgColor = "#edc111";
			break;
		case 4096:
			bgColor = "#2edc11";
			break;
		default:
			bgColor = "#edc111";
			break;
		}
		// Log.d("tag", number + "");
		if (number <= 4) {
			textColor = "#776e65";
		} else {
			textColor = "#f9f6f2";
		}
		paint.setColor(Color.parseColor(bgColor));
		paint.setStyle(Style.FILL);
		canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

		if (number > 0) {
			paint.setColor(Color.parseColor(textColor));
			float x = (getWidth() - bound.width()) / 2;
			float y = getHeight() / 2 + bound.height() / 2;
			canvas.drawText(numberVal, x, y, paint);
		}

	}

}
