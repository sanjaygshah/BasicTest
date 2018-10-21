/*******************************************************************************
 * OliBasic is a fork of BASIC! for Android
 * 
 * BASIC! is an implementation of the Basic programming language for
 * Android devices.
 * 
 * This file is part of OliBasic 
 * 
 * Copyrights (C) 2010 - 2017 of the base code and licensing under the terms of GNU GPLv3 by Paul Laughton.
 * 
 * Copyrights  (C) 2016 - 2018 for all changes and the whole composition by Gregor Tiemeyer.
 * 
 * Licensed under the terms of GNU GPLv3
 * 
 * 
 *     BASIC! and OliBasic are free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     BASIC! and OliBasic are distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with BASIC!.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *     You may contact the author or current maintainers at http://rfobasic.freeforums.org
 ******************************************************************************/
package com.rfo.basicTest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;


public class KeyEventUtil  extends Activity{


public static String DO_SEND_KEYEVENT(int downUp, String action, int keyCode, 
		int meta, int pressType, int flag) {
	try{
		String targetAction = "";
	
		if (action.trim().equalsIgnoreCase("_MEDIA")){
			targetAction = Intent.ACTION_MEDIA_BUTTON;
		}else if (action.trim().equalsIgnoreCase("_Call")){
			targetAction = Intent.ACTION_CALL_BUTTON;
		}else if (action.trim().equalsIgnoreCase("_CAMERA")){
			targetAction = Intent.ACTION_CAMERA_BUTTON;
		}else{
			targetAction = "android.intent.action.GLOBAL_BUTTON";
		}
		//ViewConfiguration.getLongPressTimeout();
		//ViewConfiguration.getDoubleTapTimeout();
		if (downUp == 4){
			downUp = 0;
			KeyEvent keyEvent = new KeyEvent(downUp, keyCode);
			Intent intent = new Intent(targetAction);
			intent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
			Basic.mgActivity.sendOrderedBroadcast(intent, null);
			downUp = 1;
		}
		KeyEvent keyEvent = new KeyEvent(downUp, keyCode);
		Intent intent = new Intent(targetAction);
		intent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
		Basic.mgActivity.sendOrderedBroadcast(intent, null);
		//sendMediaButton(Basic.mgActivity, KeyEvent.KEYCODE_MEDIA_PAUSE);
	} catch (Exception e) {
		return "Error: Sending key event \n" + e;
	}
	return "";
}
private static void sendMediaButton(Context context, int keyCode) {
   KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
   Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
   intent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
   context.sendOrderedBroadcast(intent, null);

   keyEvent = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
   intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
   intent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
   context.sendOrderedBroadcast(intent, null);
}

/**
 * Create a new key event.
 * 
 * @param downTime The time (in {@link android.os.SystemClock#uptimeMillis})
 * at which this key code originally went down.
 * @param eventTime The time (in {@link android.os.SystemClock#uptimeMillis})
 * at which this event happened.
 * @param action Action code: either {@link #ACTION_DOWN},
 * {@link #ACTION_UP}, or {@link #ACTION_MULTIPLE}.
 * @param code The key code.
 * @param repeat A repeat count for down events (> 0 if this is after the
 * initial down) or event count for multiple events.
 * @param metaState Flags indicating which meta keys are currently pressed.
 * @param deviceId The device ID that generated the key event.
 * @param scancode Raw device scan code of the event.
 * @param flags The flags for this key event
 */
/*
public KeyEvent(long downTime, long eventTime, int action,
                int code, int repeat, int metaState,
                int deviceId, int scancode, int flags) {
    mDownTime = downTime;
    mEventTime = eventTime;
    mAction = action;
    mKeyCode = code;
    mRepeatCount = repeat;
    mMetaState = metaState;
    mDeviceId = deviceId;
    mScanCode = scancode;
    mFlags = flags;
}
public KeyEvent(int action, int code) {
   mAction = action;
   mKeyCode = code;
   mRepeatCount = 0;
   mDeviceId = KeyCharacterMap.VIRTUAL_KEYBOARD;
}
*/
/**
 * Create a new key event for a string of characters.  The key code,
 * action, repeat count and source will automatically be set to
 * {@link #KEYCODE_UNKNOWN}, {@link #ACTION_MULTIPLE}, 0, and
 * {@link InputDevice#SOURCE_KEYBOARD} for you.
 * 
 * @param time The time (in {@link android.os.SystemClock#uptimeMillis})
 * at which this event occured.
 * @param characters The string of characters.
 * @param deviceId The device ID that generated the key event.
 * @param flags The flags for this key event
 */

/* public KeyEvent(long time, String characters, int deviceId, int flags) {
    mDownTime = time;
    mEventTime = time;
    mCharacters = characters;
    mAction = ACTION_MULTIPLE;
    mKeyCode = KEYCODE_UNKNOWN;
    mRepeatCount = 0;
    mDeviceId = deviceId;
    mFlags = flags;
    mSource = InputDevice.SOURCE_KEYBOARD;
}

*/	


}
