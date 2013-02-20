/*
 * Copyright (c) <2013> <nishino.keiichiro@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.redoceanred.unity.android;

import com.redoceanred.unity.android.RORAudioPlayer.OnPickAudioListener;
import com.unity3d.player.UnityPlayer;

class RORAudioPluginCallback {
	private String mGameObject;

	public RORAudioPluginCallback(final String gameObject) {
		mGameObject = gameObject;
	}

	public void call(String message) {
		UnityPlayer.UnitySendMessage(mGameObject, "CallFromJS", message);
	}
}

public class RORAudioPlugin {

	private RORAudioPlayer mPlayer;
	private String mGameObject = "";

	public RORAudioPlugin() {
		mPlayer = new RORAudioPlayer(UnityPlayer.currentActivity);
	}
	
	public void prepare() {
		mPlayer.prepare();
	}
	
	public void start() {
		mPlayer.start();
	}
	
	public void stop() {
		mPlayer.stop();
	}
	
	public void pause() {
		mPlayer.pause();
	}
	
	public void release() {
		mPlayer.setOnPickAudioListener(null);
		mPlayer.release();
	}

	public void seekTo(int msec) {
		mPlayer.seekTo(msec);
	}
	
	public int getDuration() {
		return mPlayer.getDuration();
	}
	
	public void pickAudio(String gameObject) {
		mGameObject = gameObject;
		mPlayer.setOnPickAudioListener(new OnPickAudioListener() {
			
			@Override
			public void onPickAudio(boolean success) {
				if (!mGameObject.equals("")) {
					RORAudioPluginCallback callback = new RORAudioPluginCallback(mGameObject);
					callback.call(Boolean.toString(success));
					mGameObject = "";
				}
			}
		});
		mPlayer.pickAudioDialog();
	}
}
