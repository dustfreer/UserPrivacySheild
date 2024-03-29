/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.security.ass.pinyinime;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.View;

import com.security.ass.pinyinime.SoftKeyboard.KeyRow;

/**
 * Class used to show a soft keyboard.
 * 
 * A soft keyboard view should not handle touch event itself, because we do bias
 * correction, need a global strategy to map an event into a proper view to
 * achieve better user experience. 软键盘视图
 */
public class SoftKeyboardView extends View {
	/**
	 * The definition of the soft keyboard for the current this soft keyboard
	 * view. 软件盘布屿
	 */
	private SoftKeyboard mSoftKeyboard;

	/**
	 * The popup balloon hint for key press/release.
	 */
	private BalloonHint mBalloonPopup;

	/**
	 * The on-key balloon hint for key press/release. If it is null, on-key
	 * highlight will be drawn on th soft keyboard view directly.
	 */
	private BalloonHint mBalloonOnKey;

	/** Used to play key sounds. 声音管理 */
	private SoundManager mSoundManager;

	/** The last key pressed. 朿后按下的按键 */
	private SoftKey mSoftKeyDown;

	/** Used to indicate whether the user is holding on a key. 是否正在按住按键＿ */
	private boolean mKeyPressed = false;

	/**
	 * The location offset of the view to the keyboard container.
	 * 视图到键盘集装箱之间的位置偏秿
	 */
	private int mOffsetToSkbContainer[] = new int[2];

	/**
	 * The location of the desired hint view to the keyboard container.
	 * 承霿提示视图到键盘集装箱之间的位置偏秿
	 */
	private int mHintLocationToSkbContainer[] = new int[2];

	/**
	 * Text size for normal key. 正常按键的文本大尿
	 */
	private int mNormalKeyTextSize;

	/**
	 * Text size for function key. 功能按键的文本大尿
	 */
	private int mFunctionKeyTextSize;

	/**
	 * Long press timer used to response long-press. 长按的定时器
	 */
	private SkbContainer.LongPressTimer mLongPressTimer;

	/**
	 * Repeated events for long press 长按是否为重复？
	 */
	private boolean mRepeatForLongPress = false;

	/**
	 * If this parameter is true, the balloon will never be dismissed even if
	 * user moves a lot from the pressed point. 这个参数如果为true，当用户移开点击点后，气泡不会被锿毁?
	 */
	private boolean mMovingNeverHidePopupBalloon = false;

	/** Vibration for key press. 震动操作对象 */
	private Vibrator mVibrator;

	/** Vibration pattern for key press. 震动的参敿 */
	protected long[] mVibratePattern = new long[] { 1, 20 };

	/**
	 * The dirty rectangle used to mark the area to re-draw during key press and
	 * release. Currently, whenever we can invalidate(Rect), view will call
	 * onDraw() and we MUST draw the whole view. This dirty information is for
	 * future use. 该区域用于标记按键按下和释放后需要重绘的区域，目前没有作用，是为了以后保留的?
	 */
	private Rect mDirtyRect = new Rect();

	private Paint mPaint;
	private FontMetricsInt mFmi;
	private boolean mDimSkb;
	Context mContext;

	public SoftKeyboardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mSoundManager = SoundManager.getInstance(mContext);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mFmi = mPaint.getFontMetricsInt();
	}

	public boolean setSoftKeyboard(SoftKeyboard softSkb) {
		if (null == softSkb) {
			return false;
		}
		mSoftKeyboard = softSkb;
		Drawable bg = softSkb.getSkbBackground();
		if (null != bg)
			setBackgroundDrawable(bg);
		return true;
	}

	public SoftKeyboard getSoftKeyboard() {
		return mSoftKeyboard;
	}

	/**
	 * 设置mSoftKeyboard的尺寿
	 * 
	 * @param skbWidth
	 * @param skbHeight
	 */
	public void resizeKeyboard(int skbWidth, int skbHeight) {
		mSoftKeyboard.setSkbCoreSize(skbWidth, skbHeight);
	}

	/**
	 * 设置mBalloonOnKey气泡、mBalloonPopup气泡?
	 * 
	 * @param balloonOnKey
	 * @param balloonPopup
	 * @param movingNeverHidePopup
	 */
	public void setBalloonHint(BalloonHint balloonOnKey,
			BalloonHint balloonPopup, boolean movingNeverHidePopup) {
		mBalloonOnKey = balloonOnKey;
		mBalloonPopup = balloonPopup;
		mMovingNeverHidePopupBalloon = movingNeverHidePopup;
	}

	/**
	 * 设置视图到键盘集装箱之间的位置偏秿
	 * 
	 * @param offsetToSkbContainer
	 */
	public void setOffsetToSkbContainer(int offsetToSkbContainer[]) {
		mOffsetToSkbContainer[0] = offsetToSkbContainer[0];
		mOffsetToSkbContainer[1] = offsetToSkbContainer[1];
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int measuredWidth = 0;
		int measuredHeight = 0;
		if (null != mSoftKeyboard) {
			measuredWidth = mSoftKeyboard.getSkbCoreWidth();
			measuredHeight = mSoftKeyboard.getSkbCoreHeight();
			measuredWidth += getPaddingLeft() + getPaddingRight();
			measuredHeight += getPaddingTop() + getPaddingBottom();
		}

		// TODO 如果 measuredWidth 大于父视图可给予的最大宽度，会出现什么样的情况？
		// 设置view的尺寿
		setMeasuredDimension(measuredWidth, measuredHeight);
	}

	/**
	 * 显示气泡。?辑箿介：如果该气泡需要强行销毁，那么就马上销毁?如果该气泡正在显示，就更新它，否则显示该气泡?
	 * 
	 * @param balloon
	 * @param balloonLocationToSkb
	 * @param movePress
	 */
	private void showBalloon(BalloonHint balloon, int balloonLocationToSkb[],
			boolean movePress) {
		long delay = BalloonHint.TIME_DELAY_SHOW;
		if (movePress)
			delay = 0;
		if (balloon.needForceDismiss()) {
			balloon.delayedDismiss(0);
		}
		if (!balloon.isShowing()) {
			balloon.delayedShow(delay, balloonLocationToSkb);
		} else {
			balloon.delayedUpdate(delay, balloonLocationToSkb,
					balloon.getWidth(), balloon.getHeight());
		}
		long b = System.currentTimeMillis();
	}

	/**
	 * 重置mKeyPressed为false，并关闭mBalloonOnKey气泡?
	 * 
	 * @param balloonDelay
	 *            关闭气泡的延时时闿
	 */
	public void resetKeyPress(long balloonDelay) {
		if (!mKeyPressed)
			return;
		mKeyPressed = false;
		if (null != mBalloonOnKey) {
			mBalloonOnKey.delayedDismiss(balloonDelay);
		} else {
			if (null != mSoftKeyDown) {
				if (mDirtyRect.isEmpty()) {
					mDirtyRect.set(mSoftKeyDown.mLeft, mSoftKeyDown.mTop,
							mSoftKeyDown.mRight, mSoftKeyDown.mBottom);
				}
				invalidate(mDirtyRect);
			} else {
				invalidate();
			}
		}
		mBalloonPopup.delayedDismiss(balloonDelay);
	}

	// If movePress is true, means that this function is called because user
	// moves his finger to this button. If movePress is false, means that this
	// function is called when user just presses this key.
	/**
	 * 按键按下处理函数
	 * 
	 * @param x
	 * @param y
	 * @param longPressTimer
	 * @param movePress
	 *            如果为true，意味着是因为手指触摸移动到该按键?如果为false，意味着是因为手指按下给按键?
	 * @return
	 */
	public SoftKey onKeyPress(int x, int y,
			SkbContainer.LongPressTimer longPressTimer, boolean movePress) {
		mKeyPressed = false;
		boolean moveWithinPreviousKey = false;
		if (movePress) {
			SoftKey newKey = mSoftKeyboard.mapToKey(x, y);
			if (newKey == mSoftKeyDown)
				moveWithinPreviousKey = true;
			mSoftKeyDown = newKey;
		} else {
			mSoftKeyDown = mSoftKeyboard.mapToKey(x, y);
		}
		if (moveWithinPreviousKey || null == mSoftKeyDown)
			return mSoftKeyDown;

		// TODO
		// 这句代码放在这里，那如果moveWithinPreviousKey是true的情况下，mKeyPressed不是还是false吗？
		mKeyPressed = true;

		// 播放按键声音和震势
		if (!movePress) {
			tryPlayKeyDown();
			tryVibrate();
		}

		mLongPressTimer = longPressTimer;

		// 判断是否是按下操作，如果是，就判读条件，启动长按定时噿
		if (!movePress) {
			if (mSoftKeyDown.getPopupResId() > 0 || mSoftKeyDown.repeatable()) {
				mLongPressTimer.startTimer();
			}
		} else {
			mLongPressTimer.removeTimer();
		}

		int desired_width;
		int desired_height;
		float textSize;
		Environment env = Environment.getInstance();

		if (null != mBalloonOnKey) {
			// 设置气泡背景
			Drawable keyHlBg = mSoftKeyDown.getKeyHlBg();
			mBalloonOnKey.setBalloonBackground(keyHlBg);

			// Prepare the on-key balloon
			// 设置气泡内容和尺寿
			int keyXMargin = mSoftKeyboard.getKeyXMargin();
			int keyYMargin = mSoftKeyboard.getKeyYMargin();
			desired_width = mSoftKeyDown.width() - 2 * keyXMargin;
			desired_height = mSoftKeyDown.height() - 2 * keyYMargin;
			textSize = env
					.getKeyTextSize(SoftKeyType.KEYTYPE_ID_NORMAL_KEY != mSoftKeyDown.mKeyType.mKeyTypeId);
			Drawable icon = mSoftKeyDown.getKeyIcon();
			if (null != icon) {
				mBalloonOnKey.setBalloonConfig(icon, desired_width,
						desired_height);
			} else {
				mBalloonOnKey.setBalloonConfig(mSoftKeyDown.getKeyLabel(),
						textSize, true, mSoftKeyDown.getColorHl(),
						desired_width, desired_height);
			}

			// 设置气泡显示的位罿
			mHintLocationToSkbContainer[0] = getPaddingLeft()
					+ mSoftKeyDown.mLeft
					- (mBalloonOnKey.getWidth() - mSoftKeyDown.width()) / 2;
			mHintLocationToSkbContainer[0] += mOffsetToSkbContainer[0];
			mHintLocationToSkbContainer[1] = getPaddingTop()
					+ (mSoftKeyDown.mBottom - keyYMargin)
					- mBalloonOnKey.getHeight();
			mHintLocationToSkbContainer[1] += mOffsetToSkbContainer[1];

			// 显示气泡
			showBalloon(mBalloonOnKey, mHintLocationToSkbContainer, movePress);
		} else {
			// 设置界面屿部刷新，只刷新按键区埿
			mDirtyRect.union(mSoftKeyDown.mLeft, mSoftKeyDown.mTop,
					mSoftKeyDown.mRight, mSoftKeyDown.mBottom);
			invalidate(mDirtyRect);
		}

		// TODO 下面这个气泡和上面的气泡有什么区别？
		// Prepare the popup balloon
		if (mSoftKeyDown.needBalloon()) {
			Drawable balloonBg = mSoftKeyboard.getBalloonBackground();
			mBalloonPopup.setBalloonBackground(balloonBg);

			desired_width = mSoftKeyDown.width() + env.getKeyBalloonWidthPlus();
			desired_height = mSoftKeyDown.height()
					+ env.getKeyBalloonHeightPlus();
			textSize = env
					.getBalloonTextSize(SoftKeyType.KEYTYPE_ID_NORMAL_KEY != mSoftKeyDown.mKeyType.mKeyTypeId);
			Drawable iconPopup = mSoftKeyDown.getKeyIconPopup();
			if (null != iconPopup) {
				mBalloonPopup.setBalloonConfig(iconPopup, desired_width,
						desired_height);
			} else {
				mBalloonPopup.setBalloonConfig(mSoftKeyDown.getKeyLabel(),
						textSize, mSoftKeyDown.needBalloon(),
						mSoftKeyDown.getColorBalloon(), desired_width,
						desired_height);
			}

			// The position to show.
			mHintLocationToSkbContainer[0] = getPaddingLeft()
					+ mSoftKeyDown.mLeft
					+ -(mBalloonPopup.getWidth() - mSoftKeyDown.width()) / 2;
			mHintLocationToSkbContainer[0] += mOffsetToSkbContainer[0];
			mHintLocationToSkbContainer[1] = getPaddingTop()
					+ mSoftKeyDown.mTop - mBalloonPopup.getHeight();
			mHintLocationToSkbContainer[1] += mOffsetToSkbContainer[1];
			showBalloon(mBalloonPopup, mHintLocationToSkbContainer, movePress);
		} else {
			mBalloonPopup.delayedDismiss(0);
		}

		// TODO 怎么这里还有丿个长按定时器启动＿ 上面不是已经设置了吗＿
		if (mRepeatForLongPress)
			longPressTimer.startTimer();
		return mSoftKeyDown;
	}

	/**
	 * 按键释放的处理函敿
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public SoftKey onKeyRelease(int x, int y) {
		mKeyPressed = false;
		if (null == mSoftKeyDown)
			return null;

		mLongPressTimer.removeTimer();

		if (null != mBalloonOnKey) {
			mBalloonOnKey.delayedDismiss(BalloonHint.TIME_DELAY_DISMISS);
		} else {
			mDirtyRect.union(mSoftKeyDown.mLeft, mSoftKeyDown.mTop,
					mSoftKeyDown.mRight, mSoftKeyDown.mBottom);
			invalidate(mDirtyRect);
		}

		if (mSoftKeyDown.needBalloon()) {
			mBalloonPopup.delayedDismiss(BalloonHint.TIME_DELAY_DISMISS);
		}

		// TODO 为什么在按下的处理函数中判断坐标点属于哪个按键的时忙不霿要减去padding，迌在这里霿要呢＿
		if (mSoftKeyDown.moveWithinKey(x - getPaddingLeft(), y
				- getPaddingTop())) {
			return mSoftKeyDown;
		}
		return null;
	}

	/**
	 * 按键的移动处理函敿
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public SoftKey onKeyMove(int x, int y) {
		if (null == mSoftKeyDown)
			return null;

		if (mSoftKeyDown.moveWithinKey(x - getPaddingLeft(), y
				- getPaddingTop())) {
			return mSoftKeyDown;
		}

		// The current key needs to be updated.
		mDirtyRect.union(mSoftKeyDown.mLeft, mSoftKeyDown.mTop,
				mSoftKeyDown.mRight, mSoftKeyDown.mBottom);

		if (mRepeatForLongPress) {
			// 如果mMovingNeverHidePopupBalloon为true，那么就不销毁气泡，否则，先锿毁在进入按键处理函数?
			if (mMovingNeverHidePopupBalloon) {
				return onKeyPress(x, y, mLongPressTimer, true);
			}

			if (null != mBalloonOnKey) {
				mBalloonOnKey.delayedDismiss(0);
			} else {
				invalidate(mDirtyRect);
			}

			if (mSoftKeyDown.needBalloon()) {
				mBalloonPopup.delayedDismiss(0);
			}

			if (null != mLongPressTimer) {
				mLongPressTimer.removeTimer();
			}
			return onKeyPress(x, y, mLongPressTimer, true);
		} else {
			// When user moves between keys, repeated response is disabled.
			return onKeyPress(x, y, mLongPressTimer, true);
		}
	}

	/**
	 * 震动
	 */
	private void tryVibrate() {
		if (!Settings.getVibrate()) {
			return;
		}
		if (mVibrator == null) {
			mVibrator = (Vibrator) mContext
					.getSystemService(Context.VIBRATOR_SERVICE);

			// = new Vibrator();
		}
		mVibrator.vibrate(mVibratePattern, -1);
	}

	/**
	 * 播放按键按下的声響
	 */
	private void tryPlayKeyDown() {
		if (Settings.getKeySound()) {
			mSoundManager.playKeyDown();
		}
	}

	/**
	 * 是否锿毁SoftKeyboard视图
	 * 
	 * @param dimSkb
	 *            该标志会在onDraw（）中使用?
	 */
	public void dimSoftKeyboard(boolean dimSkb) {
		mDimSkb = dimSkb;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (null == mSoftKeyboard)
			return;

		// 画布水平移动padding，使得画布的原点＿0＿0）相对于父视图移动了(getPaddingLeft(),
		// getPaddingTop())?
		canvas.translate(getPaddingLeft(), getPaddingTop());

		Environment env = Environment.getInstance();
		mNormalKeyTextSize = env.getKeyTextSize(false);
		mFunctionKeyTextSize = env.getKeyTextSize(true);
		// Draw the last soft keyboard
		int rowNum = mSoftKeyboard.getRowNum();
		int keyXMargin = mSoftKeyboard.getKeyXMargin();
		int keyYMargin = mSoftKeyboard.getKeyYMargin();
		for (int row = 0; row < rowNum; row++) {
			KeyRow keyRow = mSoftKeyboard.getKeyRowForDisplay(row);
			if (null == keyRow)
				continue;
			List<SoftKey> softKeys = keyRow.mSoftKeys;
			int keyNum = softKeys.size();
			for (int i = 0; i < keyNum; i++) {
				SoftKey softKey = softKeys.get(i);
				if (SoftKeyType.KEYTYPE_ID_NORMAL_KEY == softKey.mKeyType.mKeyTypeId) {
					mPaint.setTextSize(mNormalKeyTextSize);
				} else {
					mPaint.setTextSize(mFunctionKeyTextSize);
				}
				drawSoftKey(canvas, softKey, keyXMargin, keyYMargin);
			}
		}

		// 清空画布
		if (mDimSkb) {
			mPaint.setColor(0xa0000000);
			canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
		}

		mDirtyRect.setEmpty();
	}

	/**
	 * 在画布上画一个按锿
	 * 
	 * @param canvas
	 * @param softKey
	 * @param keyXMargin
	 * @param keyYMargin
	 */
	private void drawSoftKey(Canvas canvas, SoftKey softKey, int keyXMargin,
			int keyYMargin) {
		Drawable bg;
		int textColor;
		if (mKeyPressed && softKey == mSoftKeyDown) {
			bg = softKey.getKeyHlBg();
			textColor = softKey.getColorHl();
		} else {
			bg = softKey.getKeyBg();
			textColor = softKey.getColor();
		}

		if (null != bg) {
			bg.setBounds(softKey.mLeft + keyXMargin, softKey.mTop + keyYMargin,
					softKey.mRight - keyXMargin, softKey.mBottom - keyYMargin);
			bg.draw(canvas);
		}

		String keyLabel = softKey.getKeyLabel();
		Drawable keyIcon = softKey.getKeyIcon();
		if (null != keyIcon) {
			Drawable icon = keyIcon;
			int marginLeft = (softKey.width() - icon.getIntrinsicWidth()) / 2;
			int marginRight = softKey.width() - icon.getIntrinsicWidth()
					- marginLeft;
			int marginTop = (softKey.height() - icon.getIntrinsicHeight()) / 2;
			int marginBottom = softKey.height() - icon.getIntrinsicHeight()
					- marginTop;
			icon.setBounds(softKey.mLeft + marginLeft,
					softKey.mTop + marginTop, softKey.mRight - marginRight,
					softKey.mBottom - marginBottom);
			icon.draw(canvas);
		} else if (null != keyLabel) {
			mPaint.setColor(textColor);
			float x = softKey.mLeft
					+ (softKey.width() - mPaint.measureText(keyLabel)) / 2.0f;
			int fontHeight = mFmi.bottom - mFmi.top;
			float marginY = (softKey.height() - fontHeight) / 2.0f;
			float y = softKey.mTop + marginY - mFmi.top + mFmi.bottom / 1.5f;
			canvas.drawText(keyLabel, x, y + 1, mPaint);
		}
	}
}
