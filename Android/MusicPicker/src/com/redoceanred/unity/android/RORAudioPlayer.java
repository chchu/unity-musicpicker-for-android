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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class RORAudioPlayer {

	private static final String TAG = RORAudioPlayer.class.getSimpleName();

	private static final int REQUEST_PICK_AUDIO = 200;
	
	private WeakReference<Activity> mActivity;
	private WeakReference<Fragment> mFragment;
	private WeakReference<FragmentActivity> mFragmentActivity;
	
	public interface OnPickAudioListener {
		public void onPickAudio(boolean success);
	}
	
	private OnPickAudioListener mPickListener;

	MediaPlayer mMediaPlayer = null;
	OnPreparedListener mListener = null;

	private String mAudioFilePath;

	public RORAudioPlayer(Activity activity) {
		mActivity = new WeakReference<Activity>(activity);
	}
	
	public RORAudioPlayer(FragmentActivity activity) {
		mFragmentActivity = new WeakReference<FragmentActivity>(activity);
	}

	public RORAudioPlayer(Fragment fragment) {
		mFragment = new WeakReference<Fragment>(fragment);
	}

	public void setOnPreparedListener(OnPreparedListener listener) {
		mListener = listener;
	}
	
	public void setOnPickAudioListener(OnPickAudioListener l) {
		mPickListener = l;
	}

	public int getDuration() {
		if (mMediaPlayer != null) {
			return mMediaPlayer.getDuration();
		} else {
			return 0;
		}
	}

	public void seekTo(int msec) {
		if (mMediaPlayer != null) {
			mMediaPlayer.seekTo(msec);
		}
	}

	private void createMediaPlayer() {
		if (mMediaPlayer == null) {
			mMediaPlayer = new MediaPlayer();
			if (mListener != null) {
				mMediaPlayer.setOnPreparedListener(mListener);
			}
		} else {
			stop();
			createMediaPlayer();
		}
	}

	public boolean setDataSource(byte[] data) {
		try {
			createMediaPlayer();

			File temp = File.createTempFile("tmp_voice", "m4a", mActivity.get().getApplicationContext().getCacheDir());
			temp.deleteOnExit();
			FileOutputStream fos = new FileOutputStream(temp);
			fos.write(data);
			fos.close();

			FileInputStream fis = new FileInputStream(temp);
			mMediaPlayer.setDataSource(fis.getFD());
			fis.close();
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public boolean setDataSource() {
		return setDataSource(mAudioFilePath);
	}

	public boolean setDataSource(String inPath) {
		try {
			createMediaPlayer();

			FileInputStream fis = new FileInputStream(new File(inPath));
			mMediaPlayer.setDataSource(fis.getFD());
			fis.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void prepare() {
		if (mMediaPlayer != null) {
			try {
				mMediaPlayer.prepare();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void start() {
		if (mMediaPlayer != null) {
			mMediaPlayer.start();
		}
	}

	public void stop() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
		}
	}

	public void pause() {
		if (mMediaPlayer != null) {
			mMediaPlayer.pause();
		}
	}

	public void release() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	public void pickAudioForApp() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.setType("audio/*");
		if (mFragment != null) {
			mFragment.get().startActivityForResult(intent, REQUEST_PICK_AUDIO);
		} else if (mFragmentActivity != null){
			mFragmentActivity.get().startActivityForResult(intent, REQUEST_PICK_AUDIO);
		} else {
			mActivity.get().startActivityForResult(intent, REQUEST_PICK_AUDIO);
		}
	}

	private String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Audio.AudioColumns.DATA };
		Activity activity = null;
		if (mFragment != null) {
			activity = mFragment.get().getActivity();
		} else if (mFragmentActivity != null){
			activity = mFragmentActivity.get();
		} else {
			activity = mActivity.get();
		}
		Cursor cursor = activity.getContentResolver().query(contentUri, proj, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	/**
	 * 各種アプリの起動結果.
	 */
	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case REQUEST_PICK_AUDIO:
			if (resultCode != Activity.RESULT_OK) {
				// キャンセル時は何もしない.
				return false;
			}
			Uri dataUri = data.getData();
			String scheme = dataUri.getScheme();
			mAudioFilePath = "";
			if (scheme.startsWith("content")) {
				mAudioFilePath = getRealPathFromURI(dataUri);
				Log.d(TAG, "content -> " + mAudioFilePath);
			} else if (scheme.startsWith("file")) {
				mAudioFilePath = dataUri.toString().substring("file://".length());
				Log.d(TAG, "file -> " + mAudioFilePath);
			} else {
				Log.e(TAG, "scheme do not know. " + scheme);
				return false;
			}
			setDataSource();
			return true;
		}
		return false;
	}

	public void pickAudioDialog() {
		String[] proj = { MediaStore.Audio.AudioColumns.TITLE };
		Activity activity = null;
		if (mFragment != null) {
			activity = mFragment.get().getActivity();
		} else if (mFragmentActivity != null){
			activity = mFragmentActivity.get();
		} else {
			activity = mActivity.get();
		}
		Cursor cursor = activity.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
		int audioCount = cursor.getCount();
		String[] audioTitleArray = new String[0];
		if (audioCount == 0) {
			// Audioデータが見つからない.
		} else {
			audioTitleArray = new String[audioCount];
			int count = 0;
			while (cursor.moveToNext()) {
				String audioTitle = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
				audioTitleArray[count] = audioTitle;
				count++;
			}
		}

		RORDialogFragment.Builder builder = null;
		if (mFragment != null) {
			builder = new RORDialogFragment.Builder(mFragment.get().getActivity());
		} else if (mFragmentActivity != null){
			builder = new RORDialogFragment.Builder(mFragmentActivity.get());
		} else {
			builder = new RORDialogFragment.Builder(mActivity.get());
		}
		
		builder.setTitle("選択して下さい");
		final String[] finalTitleArray = audioTitleArray;
		builder.setItems(audioTitleArray, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String selectTitle = finalTitleArray[which];

				String[] proj = { MediaStore.Audio.AudioColumns.DATA };
				Activity activity = null;
				if (mFragment != null) {
					activity = mFragment.get().getActivity();
				} else if (mFragmentActivity != null) {
					activity = mFragmentActivity.get();
				} else {
					activity = mActivity.get();
				}
				String[] selection = { selectTitle };
				Cursor cursor = activity.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, MediaStore.Audio.AudioColumns.TITLE + "=?", selection, null);
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA);
				cursor.moveToFirst();
				mAudioFilePath = cursor.getString(column_index);
				setDataSource();

				if (mPickListener != null) {
					mPickListener.onPickAudio(true);
				}
			}
		});
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				if (mPickListener != null) {
					mPickListener.onPickAudio(false);
				}
			}
		});

		if (mFragment != null) {
			RORDialogFragment dialog = RORDialogFragment.getInstanse(builder);
			dialog.show(mFragment.get().getFragmentManager(), "test");
		} else if (mFragmentActivity != null){
			RORDialogFragment dialog = RORDialogFragment.getInstanse(builder);
			dialog.show(mFragmentActivity.get().getSupportFragmentManager(), "test");
		} else {
			// Activityに依存したくないためここで表示.Leakする可能性がある.
			final RORDialogFragment.Builder finalBuilder = builder;
			mActivity.get().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					finalBuilder.create().show();
				}
			});
		}
	}
}
