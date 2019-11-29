package com.security.ass.privacy;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.pm.PackageInfo;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Process;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import com.security.ass.R;


@SuppressLint("Registered")
public class ActivityBase extends ActionBarActivity {
	private int mThemeId;
	private Bitmap[] mCheck = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (PrivacyService.checkClient()) {
			mThemeId = R.style.CustomTheme_Light;//�������⣬��������Ϊ��ɫ
			setTheme(mThemeId);
		}

		super.onCreate(savedInstanceState);

		// Check if Privacy client available
		if (!PrivacyService.checkClient()) {
			setContentView(R.layout.xposed);
		}
	}

	protected Bitmap getOffCheckBox() {
		if (mCheck == null)
			buildCheckBoxes();
		return mCheck[0];
	}

	protected Bitmap getHalfCheckBox() {
		if (mCheck == null)
			buildCheckBoxes();
		return mCheck[1];
	}

	protected Bitmap getFullCheckBox() {
		if (mCheck == null)
			buildCheckBoxes();
		return mCheck[2];
	}

	protected Bitmap getOnDemandCheckBox() {
		if (mCheck == null)
			buildCheckBoxes();
		return mCheck[3];
	}

	protected Bitmap getCheckBoxImage(RState state, boolean expert) {
		if (state.partialRestricted)
			if (expert)
				return getHalfCheckBox();
			else
				return getFullCheckBox();
		else if (state.restricted)
			return getFullCheckBox();
		else
			return getOffCheckBox();
	}

	protected Bitmap getAskBoxImage(RState state, boolean expert) {
		if (state.partialAsk)
			if (expert)
				return getHalfCheckBox();
			else
				return getOnDemandCheckBox();
		else if (state.asked)
			return getOffCheckBox();
		else
			return getOnDemandCheckBox();
	}

	private void buildCheckBoxes() {
		mCheck = new Bitmap[4];
		int colorAccent = getResources().getColor(R.color.color_accent_light);

		// Get off check box
		TypedArray ta2 = getTheme().obtainStyledAttributes(new int[] { android.R.attr.listChoiceIndicatorMultiple });
		Drawable off = ta2.getDrawable(0);
		ta2.recycle();
		off.setBounds(0, 0, off.getIntrinsicWidth(), off.getIntrinsicHeight());

		// Get check mark
		Drawable checkmark = getResources().getDrawable(R.drawable.checkmark);
		checkmark.setBounds(0, 0, off.getIntrinsicWidth(), off.getIntrinsicHeight());
		checkmark.setColorFilter(colorAccent, Mode.SRC_ATOP);

		// Get check mark outline
		Drawable checkmarkOutline = getResources().getDrawable(R.drawable.checkmark_outline);
		checkmarkOutline.setBounds(0, 0, off.getIntrinsicWidth(), off.getIntrinsicHeight());

		// Create off check box
		mCheck[0] = Bitmap.createBitmap(off.getIntrinsicWidth(), off.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas0 = new Canvas(mCheck[0]);
		off.draw(canvas0);

		// Create half check box
		mCheck[1] = Bitmap.createBitmap(off.getIntrinsicWidth(), off.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas1 = new Canvas(mCheck[1]);
		off.draw(canvas1);
		Paint paint1 = new Paint();
		paint1.setStyle(Paint.Style.FILL);
		paint1.setColor(colorAccent);
		float wborder = off.getIntrinsicWidth() / 3f;
		float hborder = off.getIntrinsicHeight() / 3f;
		canvas1.drawRect(wborder, hborder, off.getIntrinsicWidth() - wborder, off.getIntrinsicHeight() - hborder,
				paint1);

		// Create full check box
		mCheck[2] = Bitmap.createBitmap(off.getIntrinsicWidth(), off.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas2 = new Canvas(mCheck[2]);
		off.draw(canvas2);
		checkmark.draw(canvas2);
		checkmarkOutline.draw(canvas2);

		// Get question mark
		Drawable questionmark = getResources().getDrawable(R.drawable.ondemand);
		questionmark.setBounds(0, 0, off.getIntrinsicWidth(), off.getIntrinsicHeight());
		questionmark.setColorFilter(colorAccent, Mode.SRC_ATOP);

		// Get question mark outline
		Drawable questionmarkOutline = getResources().getDrawable(R.drawable.questionmark_outline);
		questionmarkOutline.setBounds(0, 0, off.getIntrinsicWidth(), off.getIntrinsicHeight());

		// Create question check box
		mCheck[3] = Bitmap.createBitmap(off.getIntrinsicWidth(), off.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas3 = new Canvas(mCheck[3]);
		off.draw(canvas3);
		questionmark.draw(canvas3);
		questionmarkOutline.draw(canvas3);
	}

	public int getThemed(int attr) {
		TypedValue tv = new TypedValue();
		getTheme().resolveAttribute(attr, tv, true);
		return tv.resourceId;
	}
}
