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
using UnityEngine;
using System.Collections;

public class Sample : MonoBehaviour {
	
	AudioPlugin mPlugin = new AudioPlugin();
	
	public string inputSeekText = "0";
	public int duration = 0;

	void OnGUI() {
		
		int width = Screen.width;
		int height = Screen.height;
		
		if (GUI.Button(new Rect(0, height * 0.5f,width * 0.25f, height * 0.25f), "Audio Pick"))
		{
			mPlugin.Init();
		}
		if (GUI.Button(new Rect(width * 0.25f, height * 0.5f,width * 0.25f, height * 0.25f), "Audip Prepare"))
		{
			mPlugin.Prepare();
		}
		if (GUI.Button(new Rect(width * 0.5f, height * 0.5f,width * 0.25f, height * 0.25f), "Audio Start"))
		{
			mPlugin.Start();
		}
		if (GUI.Button(new Rect(width * 0.75f, height * 0.5f,width * 0.25f, height * 0.25f), "Audio Pause"))
		{
			mPlugin.Pause();
		}
		if (GUI.Button(new Rect(width * 0, height * 0.75f,width * 0.25f, height * 0.25f), "Audio Seek"))
		{
			mPlugin.SeekTo(int.Parse(inputSeekText));
		}
		
		if (GUI.Button(new Rect(width * 0.25f, height * 0.75f,width * 0.25f, height * 0.25f), "Audio Duration"))
		{
		
			duration = mPlugin.GetDuration();
		}
		if (GUI.Button(new Rect(width * 0.5f, height * 0.75f,width * 0.25f, height * 0.25f), "Audio Stop"))
		{
			mPlugin.Stop();
		}

		GUIStyle style = new GUIStyle();
		style.fontSize = 30;
		GUI.Label(new Rect(0,0,width, height * 0.25f), duration.ToString(), style);

		inputSeekText = GUI.TextArea(new Rect(0, height * 0.25f, width, height * 0.25f), inputSeekText);
	}
}
