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
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import com.rfo.basicTest.R;


public class TextInput extends Activity {
	//private Button finishedButton;			// The buttons//2017-09-20gt
	private EditText theTextView;			// The EditText TextView
	private Menu mMenu = null;//2017-09-20gt

	private boolean lockReleased;			// safety valve so interpreter doesn't get hung if this
											// instance is destroyed without first releasing the LOCK

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)  {
		// if BACK key leave original text unchanged
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			releaseLock();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.text_input);	// Layout xmls exist for both landscape and portrait modes

		Intent intent = getIntent();
		String title = intent.getStringExtra("title");
		if (title != null) { setTitle(title); }
		lockReleased = false;

		//finishedButton = (Button) findViewById(R.id.finished);		// The buttons//Deleted, menu now//2017-09-20gt
		theTextView = (EditText) findViewById(R.id.the_text);				// The text display area
		theTextView.setText(Run.TextInputString);							// The Editor's display text
		theTextView.setTypeface(Typeface.MONOSPACE);
		theTextView.setSelection(Run.TextInputString.length());

		Basic.TextStyle style = Basic.defaultTextStyle;						// Get text color from Settings
		theTextView.setTextSize(1, Settings.getFont(this));
		// theTextView.setBackgroundColor(style.mBackgroundColor);//vv2017-09-20gt
		// theTextView.setTextColor(style.mTextColor);
		String colorString = "";
		int mColor = Color.parseColor("#FF000000");
		if (intent.hasExtra("setTextColor")){
			colorString = intent.getStringExtra("setTextColor");
			colorString = colorString.toLowerCase().replace("_", "");
			mColor = Color.parseColor(colorString);		
		}else{
			mColor = style.mTextColor;		
		}
		theTextView.setTextColor(mColor);
		mColor = Color.parseColor("#FFFFFFFF");	
		if (intent.hasExtra("setBackgroundColor")){
			colorString = intent.getStringExtra("setBackgroundColor");
			colorString = colorString.toLowerCase().replace("_", "");
			mColor = Color.parseColor(colorString);		
		}else{
			mColor = style.mBackgroundColor;		
		}
		theTextView.setBackgroundColor(mColor);
		
		//vv2017-09-20gt
		InputFilter[] filters = theTextView.getFilters();				// some devices (Samsung) have a filter that limits EditText size
		if (filters.length != 0) {
			theTextView.setFilters(new InputFilter[0]);				// if there are any filters, remove them
		}
		if (intent.hasExtra("setInputType")){		// To prevent automatic word completions or -corrections, // vv 2017-08-03gt
		// because you get in trouble with the value name "thi" that returns "think" after pressing a space character.
			if(intent.getIntExtra("setInputType", 0) > 0)
				theTextView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		}
		//^^2017-09-20gt
	
		/* Obsolete because Menu 2017-09-20gt
		finishedButton.setOnClickListener(new OnClickListener() {			// **** Done Button ****

			public void onClick(View v) {
				Run.TextInputString = theTextView.getText().toString();		// Grab the text that the user is seeing
				releaseLock();
				return;
			}
		});
		*/
	}

	public void releaseLock() {
		if (lockReleased) return;

		synchronized (Run.LOCK) {
			Run.mWaitForLock = false;
			lockReleased = true;
			Run.LOCK.notify();								// release the lock that Run is waiting for
		}
		setResult(99);//2017-06-24gt
		finish();
	}

	@Override
	public void onDestroy() {
		releaseLock();										// if not already released, release the lock that Run is waiting for
		super.onDestroy();
	}

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.text_input, menu);
       return true;
   }
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
      case R.id.finished:
				Run.TextInputString = theTextView.getText().toString();		// Grab the text that the user is seeing
				releaseLock();
          return true;
          
      case R.id.stop:
				releaseLock();
          return true;
          
      }
       return super.onOptionsItemSelected(item);
   }

}
