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

/**
 * Interface to notify the input method when the user clicks a candidate or
 * makes a direction-gesture on candidate view.
 */
/**
 * 倿词视图监听器接叿
 * 
 * @ClassName CandidateViewListener
 * @author keanbin
 */
public interface CandidateViewListener {

	/**
	 * 选择了忙?词的处理函敿
	 * 
	 * @param choiceId
	 */
	public void onClickChoice(int choiceId);

	/**
	 * 向左滑动的手势处理函敿	 */
	public void onToLeftGesture();

	/**
	 * 向右滑动的手势处理函敿	 */
	public void onToRightGesture();

	/**
	 * 向上滑动的手势处理函敿	 */
	public void onToTopGesture();

	/**
	 * 向下滑动的手势处理函敿	 */
	public void onToBottomGesture();
}
