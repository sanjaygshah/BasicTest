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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;
// Still a construction site
public class StartIntent extends Activity {// implements SurfaceHolder.Callback,
	public static final String stdFlags = ""
	+"FLAG_GRANT_READ_URI_PERMISSION=0x00000001;"
	+"FLAG_GRANT_WRITE_URI_PERMISSION=0x00000002;"
	+"FLAG_FROM_BACKGROUND=0x00000004;"
	+"FLAG_DEBUG_LOG_RESOLUTION=0x00000008;"
	+"FLAG_EXCLUDE_STOPPED_PACKAGES=0x00000010;"
	+"FLAG_INCLUDE_STOPPED_PACKAGES=0x00000020;"
	+"FLAG_GRANT_PERSISTABLE_URI_PERMISSION=0x00000040;"
	+"FLAG_ACTIVITY_NO_HISTORY=0x40000000;"
	+"FLAG_ACTIVITY_SINGLE_TOP=0x20000000;"
	+"FLAG_ACTIVITY_NEW_TASK=0x10000000;"
	+"FLAG_ACTIVITY_MULTIPLE_TASK=0x08000000;"
	+"FLAG_ACTIVITY_CLEAR_TOP=0x04000000;"
	+"FLAG_ACTIVITY_FORWARD_RESULT=0x02000000;"
	+"FLAG_ACTIVITY_PREVIOUS_IS_TOP=0x01000000;"
	+"FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS=0x00800000;"
	+"FLAG_ACTIVITY_BROUGHT_TO_FRONT=0x00400000;"
	+"FLAG_ACTIVITY_RESET_TASK_IF_NEEDED=0x00200000;"
	+"FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY=0x00100000;"
	+"FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET=0x00080000;"
	+"FLAG_ACTIVITY_NO_USER_ACTION=0x00040000;"
	+"FLAG_ACTIVITY_REORDER_TO_FRONT=0X00020000;"
	+"FLAG_ACTIVITY_NO_ANIMATION=0X00010000;"
	+"FLAG_ACTIVITY_CLEAR_TASK=0X00008000;"
	+"FLAG_ACTIVITY_TASK_ON_HOME=0X00004000;"
	+"FLAG_RECEIVER_REGISTERED_ONLY=0x40000000;"
	+"FLAG_RECEIVER_REPLACE_PENDING=0x20000000;"
	+"FLAG_RECEIVER_FOREGROUND=0x10000000;"
	+"FLAG_RECEIVER_NO_ABORT=0x08000000;"
	+"FLAG_RECEIVER_REGISTERED_ONLY_BEFORE_BOOT=0x04000000;"
	+"FLAG_RECEIVER_BOOT_UPGRADE=0x02000000;";

	public static boolean dB = false;
	public static EditText Text1;
	public static final int REQUEST_CODE = 111;
	public static final int REQUEST_CODE2 = 112;
	// public ArrayList<String> VarNames ; // Each entry has the variable name
	// string
	public static String chooserText = null;

	private static boolean DebugSI = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DebugSI = Run.intentBundle.getBoolean("DebugSI");
	   if(DebugSI)Run.PrintShow("Choosing");

		switch (Run.intentToDo) {
		case 1: // "App.SAR"       Packages"
			app_SAR();
			break;
		case 2: 
			app_START();
			break;	
		default:
		}

	}
	private void app_START() {//vv2017-07-13gt
		Intent intent = Run.intentOfIntent;
		if (intent.resolveActivity(getPackageManager()) != null) { 
			startActivity(intent);
			//startActivityForResult(intent, REQUEST_CODE2); // Only for first test.

			Run.intentBundle.putString("%succededResults%", "");
		} else {
			Run.intentBundle.putString("%succededResults%", "ERROR: unresolved Activity");
		}
		Run.StartIntentDone = true;
		finish();
	}//^^2017-07-13gt

	@SuppressLint("NewApi")
	private void app_SAR() {
		try {
			Intent intent = new Intent();
			if(DebugSI)Run.PrintShow("AA");
		ArrayList<String> commandList;
		if(Run.intentBundle.containsKey("_CommandList")){//2016-11-24gt
			commandList = Run.intentBundle.getStringArrayList("_CommandList");
		}else{
			commandList = Run.intentBundle.getStringArrayList("***CommandList***");
		}
		
		//	String[] commandList = Run.intentBundle.getStringArray("listS#commandListPointer");
		if(DebugSI)Run.PrintShow("EE");
	//	if(DebugSI)Run.PrintShow(commandList[0]);

		if(DebugSI)Run.PrintShow("FF");
		chooserText = null; //2018-07-20gt

		String c34 = "" + ((char)34);
		
		String sA = "";
		for (String k : commandList) {
			//String r[] = k.split("#");
			if(DebugSI)Run.PrintShow("commandList:" + k );
			String g = k.replaceAll("[ ]", "");
			if(DebugSI)Run.PrintShow("commandList:" + g.toLowerCase() );
			k += ";";
			String r[] = (k+"))#").split("[(,)]");	
				if (k.indexOf("(") != -1) {
					//if (e1 > k.length()) { e1 = k.length(); }
					//r[0] = k.substring(0, k.indexOf("(") -1);
					r[0] = k.substring(0, k.indexOf("(") );
					r[1] = k.substring(k.indexOf("(") + 1);
					r[1] = r[1].substring(0, r[1].lastIndexOf(");"));
					if (r[1].indexOf('"') != 0) {
						//r[0] = k.substring(0, k.indexOf("(") - 1);
						r[0] = k.substring(0, k.indexOf("(") );
						r[1].replaceAll("," + c34, "\uffff" + c34);// Add an undef. character
						r[1].replaceAll("," + c34 + ",", c34 + "\uffff" + c34);
						r[1].replaceAll(c34 + ",", c34 + "\uffff");
					} else {
						r[1].replaceAll(",", "\uffff");
					}
					String rTok[] = r[1].split("\uffff");
					for (int j = 0; j < rTok.length; ++j) {
						r[j + 1] = rTok[j];
					}
				}
			if(DebugSI)Run.PrintShow("r[0]:" + r[0] );
			if(DebugSI)Run.PrintShow("r[1]:" + r[1] );
			if(DebugSI)Run.PrintShow("r[2]:" + r[2] );
			if (g.toLowerCase().indexOf("newintent(") == 0) {
				if (g.toLowerCase().indexOf("newintent()") != 0) {
					sA = r[1];
					if (sA.replaceAll("[ ]", "").indexOf('"') == 0) {
						//setAction("com.google.zxing.client.android.SCAN");
						String s[] = (sA+ c34+"#").split(c34);
						if (s[1]!= "")intent.setAction(s[1]);
						if(DebugSI)Run.PrintShow("s[0]:" + s[0] );
						if(DebugSI)Run.PrintShow("s[1]:" + s[1] );
					}else{
						//new Intent(Intent.ACTION_PICK); -->  "android.intent.action.PICK"
						if (sA.toLowerCase().indexOf("intent.") == 0) {
							String t[] = sA.split("[._]",3); //Does not work correctly!
							if(DebugSI)Run.PrintShow("t[0]:" + t[0] );
							if(DebugSI)Run.PrintShow("t[1]:" + t[1] );
							if(DebugSI)Run.PrintShow("t[2]:" + t[2] );
							if(DebugSI)Run.PrintShow("action1:" + t[2]  );
							if(DebugSI)Run.PrintShow("action1:" + ("android.intent.action." + t[2].toUpperCase()) );
							if (t[2]!= "")intent.setAction("android.intent.action." + t[2].toUpperCase());
							if(DebugSI)Run.PrintShow("action1:" + t[2] + "  -- > android.intent.action." + t[2].toUpperCase() );
						}else{
							// search variable
						}
					}
				}
			}
			if (g.toLowerCase().indexOf("setaction(") == 0) {
				sA = r[1];
				if (sA.replaceAll("[ ]", "").indexOf('"') == 0) {
					//setAction("com.google.zxing.client.android.SCAN");
					String s[] = (sA+ c34+"#").split(c34);
					if (s[1]!= "")intent.setAction(s[1]);
					if(DebugSI)Run.PrintShow("t[0]:" + s[0] );
					if(DebugSI)Run.PrintShow("t[1]:" + s[1] );
				}else{
					if (sA.toLowerCase().indexOf("intent.") == 0) {
						String t[] = sA.split("[._]",3); //Does not work correctly!
						if(DebugSI)Run.PrintShow("action2:" + t[2]  );
						if(DebugSI)Run.PrintShow("action2:" + ("android.intent.action." + t[2].toUpperCase()) );
						if (t[2]!= "")intent.setAction("android.intent.action." + t[2].toUpperCase());
						if(DebugSI)Run.PrintShow("action2:" + t[2] + "  -- > android.intent.action." + t[2].toUpperCase() );
					}else{
							// search variable
					}
				}
			}
			if (g.toLowerCase().indexOf("setpackage(") == 0) {
				sA = r[1];
				if (sA.replaceAll("[ ]", "").indexOf('"') == 0) {
					//setPackage("com.google.zxing.client.android");
					String s[] = (sA+ c34+"#").split(c34);
					if (s[1]!= "")intent.setPackage(s[1]);
					if(DebugSI)Run.PrintShow("t[0]:" + s[0] );
					if(DebugSI)Run.PrintShow("t[1]:" + s[1] );
				}else{
					// search variable
				}
				
			}
			//  overwrites intent definition before vv 2017-07-08gt
			if (g.toLowerCase().indexOf("getlaunchintentforpackage(") == 0 || g.toLowerCase().indexOf("getpackagemanager().getlaunchintentforpackage(") == 0) {
				sA = r[1];
				if (sA.replaceAll("[ ]", "").indexOf('"') == 0) {
					//setPackage("com.google.zxing.client.android");
					String s[] = (sA+ c34+"#").split(c34);
					if (s[1]!= "")intent = getPackageManager().getLaunchIntentForPackage(s[1]);
					if(DebugSI)Run.PrintShow("t[0]:" + s[0] );
					if(DebugSI)Run.PrintShow("t[1]:" + s[1] );
				}else{
					// search variable
				}
				
			} //^^ 2017-07-08gt
			
			
			//createChooser(intent, s[1])	//vv2018-07-20gt
			if (g.toLowerCase().indexOf("createchooser(") == 0 ) {
				sA = r[1];
				if (sA.replaceAll("[ ]", "").indexOf('"') == 0) {
					//setPackage("com.google.zxing.client.android");
					String s[] = (sA+ c34+"#").split("["+ c34 + "]");
					chooserText = s[1];
					if(DebugSI)Run.PrintShow("s[0]:" + s[0] );
					if(DebugSI)Run.PrintShow("s[1]:" + s[1] + "  " + s[1]);
				}else{
					// search variable
				}
				
			}//^^2018-07-20gt
			
			//setData(Uri.fromFile(aFile));		
			if (g.toLowerCase().indexOf("setdata(") == 0 || (g.toLowerCase().indexOf("setdatandnormalize(") == 0)) {
				sA = r[1];
				if (sA.replaceAll("[ ]", "").indexOf('"') == 0) {
					//setPackage("com.google.zxing.client.android");
					String s[] = (sA+ c34+"#").split("["+ c34 + "]");
					if(DebugSI)Run.PrintShow("externalFile setData:" + s[1] +" Uri.parse " + Uri.parse(s[1]));
					if (Build.VERSION.SDK_INT < 16){//2017-11-25gt
						if (s[1]!= "")intent.setData(Uri.parse(s[1]));
					}else{//vv2017-11-25gt
						if (s[1]!= "")intent.setDataAndNormalize(Uri.parse(s[1]));
					}//^^2017-11-25gt
					if(DebugSI)Run.PrintShow("s[0]:" + s[0] );
					if(DebugSI)Run.PrintShow("s[1]:" + s[1] + "  " + Uri.parse(s[1]));
					if(DebugSI)Run.PrintShow("s[1]:" + s[1] + "  " + Uri.parse(s[1].toLowerCase()));
				}else{
					// search variable
				}
				
			}
			
					//setTypeAndNormalize(String type)
					if ((g.toLowerCase().indexOf("settypeandnormalize(") == 0) || (g.toLowerCase().indexOf("settype(") == 0)) {
						sA = r[1];
						if (sA.replaceAll("[ ]", "").indexOf('"') == 0) {
							//setPackage("com.google.zxing.client.android");
							String s[] = (sA+ c34+"#").split(c34);
							if(DebugSI)Run.PrintShow("externalFile setType(AndNormalize):" + s[1]);
							if (s[1]!= "")intent.setType(normalizeMimeType(s[1]));
							if(DebugSI)Run.PrintShow("t[0]:" + s[0] );
							if(DebugSI)Run.PrintShow("t[1]:" + s[1] );
						}else{
							// search variable
						}
						
					}
					if (g.toLowerCase().indexOf("setdata(null)") == 0 ) { //Clear Intent data type //vv2017-03-23gt
						intent.setData(null);
					}	//^^2017-03-23gt
			
			String first = "";
			if (g.toLowerCase().indexOf("setdataandtype(") == 0 || (g.toLowerCase().indexOf("setdataandtypeandnormalize(") == 0)) {
				//setDataAndType(file, "application/pdf");
				sA = r[1];
				first = "";
				if (sA.replaceAll("[ ]", "").indexOf('"') == 0) {
					//setAction("com.google.zxing.client.android.SCAN");
					String s[] = (sA+ c34+"#").split(c34);
					first = s[1];
					if(DebugSI)Run.PrintShow("t[0]:" + s[0] );
					if(DebugSI)Run.PrintShow("t[1]:" + s[1] );
				}else{
					// To delete?
					/*
					if (sA.toLowerCase().indexOf("intent.") == 0) {
							String t[] = sA.split("[.]",2);
							first = "android.intent.extra." + t[1].toUpperCase();
							if(DebugSI)Run.PrintShow("extra:" + first );
					}else{
					*/
					// search variable
					}
				}
				sA = r[2]; //What type of variable?
				if (sA.replaceAll("[ ]", "").indexOf('"') == 0) {
					//Second Parameter
					String t[] = (sA+ c34+"#").split(c34);
					if (first!= "" && t[1]!= ""){
						if(DebugSI)Run.PrintShow("externalFile setDataAndType:" + first+"  " + Uri.parse(first));
					//	File externalFile = new File(first);
					//	intent.setDataAndType(Uri.fromFile(externalFile),t[1] );
				//		intent.setDataAndType(Uri.parse(first.toLowerCase()),normalizeMimeType(t[1]) );
						intent.setDataAndType(Uri.parse(first),normalizeMimeType(t[1]) );
					}
					if(DebugSI)Run.PrintShow("t[0]:" + t[0] );
					if(DebugSI)Run.PrintShow("t[1]:" + t[1] );
				}else{
					// search variable
				}

				
				
				
				
			if (g.toLowerCase().indexOf("putextra(") == 0) {
				//putExtra("SCAN_MODE", "QR_CODE_MODE");
				//putExtra(Intent.EXTRA_TITLE, "MyScanApp");
				//EXTRA_TITLE = "android.intent.extra.TITLE"
				sA = r[1];
				first = "";
				if (sA.replaceAll("[ ]", "").indexOf('"') == 0) {
					//setAction("com.google.zxing.client.android.SCAN");
					String s[] = (sA+ c34+"#").split(c34);
					first = s[1];
					//if (s[1]!= "")intent.setPackage(s[1]);
					if(DebugSI)Run.PrintShow("t[0]:" + s[0] );
					if(DebugSI)Run.PrintShow("t[1]:" + s[1] );
				}else{
					if (sA.toLowerCase().indexOf("intent.") == 0) {
							String t[] = sA.split("[.]",2);
							first = "android.intent.extra." + t[1].toUpperCase();
							if(DebugSI)Run.PrintShow("extra:" + first );
					}else{
					// search variable
					}
				}
				sA = r[2]; //What type of variable?
				if (sA.replaceAll("[ ]", "").indexOf('"') == 0) {
					//Second Parameter
					String t[] = (sA+ c34+"#").split(c34);
					
					if (first!= "" && t[1]!= "")intent.putExtra(first,t[1] );
					if(DebugSI)Run.PrintShow("first:" + first );
					if(DebugSI)Run.PrintShow("t[0]:" + t[0] );
					if(DebugSI)Run.PrintShow("t[1]:" + t[1] );
				}else{
					// search variable
					//intent.putExtra(first, Bundle value)
				}

					if (sA.toLowerCase().indexOf("!") == 0) {
						//if (first!= "" )intent.putExtra(first,true );
						if (DebugSI)
							Run.PrintShow("first:" + first);
						if (DebugSI)Run.PrintShow("sA:" + sA);
						if (Run.intentBundle.containsKey(sA)) {
							if (DebugSI)Run.PrintShow("containsKey:" + sA);
							Object o = Run.intentBundle.get(sA);
							if (o instanceof Bundle) {
								intent.putExtra(first, Run.intentBundle.getBundle(sA));
								if (DebugSI)Run.PrintShow("intent.putExtra:" + sA);
							}
							if (o instanceof Double) {
								if (sA.toLowerCase().indexOf("!int#") == 0
										|| sA.toLowerCase().indexOf("!integer#") == 0) {
									Double o1 = Run.intentBundle.getDouble(sA);
									//Long o2 = o1.longValue();
									intent.putExtra(first, o1.intValue());
									if (DebugSI)Run.PrintShow("intent.putExtra Integer:" + sA);
								} else if (sA.toLowerCase().indexOf("!long#") == 0) {
									Double o1 = Run.intentBundle.getDouble(sA);
									intent.putExtra(first, o1.longValue());
									if (DebugSI)Run.PrintShow("intent.putExtra Long:" + sA);
								} else {
									intent.putExtra(first,Run.intentBundle.getDouble(sA));
									if (DebugSI)Run.PrintShow("intent.putExtra Double:" + sA);

								}
								//intent.putExtra(first, Run.intentBundle.getBundle(sA));
								//if(DebugSI)Run.PrintShow("intent.putExtra Double:" + sA );
							}
						}
					}

				if (sA.toLowerCase().indexOf("true") == 0) {
					if (first!= "" )intent.putExtra(first,true );
					if(DebugSI)Run.PrintShow("first:" + first );
					if(DebugSI)Run.PrintShow("sA:" + sA );
				}
				if (sA.toLowerCase().indexOf("false") == 0) {
					if (first!= "" )intent.putExtra(first,false );
					if(DebugSI)Run.PrintShow("first:" + first );
					if(DebugSI)Run.PrintShow("sA:" + sA );
				}
				
			}
			/*
			 String key = first; 
			 Object value = entry.getValue();
			 if(value instanceof Integer) { 
				 intent.putExtra(key, (Integer) value); }
			 else if(value instanceof Long) { 
				 intent.putExtra(key, (Long) value);
			 }else if(value instanceof Boolean) { 
				 intent.putExtra(key, (Boolean)value); 
			 }else if(value instanceof Double) { 
				 intent.putExtra(key,(Double) value); 
			 }else if(value instanceof Float) {
				 intent.putExtra(key, (Float) value); 
			 }else if(value instanceof Bundle){ 
				 intent.putExtra(key, (Bundle) value); 
			 }else{ 
			 	 intent.putExtra(key,value.toString()); 
			 } 
			 */
			
			
			
			//String searchStr = c34+"addCategory(Intent.CATEGORY_DEFAULT)"+c34;
			//Intent.CATEGORY_DEFAULT = "android.intent.category.DEFAULT"
			//if (g.toLowerCase().indexOf(searchStr.toLowerCase()) == 0)intent.addCategory("android.intent.category.DEFAULT");
			
			if (g.toLowerCase().indexOf("addcategory(") == 0) {
				sA = r[1].replaceAll("[ ]", "");
					if (sA!= ""){
						String t[] = sA.split("[._]",3);
						intent.addCategory("android.intent.category." + t[2].toUpperCase());
						if(DebugSI)Run.PrintShow("addCategory:" + "android.intent.category." + t[2].toUpperCase() );
					}
				//}else{
					// search variable
				//}
			}

			first = ""; //vv2017-06-25gt
			//setComponent(new ComponentName("The package name of the activity that you wish to launch","Its fully qualified class name"))
			if (g.toLowerCase().indexOf("newcomponentname(") == 0 || g.toLowerCase().indexOf("setcomponent(newcomponentname(") == 0) {
				//setDataAndType(file, "application/pdf");
				sA = r[1];
				first = "";
				if (sA.replaceAll("[ ]", "").indexOf('"') == 0) {
					//setAction("com.google.zxing.client.android.SCAN");
					String s[] = (sA+ c34+"#").split(c34);
					first = s[1];
					if(DebugSI)Run.PrintShow("t[0]:" + s[0] );
					if(DebugSI)Run.PrintShow("t[1]:" + s[1] );
				}else{
					// To delete?
					/*
					if (sA.toLowerCase().indexOf("intent.") == 0) {
							String t[] = sA.split("[.]",2);
							first = "android.intent.extra." + t[1].toUpperCase();
							if(DebugSI)Run.PrintShow("extra:" + first );
					}else{
					*/
					// search variable
					}
				}
				sA = r[2]; //What type of variable?
				if (sA.replaceAll("[ ]", "").indexOf('"') == 0) {
					//Second Parameter
					String t[] = (sA+ c34+"#").split(c34);
					if (first!= "" && t[1]!= ""){
						if(DebugSI)Run.PrintShow("setComponent(new ComponentName(:" + first+"  " + t[1]);
						intent.setComponent(new ComponentName(first,t[1]));
					}
					if(DebugSI)Run.PrintShow("t[0]:" + t[0] );
					if(DebugSI)Run.PrintShow("t[1]:" + t[1] );
				}else{
					// search variable
				} //^^2017-06-25gt


			
			
			//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			if (g.toLowerCase().indexOf("addflags(") == 0) {
				sA = r[1].replaceAll("[ ]", "");
					//addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					if (sA!= ""){
						String t[] = sA.split("[.]");
						int woSt = stdFlags.indexOf(t[1].toUpperCase());
						if(DebugSI)Run.PrintShow("woSt:" + woSt +  "   " + t[1]);

						if (woSt > -1){
							int woGl = stdFlags.indexOf("=", woSt);
							String N = stdFlags.substring(woGl+3, woGl+11);	//0x"04000000";
							if(DebugSI)Run.PrintShow("N:" + N );
							int nI = (int) new BigInteger(N,16).longValue();
							intent.addFlags(nI);
							if(DebugSI)Run.PrintShow("addFlags:" + nI );
							//nI = (int) new BigInteger("04000000",16).longValue();
							//if(DebugSI)Run.PrintShow("addFlags:" + nI );
						}
					}
			//	}else{
					// search variable
			//	}
			}
			if (g.toLowerCase().indexOf("setflags(0)") == 0 ) { //Clear Intent Flags //vv2017-03-23gt
				intent.setFlags(0);
			}	//^^2017-03-23gt
			if (g.toLowerCase().indexOf("replaceextras(new bundle())") == 0 ) { //Clear Intent Extras //vv2017-03-23gt
				intent.replaceExtras(new Bundle());
			}	//^^2017-03-23gt
	
			
		}
			
		if(DebugSI)Run.PrintShow("startActivity");
			if (Run.intentBundle.containsKey("_ResultList")||Run.intentBundle.containsKey("***ResultList***") ) {
				Run.intentBundle.putString("%succededResults%", "");
				if(DebugSI)Run.PrintShow("startActivityForResult");
				if(chooserText == null){//2018-07-20gt
					startActivityForResult(intent, REQUEST_CODE);
				}else{//vv2018-07-20gt
					startActivityForResult(Intent.createChooser(intent, chooserText), REQUEST_CODE);
				}//^^2018-07-20gt
			} else if (Run.intentBundle.containsKey("_Broadcast")||Run.intentBundle.containsKey("_BroadCast")){//vv2017-02-08gt
				if(DebugSI)Run.PrintShow("Send BroadCast");

				Context context = getApplicationContext();
				//context.sendBroadcast(intent);
				sendBroadcast(intent);
				Run.intentBundle.putString("%succededResults%", "");
				Run.StartIntentDone = true;
				finish();//^^2017-02-08gt
			} else {
				if (intent.resolveActivity(getPackageManager()) != null) { 
					if(chooserText == null){//2018-07-20gt
						startActivity(intent);
					}else{//vv2018-07-20gt
						startActivity(Intent.createChooser(intent, chooserText));
					}//^^2018-07-20gt
					Run.intentBundle.putString("%succededResults%", "");//vv 2017-01-08 gt
				} else {
					Run.intentBundle.putString("%succededResults%", "ERROR: unresolved Activity");//^^ 2017-01-08 gt
				}
				Run.StartIntentDone = true;
				finish();
			}

		} catch (ActivityNotFoundException e) {
			if (DebugSI)
				Run.PrintShow(e.toString());
		}
	}
	private void shareContent(String update) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, update);
		startActivity(Intent.createChooser(intent, "Share..."));
	}

	/*
	 * Uri uri = Uri.parse("file:///path/to/the/video.mp4"); Intent shareIntent
	 * = new Intent(Intent.ACTION_VIEW, uri);
	 * shareIntent.setPackage("org.videolan.vlc"); startActivity(shareIntent);
	 */
	private void viewVideo(Uri file) {
		Intent intent;
		intent = new Intent(Intent.ACTION_VIEW);
		// intent.setDataAndType(file, "video/mp4");
		intent.setDataAndType(file, "video");
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			// No application to view, ask to download one
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("No Application Found");
			builder.setMessage("Download one from Android Market?");
			builder.setPositiveButton("Yes, Please",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent marketIntent = new Intent(Intent.ACTION_VIEW);
							marketIntent.setData(Uri
									.parse("market://details?id=com.adobe.reader"));
							startActivity(marketIntent);
						}
					});
			builder.setNegativeButton("No, Thanks", null);
			builder.create().show();
		}
	}



	public static String convertStreamToString(InputStream is) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		reader.close();
		return sb.toString();
	}

	public static String getStringFromFile(String filePath) throws Exception {
		File fl = new File(filePath);
		FileInputStream fin = new FileInputStream(fl);
		String ret = convertStreamToString(fin);
		// Make sure you close all streams.
		fin.close();
		return ret;
	}
/**
 * ************************
 * 
 * ************************
*/




//REQUEST_CODE
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data); //gt 2016-06-03
		if(DebugSI)Run.PrintShow("requestCode: " + requestCode);
		if (requestCode == REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				if(DebugSI)Run.PrintShow("Has finished: ");
				if(DebugSI)Run.PrintShow("Returned data: " + data);
				if (data != null) {
					if(DebugSI)Run.PrintShow("DD");
					ArrayList<String> resultList;
					if (Run.intentBundle.containsKey("_ResultList") ) {
						
						resultList = Run.intentBundle.getStringArrayList("_ResultList");
					}else{
						resultList = Run.intentBundle.getStringArrayList("***ResultList***");
					}
						//String[] resultList = Run.intentBundle.getStringArray("listS#resultListPointer");
					if(DebugSI)Run.PrintShow("EE");
					//if(DebugSI)Run.PrintShow(resultList[0]);

					if(DebugSI)Run.PrintShow("FF");

					StringBuilder sb = new StringBuilder();
					sb.append((char) 34);
					String c34 = sb.toString();

					String sA = "";
					for (String k : resultList) {
						//
						if(DebugSI)Run.PrintShow("resultList:" + k);
						String gS = k.replaceAll("[ ]", "");
						String gSS[] = (gS + "==#").split("=");
						String g = gSS[1];
						if(DebugSI)Run.PrintShow("resultList:" + g.toLowerCase());
						String r[] = (k + ",,#").split("[=(,)]");
						if(DebugSI)Run.PrintShow("r[0]:" + r[0]);
						if(DebugSI)Run.PrintShow("r[1]:" + r[1]);
						if(DebugSI)Run.PrintShow("r[2]:" + r[2]);

						//"result$=getStringExtra("+CHR$(34)+"SCAN_RESULT"+CHR$(34)+");"
						// result = data.getStringExtra("SCAN_RESULT");
						if(DebugSI)Run.PrintShow("sA:" + sA);
						if (g.toLowerCase().indexOf("getstringextra(") == 0) {
							sA = r[2];
							if(DebugSI)Run.PrintShow("sA:" + sA);
							if (sA.replaceAll("[ ]", "").indexOf('"') == 0) {
								//getStringExtra("SCAN_RESULT");
								String s[] = (sA + c34 + "#").split(c34);
								if(DebugSI)Run.PrintShow("getStringExtra:" + s[1]);
								String vari = gSS[0];
								if (s[1] != "" && Run.intentBundle.containsKey(gSS[0])) {
									Run.intentBundle.putString(gSS[0],data.getStringExtra(s[1]));
									Run.intentBundle.putString("%succededResults%", Run.intentBundle.getString("%succededResults%") + gSS[0]+ ",");
								}
								if(DebugSI)Run.PrintShow("data.getStringExtra(s[1]):" + data.getStringExtra(s[1]));
								if(DebugSI)Run.PrintShow("%succededResults%:" + Run.intentBundle.getString("%succededResults%"));
							} else {
								// search variable
							}

						}
									
						if (g.toLowerCase().indexOf("getbundleextra(") == 0) {
							sA = r[2];
							if(DebugSI)Run.PrintShow("sA:" + sA);
							if (sA.replaceAll("[ ]", "").indexOf('"') == 0) {
								//getStringExtra("SCAN_RESULT");
								String s[] = (sA + c34 + "#").split(c34);
								if(DebugSI)Run.PrintShow("getbundleextra:" + s[1]);
								//	if(DebugSI)Run.PrintShow("r[3]:" + r[3]+","+s[1]+","+gSS[0]+","+Run.intentBundle.containsKey(gSS[0]));
								if (s[1] != "" && Run.intentBundle.containsKey(gSS[0])) {
									Run.intentBundle.putBundle(gSS[0],data.getBundleExtra(s[1]));
									Run.intentBundle.putString("%succededResults%", Run.intentBundle.getString("%succededResults%") + gSS[0]+ ",");
								}
								//if(DebugSI)Run.PrintShow("%succededResults%:" + Run.intentBundle.getString("%succededResults%"));
							} else {
								// search variable
							}
						}

						if (g.toLowerCase().indexOf("getintextra(") == 0) {
							sA = r[2];
							if(DebugSI)Run.PrintShow("sA:" + sA);
							if (sA.replaceAll("[ ]", "").indexOf('"') == 0) {
								//getStringExtra("SCAN_RESULT");
								String s[] = (sA + c34 + "#").split(c34);
								if(DebugSI)Run.PrintShow("getIntExtra:" + s[1]);
								String vari = gSS[0];
								//	if(DebugSI)Run.PrintShow("r[3]:" + r[3]+","+s[1]+","+gSS[0]+","+Run.intentBundle.containsKey(gSS[0]));
								if (s[1] != "" && Run.intentBundle.containsKey(gSS[0])) {
									String StringConstant = r[3];
									int i1 = 0;
									try { i1 = Integer.parseInt(StringConstant);}			// have java parse it into a double
									catch (Exception e) {
									//	return Run.RunTimeError(e);
									}
									
									double w = data.getIntExtra(s[1],i1);
									Run.intentBundle.putDouble(gSS[0],w);
									Run.intentBundle.putString("%succededResults%", Run.intentBundle.getString("%succededResults%") + gSS[0]+ ",");
									if(DebugSI)Run.PrintShow("data.getIntExtra(s[1]):" + data.getIntExtra(s[1],i1));
								}
								if(DebugSI)Run.PrintShow("%succededResults%:" + Run.intentBundle.getString("%succededResults%"));
							} else {
								// search variable
							}
						}

						if (g.toLowerCase().indexOf("getdoubleextra(") == 0) {
							sA = r[2];
							if(DebugSI)Run.PrintShow("sA:" + sA);
							if (sA.replaceAll("[ ]", "").indexOf('"') == 0) {
								//getStringExtra("SCAN_RESULT");
								String s[] = (sA + c34 + "#").split(c34);
								if(DebugSI)Run.PrintShow("getDoubleExtra:" + s[1]);
								String vari = gSS[0];
								if (s[1] != "" && Run.intentBundle.containsKey(gSS[0])) {
									String StringConstant = r[3];
									double d1 = 0;
									try { d1 = Double.parseDouble(StringConstant);}			// have java parse it into a double
									catch (Exception e) {
									//	return Run.RunTimeError(e);
									}
									double w = data.getDoubleExtra(s[1],d1);
									Run.intentBundle.putDouble(gSS[0],w);
									Run.intentBundle.putString("%succededResults%", Run.intentBundle.getString("%succededResults%") + gSS[0]+ ",");
									if(DebugSI)Run.PrintShow("data.getIntExtra(s[1]):" + data.getDoubleExtra(s[1],d1));
								}
								if(DebugSI)Run.PrintShow("%succededResults%:" + Run.intentBundle.getString("%succededResults%"));
							} else {
								// search variable
							}
						}

						//"theFilePath$=getData().getPath();"
						// theFilePath = data.getData().getPath();
						if (g.toLowerCase().indexOf("getdata().getpath()") == 0) {
							if (DebugSI)Run.PrintShow("getdata().getpath():");
							if (gSS[0] != "" && Run.intentBundle.containsKey(gSS[0])) {
								Run.intentBundle.putString(gSS[0], data.getData().getPath());
								Run.intentBundle.putString("%succededResults%",Run.intentBundle.getString("%succededResults%")+ gSS[0] + ",");
							}
							if (DebugSI)Run.PrintShow("getData().getPath()(s[1]):" + data.getData().getPath());
							if (DebugSI)Run.PrintShow("%succededResults%:" + Run.intentBundle.getString("%succededResults%"));
						}
						if (("#"+g).toLowerCase().indexOf("#getdata()") == 0) {
							if (DebugSI)Run.PrintShow("#getdata():");
							if (gSS[0] != "" && Run.intentBundle.containsKey(gSS[0])) {
								Run.intentBundle.putString(gSS[0], data.getDataString()); //.getData().toString());
								Run.intentBundle.putString("%succededResults%",Run.intentBundle.getString("%succededResults%")+ gSS[0] + ",");
							}
						
						}
						if (DebugSI)Run.PrintShow("g:" + g);
						if (g.toLowerCase().indexOf(("getParcelableArrayListExtra(Intent.EXTRA_STREAM)").toLowerCase()) == 0) {
							if (data!=null && data.getExtras()!=null) {
								try { 
								ArrayList<Uri> theFileUriList = data.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
								String[] array = new String[theFileUriList.size()];
								if (DebugSI)Run.PrintShow("theFileUriList.size(): "+theFileUriList.size());
								ArrayList returnedList = new ArrayList();
								int i = -1;
								for (Uri tt : theFileUriList){
									 //array[i++] = tt.toString();
									 array[++i] = "" + tt;
									 returnedList.add("" + tt);
									if (DebugSI)Run.PrintShow(array[i]);
								}
								//if(gSS[0].indexOf("$[]") > -1){
								if(gSS[0].lastIndexOf("$[]") == gSS[0].length()-3){
									Run.intentBundle.putStringArray(gSS[0],array);
								}else	if(gSS[0].toLowerCase().indexOf("arrays#") == 0){
									Run.intentBundle.putStringArray(gSS[0],array);
								}else	if(gSS[0].toLowerCase().indexOf("stacks#") == 0){
									Run.intentBundle.putStringArray(gSS[0],array);
								}else{
									Run.intentBundle.putParcelableArrayList(gSS[0],returnedList);
								}
								Run.intentBundle.putString("%succededResults%",Run.intentBundle.getString("%succededResults%")+ gSS[0] + ",");
								if (DebugSI)Run.PrintShow("%succededResults%:" + Run.intentBundle.getString("%succededResults%") );
								Bundle a = Run.intentBundle.getBundle("\uffff");
								if (a.containsKey(gSS[0])){ //If gSS[0] is an array and not a List or Stack.
									a.putIntArray(gSS[0], new int[] {theFileUriList.size()});
									Run.intentBundle.putBundle("\uffff", a);
									if (DebugSI)Run.PrintShow("%succededResults% new int[] {i+1}:" + theFileUriList.size() );
								}
								}catch (Exception e) {
									//return RunTimeError(e);
									if (DebugSI)Run.PrintShow(""+e);
								}
							}
							if (DebugSI)Run.PrintShow("array[]:" );
						}

						
						
					}
				}
			} else {
				if (DebugSI)
					Run.PrintShow("RESULT Failed!");
			}
		}
		if (requestCode == REQUEST_CODE2) { //vv2017-07-13gt
			Run.PrintShow("Waited for result!"); //Test was successful!
		}//^^2017-07-13gt

		if (resultCode == RESULT_OK) {

			if (requestCode == 254) {
				// String fn = getTempFileName();
				if (data != null) {
					String returnedFilename = data.getDataString().replace(
							"content:", "");
					;
					if(DebugSI)Run.PrintShow("Camera returned path: " + returnedFilename);
				} else {

				}
			}
		}

		
		if (!dB) {
			Run.StartIntentDone = true;
			finish();
		}
	}


  public static String normalizeMimeType(String type) {
      if (type == null) {
          return null;
      }

      type = type.trim().toLowerCase(Locale.getDefault());

      final int semicolonIndex = type.indexOf(';');
      if (semicolonIndex != -1) {
          type = type.substring(0, semicolonIndex);
      }
      return type;
  }


	private void Toaster(CharSequence msg) {
		Context context = getApplicationContext();
		CharSequence text = msg;
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 50);
		toast.show();
	}

	
	private void PROC(int which) {
	}
	private static String quote(String str) {
		return '\"' + str + '\"';
	}

}
