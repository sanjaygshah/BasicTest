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

import static com.rfo.basicTest.Run.EventHolder.KEY_DOWN;
import java.util.Timer;
import java.util.TimerTask;
import com.rfo.basicTest.Basic.ColoredTextAdapter;
import com.rfo.basicTest.Basic.TextStyle;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;


//Log.v(Select.LOGTAG, " " + Select.CLASSTAG + " String Var Value =  " + d);

/* The User Interface used to select items from a list.
 * The user is presented with a list of things from which
 * she can select a thing.
 */

public class Select extends ListActivity {
	private static final String LOGTAG = "Select";

	public static final String EXTRA_TITLE = "select_title";
	public static final String EXTRA_MSG   = "select_msg";
	public static final String EXTRA_LIST  = "select_list";
	public static final String EXTRA_TIME  = "select_maxShowTime"; //2017-06-21gt
	public static final String EXTRA_LAYOUT  = "select_layout"; //2017-10-14gt

	private boolean lockReleased;			// safety valve so interpreter doesn't get hung if this
											// instance is destroyed without first releasing the LOCK
	//vv2017-10-14gt
	TextStyle mTextStyle = Basic.defaultTextStyle;
	//TextStyle saveDefaultTextStyle = new TextStyle(Basic.defaultTextStyle); // Do not cut the references
	int tc = mTextStyle.mTextColor;
	Typeface tf = mTextStyle.mTypeface;
	float ts = mTextStyle.mSize;
	int bc = mTextStyle.mBackgroundColor;
	Bundle layoutBundle = null;
	int selectTouchCounter = 0;
	public Timer touchTimer;
	//^^2017-10-14gt

	
	

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		String title = intent.getStringExtra(EXTRA_TITLE);
		String message = intent.getStringExtra(EXTRA_MSG);
		long timerValue = intent.getLongExtra(EXTRA_TIME, 0);//2017-06-21gt
		// If the mReturnVal (maxShowTime) at start negative the DialogBox dismiss in MilliSeconds //vv2017-06-21gt
		if (timerValue < 0) {
			final Timer timer2 = new Timer();
			timer2.schedule(new TimerTask() {
				@Override
				public void run() {
					setSelection(0, 0.0); //Changed to double because three click type possibe 2017-10-14gt
					timer2.cancel(); //this will cancel the timer of the system
				}
			}, -timerValue); // the timer will count 5 seconds....
		}//^^2017-06-21gt

		// ArrayList<String> list = intent.getStringArrayListExtra(EXTRA_LIST);//2017-02-23gt

		lockReleased = false;
		double textHtml = 0;//2018-02-01gt
		double textSelectable = 0;//2018-02-01gt
		String direction = "";//2018-02-01gt
		double bmScale = 0;//2018-02-01gt
		if (intent.hasExtra(EXTRA_LAYOUT)) { //vv2017-10-14gt
			try {
				layoutBundle = intent.getBundleExtra(EXTRA_LAYOUT);
				if (layoutBundle != null) {
					if (layoutBundle.containsKey("_TextHtml")) {//vv2018-02-01gt
						textHtml = layoutBundle.getDouble("_TextHtml", 0.0);
					}
					if (layoutBundle.containsKey("_HtmlTextSelectable")) {
						textSelectable = layoutBundle.getDouble("_HtmlTextSelectable", 0.0);
					}
					if (layoutBundle.containsKey("_HtmlBitmapScale")) {
						bmScale = layoutBundle.getDouble("_HtmlBitmapScale", 0.0);
					}
					if (layoutBundle.containsKey("_TextDirection")) {
						direction = layoutBundle.getString("_TextDirection");
					}//.TEXT_DIRECTION_ANY_RTL
					//^^2018-02-01gt
					if (layoutBundle.containsKey("_TextSize"))
						mTextStyle.mSize = new Float(
								layoutBundle.getDouble("_TextSize"));
					if (layoutBundle.containsKey("_TextColor")) {
						String textColorStr = layoutBundle.getString("_TextColor");
						mTextStyle.mTextColor = (parseColor(textColorStr));
						//mTextStyle.mTextColor = Color.RED;
					}
					String font = "";
					if (layoutBundle.containsKey("_TextFont")) {
						font = layoutBundle.getString("_TextFont");
					}
					String style = "";
					if (layoutBundle.containsKey("_TextStyle")) {
						style = layoutBundle.getString("_TextStyle");
					}
					if (font != "" || style != "") {
						int mTS = Typeface.NORMAL;
						style = style + "§";
						if (style.trim().toLowerCase().indexOf("_bold§") == 0)
							mTS = Typeface.BOLD;
						if (style.trim().toLowerCase().indexOf("_bold_italic§") == 0)
							mTS = Typeface.BOLD_ITALIC;
						if (style.trim().toLowerCase().indexOf("_italic§") == 0)
							mTS = Typeface.ITALIC;
						mTextStyle.mTypeface = Typeface.create(Typeface.DEFAULT, mTS);
						if (font.trim().toLowerCase()
								.indexOf("_default_bold§") == 0) {
							mTextStyle.mTypeface = Typeface
									.create(Typeface.DEFAULT_BOLD, mTS);
						}
						if (font.trim().toLowerCase().indexOf("_serif§") == 0) {
							mTextStyle.mTypeface = Typeface.create(Typeface.SERIF,
									mTS);
						}
						if (font.trim().toLowerCase().indexOf("_sans_serif§") == 0) {
							mTextStyle.mTypeface = Typeface.create(Typeface.SANS_SERIF,
									mTS);
						}
						if (font.trim().toLowerCase().indexOf("_monospace§") == 0) {
							mTextStyle.mTypeface = Typeface.create(Typeface.MONOSPACE,
									mTS);
						}
					}
					if (layoutBundle.containsKey("_TextBackgroundColor")) {
						String textBackgroundColor = layoutBundle
								.getString("_TextBackgroundColor");
						mTextStyle.mBackgroundColor = (parseColor(
								textBackgroundColor));
					}
					if (layoutBundle.containsKey("_Orientation")){
						setOrientation((int) layoutBundle.getDouble("_Orientation"));
					}
					//^^2017-10-14gt
				} else {
					Run.PrintShow("layoutBundle = null ????");
				}
			} catch (Exception e) {
				Run.PrintShow("Invalid layout bundle text argument(s) \n" + e);
			}
		}
		
		//ColoredTextAdapter adapter = new ColoredTextAdapter(this, list, Basic.defaultTextStyle);
		//A public static Run.selectListSelect is needed, because sometimes the device's individual max. INTENT memory size 
		//is reached.
		//ColoredTextAdapter adapter = new ColoredTextAdapter(this, Run.selectListSelect, Basic.defaultTextStyle);//2017-02-23gt
		//ColoredTextAdapter adapter = new ColoredTextAdapter(this, Run.selectListSelect, mTextStyle);//2017-02-23gt
		ColoredTextAdapter adapter = new ColoredTextAdapter(this, Run.selectListSelect, mTextStyle, 
				textHtml, textSelectable, bmScale, direction);//2018-02-01gt

		setListAdapter(adapter);
		
		ListView lv = getListView();
		lv.setTextFilterEnabled(false);
		if (title != null) { setTitle(title); }
		lv.setBackgroundColor(
			Settings.getEmptyConsoleColor(this).equals("line")
				? adapter.getLineColor()
				: adapter.getBackgroundColor());			// default is "background"
		if (layoutBundle != null) { //vv2017-10-14gt
			try {

				if (layoutBundle.containsKey("_DividerColor")) {
					String dividerColor = layoutBundle.getString("_DividerColor");
					lv.setDivider(new ColorDrawable(parseColor(dividerColor)));
				}
				if (layoutBundle.containsKey("_DividerFilename")) {
					String filename = layoutBundle.getString("_DividerFilename");
					lv.setDivider(Drawable.createFromPath(
							Basic.getFilePath(Basic.DATA_DIR, filename)));
				}
				//It's important that DividerHeight is set after setdivider
				if (layoutBundle.containsKey("_DividerHeight")) {
					int dh = (int) layoutBundle.getDouble("_DividerHeight");
					lv.setDividerHeight(dh);
				}
				if (layoutBundle.containsKey("_CacheColorHint")) {//usefull???????? 
					String setCacheColorHint = layoutBundle
							.getString("_CacheColorHint");
					lv.setCacheColorHint(parseColor(setCacheColorHint));
					//lv.setCacheColorHint(Color.TRANSPARENT);
				}
				if (layoutBundle.containsKey("_BackgroundColor")) {
					String textBackgroundColor = layoutBundle
							.getString("_BackgroundColor");
					lv.setBackgroundColor(parseColor(textBackgroundColor));
				}
				if (Build.VERSION.SDK_INT > 15) {
					if (layoutBundle.containsKey("_BackgroundWallpaper")) {
						double wp = layoutBundle.getDouble("_BackgroundWallpaper");
						if (wp > 0)
							lv.setBackground(getWallpaper());//Min Api 16
					}
					if (layoutBundle.containsKey("_BackgroundFilename")) {
						String filename = layoutBundle.getString("_BackgroundFilename");
						if (filename.length() > 0) {
							lv.setBackground(Drawable.createFromPath(
									Basic.getFilePath(Basic.DATA_DIR, filename)));//Min Api 16
						}
					}
				}
				if (layoutBundle.containsKey("_ConsoleMode")) {
					double cm = layoutBundle.getDouble("_ConsoleMode");
					if (cm > 0) {
						lv.setFocusable(true);
						lv.setFocusableInTouchMode(true);
						//lv.setTranscriptMode(0); //TRANSCRIPT_MODE_DISABLED
						//lv.setTranscriptMode(1); //TRANSCRIPT_MODE_NORMAL
						lv.setTranscriptMode(2); //TRANSCRIPT_MODE_ALWAYS_SCROLL
					}
				}
				if (layoutBundle.containsKey("_TranscriptMode")) {//Today no useful effect
					int stm = 2;//_ALWAYS_SCROLL
					String tm = layoutBundle.getString("_TranscriptMode");
					tm = tm + "§";
					if (tm.trim().toLowerCase().indexOf("_disabled§") == 0)
						stm = 0;
					if (tm.trim().toLowerCase().indexOf("_normal§") == 0)
						stm = 2;
					lv.setTranscriptMode(stm);
				}
				if (layoutBundle.containsKey("_SetSelection")) {
					int ss = (int) layoutBundle.getDouble("_SetSelection");
					lv.setSelection(ss - 1);
				}
				if (layoutBundle.containsKey("_StackFromBottom")) {
					double sfb = layoutBundle.getDouble("_StackFromBottom");
					if (sfb > 0) {
						lv.setStackFromBottom(true);
					} else {
						lv.setStackFromBottom(false);
					}
				}
			} catch (Exception e) {
				Run.PrintShow("Invalid layout bundle argument(s) \n" + e);
			}

		} //^^2017-10-14gt
		// Wait for user to select something
		//lv.setDivider(new ColorDrawable(color.black));
		//lv.setDividerHeight(6);
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {	// when user long-presses a filename line
			@Override
			public boolean  onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				setSelection(position + 1, 1.0);			// convert to 1-based item index
				return true;
			}
		});
		/*
		lv.setOnItemClickListener(new OnItemClickListener() {			// when user short-taps a filename line
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				setSelection(position + 1, false);			// convert to 1-based item index
			}
		});
		*/
		lv.setOnItemClickListener(new OnItemClickListener() {			// when user short-taps a  line
			//vv2017-10-14gt
			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				++selectTouchCounter;
				if (selectTouchCounter == 1){
					if (touchTimer != null) {
						touchTimer.cancel();
						touchTimer = null;
					}
					touchTimer = new Timer();
					touchTimer.schedule(new TimerTask() {
						@Override
						public void run() {
							if (touchTimer != null) {
								touchTimer.cancel();
								touchTimer = null;
							}
							selectTouchCounter = 0;
							setSelection(position + 1, 0.0);			// convert to 1-based item index //set ClickType to 0 one short touch
						}
					}, ViewConfiguration.getDoubleTapTimeout()); 
				}
				if (selectTouchCounter == 2){
					if (touchTimer != null) {
						touchTimer.cancel();
						touchTimer = null;
					}
					selectTouchCounter = 0;
					setSelection(position + 1, 2.0);			// convert to 1-based item index //set ClickType to 2 double touch
				}
			}//^^2017-10-14gt
		});
		if ((message != null) && !message.equals("")) {
			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();	// Display the user's toast
		}
	} // onCreate
	public void setSelection(int item, double isLongClick) {
		if (lockReleased) return;

		synchronized (Run.LOCK) {
			Run.SelectedItem = item;						// 1-based index of selected item
			Run.SelectLongClick = isLongClick;
			Run.mWaitForLock = false;
			lockReleased = true;
			Run.LOCK.notify();								// release the lock that Run is waiting for
		}
		//Reset to main values //vv2017-10-14gt
		mTextStyle.mTextColor = tc;
		mTextStyle.mTypeface = tf;
		mTextStyle.mSize = ts;
		mTextStyle.mBackgroundColor = bc;
		//^^2017-10-14gt
		finish();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {				// If user presses back key
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { // go back to Run without a selection
			// Set the click type to 0 because three type are possible now. 2017-10-14gt
			setSelection(0, 0.0);							// zero indicates no selection made
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	
	@Override//vv2017-10-13gt Copied from GR.java
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Log.v(LOGTAG, "keyDown " + keyCode);
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return super.onKeyDown(keyCode, event);
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			// Do not put the KeyEvent on the EventList. This keeps Run.onKeyDown() from building a menu.
			//Run.mEventList.add(new Run.EventHolder(KEY_DOWN, keyCode, null));//2017-02-20gt
			event = null;//2017-02-20gt
		}
		Run.mEventList.add(new Run.EventHolder(KEY_DOWN, keyCode, event));//2017-02-20gt
		return true;									// ignore anything else
	}//vv2017-10-13gt


	@Override
	public void onDestroy() {
		// Set the click type to 0 because three type are possible now. 2017-10-14gt
		setSelection(0, 0.0);						// if not already released, release the lock that Run is waiting for
		super.onDestroy();
	}

	public void refresh() {									// set fields from setup.xml values and Preferences settings
		Context appContext = Basic.mContextMgr.getContext(ContextManager.ACTIVITY_APP);
	}
	public static int parseColor(String input) {	//vv2017-10-14gt
		//{Alpha,}Red,Green,Blue
		int mColor = 0;
		try {
		input = input + ",";
		String[] cLine = input.split("[,]");
		int alpha = 255;
		int alphaOk = 0;
		if (cLine.length == 4){
			alphaOk = 1;
			if (cLine[0] != "")alpha = new Integer (cLine[0]);
		}
		int red = 0;
		if (cLine[0 + alphaOk] != "")red = new Integer (cLine[0 + alphaOk]);
		int green = 0;
		if (cLine[1 + alphaOk] != "")green = new Integer (cLine[1 + alphaOk]);
		int blue = 0;
		if (cLine[2 + alphaOk] != "")blue = new Integer (cLine[2 + alphaOk]);
		mColor = alpha * 0x1000000 + red * 0x10000 + green * 0x100 + blue;
		} catch (Exception e) {
			Run.PrintShow(input + " is invalid color argument(s) \n" + e);
		}

		return mColor;
	}	//^^2017-10-14gt

	@TargetApi(Build.VERSION_CODES.GINGERBREAD) //From GR class 2017-10-14gt
	public void setOrientation(int orientation) {		// Convert and apply orientation setting
		Log.v(LOGTAG, "Set orientation " + orientation);
		switch (orientation) {
			default:
			case 1:  orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT; break;
			case 3:  orientation = (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD)
								 ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
								 : ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
				break;
			case 0:  orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE; break;
			case 2:  orientation = (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD)
								 ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
								 : ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
				break;
			case -1: orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR; break;
		}
		setRequestedOrientation(orientation);
	}


}
