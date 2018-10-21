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

import static com.rfo.basicTest.Run.EventHolder.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.graphics.RectF;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class GR extends Activity {
   private Camera mCamera = null;//vv2017-09-08gt
   public static int camPrev = -1; //No camera preview. Attention: Numbering in gr.camera.set begins with 1 instead of 0.
	public static String fileNameForSaving = "";//2017-12-14gt
	public static Parameters camParams = null;
   public static CameraPreview mPreview;//^^2017-09-08gt
   public static FrameLayout preview;
   private SurfaceHolder mHolder;
  // private Camera mCamera;

	
	private static final String LOGTAG = "GR";

	public static final String EXTRA_SHOW_STATUSBAR = "statusbar";
	public static final String EXTRA_ORIENTATION = "orientation";
	public static final String EXTRA_BACKGROUND_COLOR = "background";
	public static final String EXTRA_CAMERA_PREVIEW = "campreview";//2017-09-08gt

	public static Object LOCK = new Object();
	public static boolean waitForLock = false;

	public static Context context;
	public static DrawView drawView;
	public static Bitmap screenBitmap = null;
	private static int orientation = -1;//2017-12-14gt
	public static float scaleX = 1f;
	public static float scaleY = 1f;
	public static float transX = 0f;//vv2017-10-22gt
	public static float transY = 0f;
	public static float touchScaleX = 1f;
	public static float touchScaleY = 1f;
	public static float touchTransX = 0f;
	public static float touchTransY = 0f;//^^2017-10-22gt

	public static boolean Running = false;				// flag set when Open object runs
	private boolean mCreated = false;					// flat set when onCreate is complete
	public static boolean NullBitMap = false;
	public static float Brightness = -1;

	public static boolean doSTT = false;
	public static boolean doEnableBT = false;
	public static boolean startConnectBT = false;

	public enum VISIBLE { SHOW, HIDE, TOGGLE; }

	// ************* enumeration of BASIC! Drawable Object types **************

	public enum Type {
		Null("null",				new String[0]),
		Point("point",				new String[]
				{ "x", "y" } ),
		Line("line",				new String[]
				{ "x1", "y1", "x2", "y2" } ),
		/*
		Rect("rect",				new String[]
				{ "left", "right", "top", "bottom" } ),
		*/
		Rect("rect",				new String[]
				{ "left", "right", "top", "bottom", "rx", "ry" } ),//2016-10-27 gt
		Circle("circle",			new String[]
				{ "x", "y", "radius" } ),
		Oval("oval",				new String[]
				{ "left", "right", "top", "bottom" } ),
		Arc("arc",					new String[]
				{ "left", "right", "top", "bottom",
					"start_angle", "sweep_angle", "fill_mode" } ),
		Path("path",				new String[]
			//	{"x", "y", "listF" } ),//2016-10-27 gt
				{"x", "y", "list" } ),//2016-11-06 gt
		Poly("poly",				new String[]
				{"x", "y", "list" } ),
		Bitmap("bitmap",			new String[]
				{ "x", "y", "bitmap" } ),
		SetPixels("set.pixels",		new String[]
				{ "x", "y" } ),
		Text("text",				new String[]
				{ "x", "y", "text" } ),
		Group("group",				new String[]
				{ "list" } ),
		RotateStart("rotate.start",	new String[]
				{ "x", "y", "angle" } ),
		RotateEnd("rotate.end",		new String[0]),
		Clip("clip",				new String[]
				{ "left", "right", "top", "bottom", "RO" } ),
		Open("open",				new String[0]),
		Close("close",				new String[0]);

		private final String mType;
		private final String[] mParameters;		// all parameters except "paint" and "alpha"
		Type(String type, String[] parameters) {
			mType = type;
			mParameters = parameters;
		}

		public String type()			{ return mType; }
		public String[] parameters()	{ return mParameters; }

		public boolean hasParameter(String parameter) {
			for (String p : mParameters) {
				if (parameter.equals(p)) { return true; }
			}
			if (parameter.equals("paint")) { return true; }
			if (parameter.equals("alpha")) { return true; }
			return false;
		}
	}

	// ********************* BASIC! Drawable Object class *********************
	// Objects go on the Display List. Not related to Android's Drawable class.

	public static class BDraw {
		private final Type mType;
		private String mErrorMsg;

		private int mBitmap;							// index into the BitmapList
		private int mPaint;								// index into the PaintList
		private int mAlpha;
		private boolean mVisible;

		private String mText;							// for Type.Text
		private int mClipOpIndex;						// for getValue
		private Region.Op mClipOp;						// for Type.Clip
		private int mListIndex;
		private ArrayList<Double> mList;
		private ArrayList<Float> mListF;// 2016-10-31 gt
		private Var.ArrayDef mArray;					// for Type.SetPixels
		private int mArrayStart;						// position in array to start pixel array
		private int mArraySublength;					// length of array segment to use as pixel array
		private int mRadius;							// for Type.Circle
		private float mAngle_1;							// for Type.Rotate, Arc
		private float mAngle_2;							// for Type.Arc
		private int mFillMode;							// for getValue
		private boolean mUseCenter;						// for Type.Arc

		private int mLeft;								// left, x, or x1
		private int mRight;								// right or x2
		private int mTop;								// top, y, or y1
		private int mBottom;							// bottom or y2
		private int mRx;							// rx //2016-10-27 gt
		private int mRy;							// ry //2016-10-27 gt

		public BDraw(Type type) {
			mType = type;
			mVisible = true;
			mErrorMsg = "";
		}

		public void common(int paintIndex, int alpha) { mPaint = paintIndex; mAlpha = alpha; }

		// setters
		// For now, range checking for bitmap, paint, and list must be done in Run.
		public void bitmap(int bitmap) { mBitmap = bitmap; }	// index into the BitmapList
		public void paint(int paint) { mPaint = paint; }		// index into the PaintList
		public void alpha(int alpha) { mAlpha = alpha & 255; }

		public void xy(int[] xy) { mLeft = mRight = xy[0]; mTop = mBottom = xy[1]; }
		public void ltrb(int[] ltrb) { 
		try{
			mLeft = ltrb[0]; mTop = ltrb[1]; mRight = ltrb[2]; mBottom = ltrb[3]; 
			}catch (Exception e) {Run.PrintShow(e.toString());}//2016-10-27 gt
		}			
		public void rx(int rx) { mRx = rx; }
		
		
		public void radius(int radius) { mRadius = radius; }
		public void text(String text) { mText = text; }
		public void array(Var.ArrayDef array, int start, int sublength) {
			mArray = array;
			mArrayStart = start;
			mArraySublength = sublength;
		}
		public void angle(float angle) { mAngle_1 = angle; }

		public void show(VISIBLE show) {
			switch (show) {
				case SHOW: mVisible = true; break;
				case HIDE: mVisible = false; break;
				case TOGGLE: mVisible = !mVisible; break;
			}
		}

		public void clipOp(int opIndex) {
			Region.Op[] ops = {
				Region.Op.INTERSECT, Region.Op.DIFFERENCE, Region.Op.REPLACE,
				Region.Op.REVERSE_DIFFERENCE, Region.Op.UNION, Region.Op.XOR
			};
			mClipOpIndex = opIndex;
			mClipOp = ops[opIndex];
		}

		public void list(int index, ArrayList<Double> list) {
			mListIndex = index;
			mList = list;
		}

		public void listF(int index, ArrayList<Float> listF) {//vv 2016-10-31 gt
			mListIndex = index;
			mListF = listF;
		}//^^ 2016-10-31 gt

		public void useCenter(int fillMode) {
			mFillMode = fillMode;
			mUseCenter = (fillMode != 0);
		}

		public void arc(int[] ltrb, float startAngle, float sweepAngle, int fillMode) {
			ltrb(ltrb);
			mAngle_1 = startAngle;
			mAngle_2 = sweepAngle;
			useCenter(fillMode);
		}

		public void circle(int[] xy, int radius) {
			xy(xy);					// (x, y) in mTop and mLeft marks the CENTER of the circle
			mRadius = radius;
		}

		public void rect(int[] ltrb, int rx, int ry) {//vv 2016-10-27 gt
			ltrb(ltrb);
			mRx = rx;
			mRy = ry;
		}//^^ 2016-10-27 gt

		public void move(int[] dxdy) {
			int dx = dxdy[0];
			int dy = dxdy[1];
			mLeft += dx; mRight += dx;
			mTop += dy; mBottom += dy;
		}

		// universal getters
		public Type type()			{ return mType; }
		public String errorMsg()	{ return mErrorMsg; }
		public int bitmap()			{ return mBitmap; }	// index into the BitmapList
		public int paint()			{ return mPaint; }	// index into the PaintList
		public int alpha()			{ return mAlpha; }
		public boolean isHidden()	{ return !mVisible; }
		public boolean isVisible()	{ return mVisible; }

		// type-specific getters
		public String text()		{ return mText; }
		public Region.Op clipOp()	{ return mClipOp; }
		public ArrayList<Double> list() {
			if (mList == null) { mList = new ArrayList<Double>(); }
			return mList;
		}
		public ArrayList<Float> listF() {//vv 2016-10-31 gt
			if (mListF == null) { mListF = new ArrayList<Float>(); }
			return mListF;
		}//^^ 2016-10-31 gt
		public Var.ArrayDef array()	{ return mArray; }
		public int arrayStart()		{ return mArrayStart; }
		public int arraySublength()	{ return mArraySublength; }
		public int radius()			{ return mRadius; }
		public float angle()		{ return mAngle_1; }
		public float arcStart()		{ return mAngle_1; }
		public float arcSweep()		{ return mAngle_2; }
		public boolean useCenter()	{ return mUseCenter; }
		public int rx()			{ return mRx; }//2016-10-27 gt
		public int ry()			{ return mRy; }//2016-10-27 gt

		// coordinate getters
		public int x()				{ return mLeft; }
		public int x1()				{ return mLeft; }
		public int left()			{ return mLeft; }
		public int y()				{ return mTop; }
		public int y1()				{ return mTop; }
		public int top()			{ return mTop; }
		public int x2() 			{ return mRight; }
		public int right()			{ return mRight; }
		public int y2()				{ return mBottom; }
		public int bottom()			{ return mBottom; }

		// For GR.Get.Value
		public double getValue(String p) {
			if (p.equals("paint"))	{ return mPaint; }
			if (p.equals("alpha"))	{ return mAlpha; }

			switch (mType) {
				case Circle:	if (p.equals("radius")) { return mRadius; }
				case Point:
				case SetPixels:
				case Text:	// For now, "text" must be handled by Run
					if (p.equals("x"))				{ return mLeft; }
					if (p.equals("y"))				{ return mTop; }
					break;
				case Line:
					if (p.equals("x1"))				{ return mLeft; }
					if (p.equals("y1"))				{ return mTop; }
					if (p.equals("x2"))				{ return mRight; }
					if (p.equals("y2"))				{ return mBottom; }
					break;
				case Clip:		if (p.equals("RO")) { return mClipOpIndex; }
				case Oval:
				case Rect:
					if (p.equals("left"))			{ return mLeft; }
					if (p.equals("top"))			{ return mTop; }
					if (p.equals("right"))			{ return mRight; }
					if (p.equals("bottom"))			{ return mBottom; }
					if (p.equals("rx"))			{ return mRx; }//2016-10-27 gt
					if (p.equals("ry"))			{ return mRy; }//2016-10-27 gt
					break;
				case Arc:
					if (p.equals("start_angle"))	{ return mAngle_1; }
					if (p.equals("sweep_angle"))	{ return mAngle_2; }
					if (p.equals("fill_mode"))		{ return mFillMode; }
					if (p.equals("left"))			{ return mLeft; }
					if (p.equals("top"))			{ return mTop; }
					if (p.equals("right"))			{ return mRight; }
					if (p.equals("bottom"))			{ return mBottom; }
					break;
				case Path:
					/*
					if (p.equals("left"))			{ return mLeft; }//vv 2016-11-06 gt
					if (p.equals("top"))				{ return mTop; }
					if (p.equals("right"))			{ return mRight; }
					if (p.equals("bottom"))			{ return mBottom; }//^^ 2016-11-06 gt
					*/
					if (p.equals("x"))				{ return mLeft; }//vv 2016-11-06 gt
					if (p.equals("y"))				{ return mTop; }//^^ 2016-11-06 gt
				case Poly:
					/*
					if (p.equals("left"))			{ return mLeft; }//vv 2016-11-06 gt
					if (p.equals("top"))				{ return mTop; }
					if (p.equals("right"))			{ return mRight; }
					if (p.equals("bottom"))			{ return mBottom; }//^^ 2016-11-06 gt
					*/
					if (p.equals("x"))				{ return mLeft; }
					if (p.equals("y"))				{ return mTop; }
				case Group:
					if (p.equals("list"))			{ return mListIndex; }
					break;
				case Bitmap:
					if (p.equals("bitmap"))			{ return mBitmap; }
					if (p.equals("x"))				{ return mLeft; }
					if (p.equals("y"))				{ return mTop; }
					break;
				case RotateStart:
					if (p.equals("angle"))			{ return mAngle_1; }
					if (p.equals("x"))				{ return mLeft; }
					if (p.equals("y"))				{ return mTop; }
					break;
				case Close:
				case Null:
				case RotateEnd:
				default:							break;
			}
			return 0.0;
		} // getValue(String)

		// For GR.Modify
		private boolean mod_xy(String p, int val) {
			if (p.equals("x"))		{ mLeft = mRight  = val; return true; }
			if (p.equals("y"))		{ mTop  = mBottom = val; return true; }
			return false;
		}
		private boolean mod_x2y2(String p, int val) {
			if (p.equals("x1"))		{ mLeft   = val; return true; }
			if (p.equals("y1"))		{ mTop    = val; return true; }
			if (p.equals("x2"))		{ mRight  = val; return true; }
			if (p.equals("y2"))		{ mBottom = val; return true; }
			return false;
		}
		private boolean mod_ltrb(String p, int val) {
			if (p.equals("left"))	{ mLeft   = val; return true; }
			if (p.equals("top"))	{ mTop    = val; return true; }
			if (p.equals("right"))	{ mRight  = val; return true; }
			if (p.equals("bottom"))	{ mBottom = val; return true; }
			if (p.equals("rx"))	{ mRx = val; return true; }
			if (p.equals("ry"))	{ mRy = val; return true; }
			return false;
		}
		public boolean modify(String p, int iVal, float fVal, String text) {
			// For now, "paint" is handled in Run because of range check.
			// if (p.equals("paint"))	{ mPaint = iVal; return true; }
			if (p.equals("alpha"))	{ alpha(iVal); return true; }
			switch (mType) {
				case Circle:	if (p.equals("radius"))	{ mRadius = iVal; return true; }
				case Bitmap:	// for now, BitmapList range check must be handled in Run
				case Path:		// for now, list parm must be handled in Run
				case Point:
				case Poly:		// for now, list parm must be handled in Run
				case SetPixels:	if (mod_xy(p, iVal))	{ return true; } else { break; }

				case Line:		if (mod_x2y2(p, iVal))	{ return true; } else { break; }

				case Clip:		if (p.equals("RO"))		{ clipOp(iVal); return true; }
				case Oval:
				case Rect:		if (mod_ltrb(p, iVal))	{ return true; } else { break; }

				case Arc:
					if (p.equals("start_angle"))	{ mAngle_1 = fVal; return true; }
					if (p.equals("sweep_angle"))	{ mAngle_2 = fVal; return true; }
					if (p.equals("fill_mode"))		{ useCenter(iVal); return true; }
					if (mod_ltrb(p, iVal))			{ return true; }
					break;

				case RotateStart:
					if (p.equals("angle"))			{ mAngle_1 = fVal; return true; }
					if (mod_xy(p, iVal))			{ return true; }
					break;

				case Text:
					if (p.equals("text"))			{ mText = text; return true; }
					if (mod_xy(p, iVal))			{ return true; }
					break;

				case Close:
				case Group:		// for now, list parm must be handled in Run
				case Null:
				case RotateEnd:
				default:							break;
			}
			mErrorMsg = "Object does not contain: " + p;
			return false;
		} // modify(String, int, float, String)

	} // BDraw class
	/** A safe way to get an instance of the Camera object. */
	@SuppressLint("NewApi")//Because Camera.open
	public static Camera getCameraInstance(){//vv2017-09-08gt
	    Camera c = null;
	    try {
				//Attention: Numbering in gr.camera.set begins with 1 instead of 0.
				// That is corrected at camPrev = intent.getIntExtra(EXTRA_CAMERA_PREVIEW, 0)-1;
					if (camPrev > 0 && Build.VERSION.SDK_INT > 8 ){
						if (camPrev > Camera.getNumberOfCameras()-1)camPrev = Camera.getNumberOfCameras()-1;
						c = Camera.open(camPrev); 
					}else{
						c = Camera.open(); // attempt to get a Camera instance
					}
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	      	Run.PrintShow("Camera is not available (in use or does not exist: " + e.getMessage());
	    }
	    return c; // returns null if camera is unavailable
	}
	/** A basic Camera preview class */  
	public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	//public class CameraPreview extends View implements SurfaceHolder.Callback {
		/*
	    private SurfaceHolder mHolder;
	    private Camera mCamera;
*/
	    public CameraPreview(Context context, Camera camera) {
	        super(context);
				try {
	        mCamera = camera;

	        // Install a SurfaceHolder.Callback so we get notified when the
	        // underlying surface is created and destroyed.
	        mHolder = getHolder();
	        mHolder.addCallback(this);
	        // deprecated setting, but required on Android versions prior to 3.0
	        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	        /*
				Canvas canvas = mHolder.lockCanvas();
				camera.setOneShotPreviewCallback(mPreview);
	        */
				
				} catch (Exception e) {
					// Log.d(TAG, "Error setting camera preview: " + e.getMessage());
					Run.PrintShow("Error at CameraPreview setting camera preview: "
							+ e.getMessage());
				}
	    }

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// The Surface has been created, now tell the camera where to draw the preview.
				try {
						mCamera.setPreviewDisplay(holder);
						Parameters params = mCamera.getParameters();
						params.setFocusMode(Parameters.FOCUS_MODE_AUTO);
						params.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
						mCamera.setParameters(params);
/*
						SurfaceTexture st = new SurfaceTexture(MODE_PRIVATE);  //For Android 6
						//st.attachToGLContext(texName);
						mCamera.setPreviewTexture(st);
						*/
						mCamera.startPreview();
				//	}
				} catch (IOException e) {
					// Log.d(TAG, "Error setting camera preview: " + e.getMessage());
					Run.PrintShow("Error at surfaceCreated setting camera preview: "
							+ e.getMessage());
				}
		}

	    @Override
		public void surfaceDestroyed(SurfaceHolder holder) {
	   	 /*
	 		if (mAutoFocus) {
				if (mCamera != null) {mCamera.cancelAutoFocus(); }
				mAutoFocus = false;
			}
	 		*/
	 		/*
			if (mCamera != null) {
				mCamera.release();
				mCamera = null;
			}
			*/
	 		if (camPrev > -1 && mCamera != null) {//vv2017-09-08gt
				mCamera.stopPreview();
		 		mCamera.cancelAutoFocus();
				mCamera.release();
				mCamera = null;
			}//^^2017-09-08gt

	        // empty. Take care of releasing the Camera preview in your activity.
	    }

	    @Override
		public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
	        // If your preview can change or rotate, take care of those events here.
	        // Make sure to stop the preview before resizing or reformatting it.

	        if (mHolder.getSurface() == null){
	          // preview surface does not exist
	          return;
	        }

	        // stop preview before making changes
	        try {
	            mCamera.stopPreview();
	        } catch (Exception e){
	          // ignore: tried to stop a non-existent preview
		      	Run.PrintShow("Error ignore: tried to stop a non-existent previe: " + e.getMessage());
	        }

	        // set preview size and make any resize, rotate or
	        // reformatting changes here

	        // start preview with new settings
	        try {
	            mCamera.setPreviewDisplay(mHolder);
	            /*
					SurfaceTexture st = new SurfaceTexture(MODE_PRIVATE); //For Android 6
					mCamera.setPreviewTexture(st);
	   			drawView.setAlpha(0f);
*/
	            mCamera.startPreview();

	        } catch (Exception e){
	           // Log.d(TAG, "Error starting camera preview: " + e.getMessage());
		      	Run.PrintShow("Error at surfaceChanged setting camera preview: " + e.getMessage());
	        }
		}

			public double doZoom(double zoom) {
				try {
					//Run.PrintShow("Zoom: " + zoom);
					Parameters params = mCamera.getParameters();
					if (!params.isZoomSupported()) return -1;
					//List<Integer> s = params.getZoomRatios();
					double zoomF = params.getZoomRatios().get(params.getMaxZoom())/100;
					int zF =  (int)((params.getMaxZoom() / zoomF * zoom) + 0.5d);
					if (zF > params.getMaxZoom()){
						zF = params.getMaxZoom();
						zoom = zoomF;
					}
					//Run.PrintShow("Zoom: " + zF + " # " + params.getMaxZoom()+ " # " + zoomF);
					params.setZoom(zF);
					mCamera.setParameters(params);
					//SurfaceTexture st = new SurfaceTexture(MODE_PRIVATE);  //For Android 6
					//mCamera.setPreviewTexture(st);
					camParams = params;
					mCamera.startPreview();
				} catch (Exception e) {
		      	Run.PrintShow("Error at Zoom: " + e.getMessage());
					return -1; //RunTimeError(e);
				}
				return zoom;
			} // doZoom()

			public double doFlash(int flash) {
				try {
					Parameters params = mCamera.getParameters();
					if (flash == 0) {
						params.setFlashMode(Parameters.FLASH_MODE_AUTO);
					} else if (flash == 1){
						params.setFlashMode(Parameters.FLASH_MODE_ON);
					} else if (flash == 3){
						params.setFlashMode(Parameters.FLASH_MODE_TORCH);
					} else if (flash == 4){
						params.setFlashMode(Parameters.FLASH_MODE_RED_EYE);
					} else {
						params.setFlashMode(Parameters.FLASH_MODE_OFF);
					}
					//     	   mCamera.stopPreview(); //Without faster
					mCamera.setParameters(params);
					//SurfaceTexture st = new SurfaceTexture(MODE_PRIVATE);  //For Android 6
					//mCamera.setPreviewTexture(st);
					camParams = params;
					mCamera.startPreview();
				} catch (Exception e) {
					Run.PrintShow("Error at Flash: " + e.getMessage());
					return -1; //RunTimeError(e);
				}
				return flash;
			} // doFlash()

			public double doFocus(int mode) {
				try {
					//Run.PrintShow("Zoom: " + zoom);
					Parameters params = mCamera.getParameters();
					if (mode == 0) {
						params.setFocusMode(Parameters.FOCUS_MODE_AUTO);
					} else if (mode == 1){
						params.setFocusMode(Parameters.FOCUS_MODE_FIXED);
					} else if (mode == 2){
						params.setFocusMode(Parameters.FOCUS_MODE_INFINITY);
					} else if (mode == 3){
						params.setFocusMode(Parameters.FOCUS_MODE_MACRO);
					} else if (mode == 4){
						params.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
					} else if (mode == 5){
						params.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
					} else {
					}
					//     	   mCamera.stopPreview(); //Without faster
					mCamera.setParameters(params);
					camParams = params;
					//SurfaceTexture st = new SurfaceTexture(MODE_PRIVATE);  //For Android 6
					//mCamera.setPreviewTexture(st);
					//mCamera.startPreview();
				} catch (Exception e) {
					Run.PrintShow("Error at Focus Mode: " + e.getMessage());
					return -1; //RunTimeError(e);
				}
				return mode;
			} // doFlash()

			public String doGetParam() {
				String CameraParameters = "";
				try {
					Parameters params = mCamera.getParameters();
					camParams = params;
					CameraParameters = params.flatten();//Parameters/2017-12-14gt
					//Run.PrintShow(" at GetParam: " + CameraParameters);
						String array[] = CameraParameters.split("[;]");
						CameraParameters = ";";
						Arrays.sort(array);
						for (int i = 0; i < array.length; i++) {
							CameraParameters += array[i] + ";";
						}
						// For better searching  ;<key>=<value>;
						// chop off the extra semicolon at the end
						CameraParameters = CameraParameters.replace(";;", ";");
				} catch (Exception e) {
		      	//Run.PrintShow("Error at GetParam: " + e.getMessage());
					return "Error at GetParam: \n" + e.getMessage(); //RunTimeError(e);
				}
				return CameraParameters;
			} // doGetParam()
			
		public String doSetParams() {
			try {
				Parameters params = mCamera.getParameters();
				// NewParameters/26-09-2013 gt
				// Run.CameraNewParameters = ";zoom=4;preview-size=176x144;picture-size=1600x1200;focal-length=0.0;";
				String exeptions = ";focal-length=3.43;horizontal-view-angle=51.2;vertical-view-angle=39.4;";
				String array[] = Run.CameraNewParameters.split("[;]");
				for (int i = 0; i < array.length; i++) {
					if (array[i].indexOf("supported") < 0) { //Skip if options were obviously sent.
						String options[] = array[i].split("[=]");
						/*
						if (Run.CameraParameters.indexOf(";" + options[0] + "=") >= 0
								&& exeptions.indexOf(";" + options[0] + "=") < 0) {
							params.set(options[0], options[1]);
						}
						*/
						if (params.flatten().indexOf(";" + options[0] + "=") >= 0
								&& exeptions.indexOf(";" + options[0] + "=") < 0) {
							params.set(options[0], options[1]);
						}
					}
				}
				mCamera.setParameters(params);
				//SurfaceTexture st = new SurfaceTexture(MODE_PRIVATE);  //For Android 6
				//mCamera.setPreviewTexture(st);
				camParams = params;
				mCamera.startPreview();
			} catch (Exception e) {
				return "Error at Setting Camera Parameters: \n" + e.getMessage(); //RunTimeError(e);
			}
			return "";
		} // doSetParams()
		public String doDirectShoot(String cameraFileName, String cameraSize) {
			fileNameForSaving = cameraFileName;
				Parameters params = mCamera.getParameters();
				if (cameraSize.toLowerCase().indexOf("_max") == 0){
				// Use the largest picture size
				List<Camera.Size> sizes = params.getSupportedPictureSizes();
				Camera.Size cs = sizes.get(0);
				for (Camera.Size ps : sizes) {	//2013-07-27-01-15 gt
					if (cs.width < ps.width) {
						cs = ps;
					}
				}
				params.setPictureSize(cs.width, cs.height);
				}else if (cameraSize.toLowerCase().indexOf("_screen") == 0){
					DisplayMetrics dm = new DisplayMetrics();
					getWindowManager().getDefaultDisplay().getMetrics(dm);
					int wD = Math.max(dm.widthPixels,dm.heightPixels);
					int hD = Math.min(dm.widthPixels,dm.heightPixels);
					List<Camera.Size> sizes = params.getSupportedPictureSizes();
					Camera.Size cs = sizes.get(0);
					boolean match = false;
					for (Camera.Size ps : sizes) {	
						if (ps.height == hD) {
							cs = ps;
							match = true;
						}
					}
					
					if (!match){
					}
					params.setPictureSize(cs.width, cs.height);
				}else if (cameraSize.toLowerCase().indexOf("_none") < 0){
					params.set("picture-size", cameraSize.toLowerCase());
				}
				camParams = params;
				if (cameraSize.toLowerCase().indexOf("_none") < 0)params.setRotation(orientation*90);
				mCamera.setParameters(params);
			try {
				mCamera.takePicture(null, null, null, picCallback); //changed to picCallback /2013-09-26
				while (!Run.CameraDone);
			//	if (Build.VERSION.SDK_INT < 24) {//For Android < 7
	            mCamera.stopPreview();
	            mCamera.startPreview();
			//	}

			} catch (Exception e) {
				Run.PrintShow("CameraDirectShoot: \n" + e.getMessage());
				Run.CameraDone = true;
				return "Error at CameraDirectShoot: \n" + e.getMessage(); //RunTimeError(e);
				//return "";
			}
			return "";
		} // doDirectShoot()
		
		Camera.PictureCallback picCallback = new Camera.PictureCallback() {
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
	      try {
				if (getPictureFormat().equals("jpeg")) {
					savePicture("jpg", data);
				} else {
					savePicture("png", data);
				}
				Run.CameraDone = true;
				
				
			} catch (Exception e) {
				Run.PrintShow("Error at onPictureTaken(): " + e.getMessage() );
			}

			}
		};


			
			
	}//class CameraPreview//^^2017-09-08gt
/*	
	Camera.PictureCallback picCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
      try {
			if (getPictureFormat().equals("jpeg")) {
				savePicture("jpg", data);
			} else {
				savePicture("png", data);
			}
			Run.CameraDone = true;
			
			
		} catch (Exception e) {
			Run.PrintShow("Error at onPictureTaken(): " + e.getMessage() );
		}

		}
	};
*/
	private String getFileName(String ext){		// ext is "png" or "jpg"
		if (fileNameForSaving == ""){//Temp File Name
			return Basic.getDataPath("image." + ext);
		}else{
			int w = fileNameForSaving.lastIndexOf(".");
			fileNameForSaving = fileNameForSaving.substring(0, w);
			return Basic.getDataPath(fileNameForSaving + "." + ext);
		}
	}

	private String getTempFileName(String ext){		// ext is "png" or "jpg"
		return Basic.getDataPath("image." + ext);
	}
	/*
	private void savePicture(String ext, byte[] data) {
		try {
		FileOutputStream outStream = null;
		String fileName = getFileName(ext);
			outStream = new FileOutputStream(fileName);
			outStream.write(data);
			outStream.close();
//			Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
		System.gc();
		BitmapFactory.Options BFO = new BitmapFactory.Options();
		BFO.inSampleSize = 4;
		//Run.CameraBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);//.decodeFile(fileName,BFO);           // Make the bit map from the file
		Run.CameraBitmap = BitmapFactory.decodeFile(fileName,BFO);           // Make the bit map from the file
		} catch (Exception e) {
			Run.PrintShow("Error BFO: \n" + e.getMessage());
		}
		// if (Run.CameraManual) mCamera.startPreview();
		//Run.CameraDone = true;
		//finish();
	}
	*/
	private void savePicture(String ext, byte[] data) {//2017-12-14gt
		try {
			Run.CameraBitmap = null; // To get no memory fault
			System.gc();// To get no memory fault
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			Camera.Parameters p = mCamera.getParameters();
			Camera.Size result = p.getPictureSize();
			int wD = Math.max(dm.widthPixels,dm.heightPixels);
			int hD = Math.min(dm.widthPixels,dm.heightPixels);
			double wP = Math.max(result.width,result.height); //Has to be double, so no later
			double hP = Math.min(result.width,result.height); //calculation result is integer
	      double iSS = Math.max(Math.min(wP/wD,hP/hD),1);
			BitmapFactory.Options BFO = new BitmapFactory.Options();
			BFO.inSampleSize = (int)fix(iSS); //Changed to 1, because right size. /2013-08-19 gt
			Run.CameraBitmap = BitmapFactory.decodeByteArray(data,  0,data.length, BFO);
			/**/
			if (iSS > 1){ //&& toMaxScreenSizeBorders = true
				double bw = Math.max(Run.CameraBitmap.getWidth(),Run.CameraBitmap.getHeight());                 // Get the image width
				double bh = Math.min(Run.CameraBitmap.getWidth(),Run.CameraBitmap.getHeight());                // Get the image height
				double ar = Math.max( wD/bw, hD/bh); //2017-12-21gt
				//Run.PrintShow("ar: "+ar+" "+wD+" "+bw+" "+ hD+" "+bh);
				Run.CameraBitmap = Bitmap.createScaledBitmap(Run.CameraBitmap, (int) fix(bw*ar), (int) fix(bh*ar), true);
			}
			String fn = getFileName(ext);
			FileOutputStream fos2 = new FileOutputStream(fn);
			Bitmap mBitmap = null ;
			BFO.inSampleSize = 1;
			mBitmap = BitmapFactory.decodeByteArray(data,  0,data.length, BFO);
			if (ext.equals("png")){
				mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos2);
				// Run.PrintShow("CompressFormat.PNG: ");
			}
			if ((!getPictureFormat().equals("jpeg")) && ext.equals("jpg")){
				mBitmap.compress(Bitmap.CompressFormat.JPEG, getJpegQuality(), fos2);
				// Run.PrintShow("CompressFormat.JPEG: "+ getJpegQuality());
			}
			fos2.write(data); //Write full size. / 2013-08-19 gt
			fos2.close();
			// Run.PrintShow("Photo Path: "+ fn+" "+ext+" "+getPictureFormat());
			/*
			ExifInterface exif = new ExifInterface(file.getAbsolutePath());
			String exifOrientation = exif.setAttribute(ExifInterface.TAG_ORIENTATION);	
			*/
		} catch (Exception e) {
			Run.PrintShow("Error in savePicture(): \n" + e);
		}
		Run.CameraDone = true;
	}	

	private String getPictureFormat() { //2017-12-14gt
		String CameraParameters = mCamera.getParameters().flatten();//Parameters/2017-12-14gt
		int start = CameraParameters.indexOf("picture-format=");
		if (start>=0){
			String extr = CameraParameters.substring(start, start+40);
			String[] mem = extr.split("[=;]");
			return mem[1];
		}else{
			return "";
		}
	}
	private int getJpegQuality() { //2017-12-14gt
		String CameraParameters = mCamera.getParameters().flatten();//Parameters/2017-12-14gt
		int start = CameraParameters.indexOf("jpeg-quality=");
		if (start>=0){
			String extr = CameraParameters.substring(start, start+40);
			String[] mem = extr.split("[=;]");
			return Integer.parseInt(mem[1]);
		}else{
			return 90;
		}
	}
	private double fix(double d) { //2013-08-08 gt
		if (d >= 0)	return Math.floor(d);
		return Math.ceil(d);
	}
	private double frac(double d) { //2013-08-08 gt 
		if (d >= 0)	return d - Math.floor(d);
		return d - Math.ceil(d);
	}

		
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {//vv2017-09-08gt
			Log.v(LOGTAG, "onCreate");
			super.onCreate(savedInstanceState);
			ContextManager cm = Basic.getContextManager();
			cm.registerContext(ContextManager.ACTIVITY_GR, this);
			cm.setCurrent(ContextManager.ACTIVITY_GR);

			Intent intent = getIntent();
			int showStatusBar = intent.getIntExtra(EXTRA_SHOW_STATUSBAR, 0);
			orientation = intent.getIntExtra(EXTRA_ORIENTATION, -1);
			int backgroundColor = intent.getIntExtra(EXTRA_BACKGROUND_COLOR,
					0xFF000000);
			camPrev = intent.getIntExtra(EXTRA_CAMERA_PREVIEW, 0) - 1;//-1 because numbering in gr.camera.set //2017-09-08gt 
			setOrientation(orientation);
			setVolumeControlStream(AudioManager.STREAM_MUSIC);

			showStatusBar = (showStatusBar == 0)
					? WindowManager.LayoutParams.FLAG_FULLSCREEN // do not show status bar
					: WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN; // show status bar
			getWindow().setFlags(showStatusBar, showStatusBar);
			requestWindowFeature(Window.FEATURE_NO_TITLE);

			scaleX = 1.0f;
			scaleY = 1.0f;
			transX = 0.0f;//vv2017-10-22gt
			transY = 0.0f;
			touchScaleX = 1.0f;
			touchScaleY = 1.0f;
			touchTransX = 0.0f;
			touchTransY = 0.0f;//^^2017-10-22gt
			Brightness = -1;

			drawView = new DrawView(this);
			//setContentView(drawView);//2017-09-08gt
			drawView.setBackgroundColor(backgroundColor);
			drawView.setId(33);

			if (camPrev > -1) {
				/*
				  // Create an instance of Camera
				  mCamera = getCameraInstance();
				  mCamera.setDisplayOrientation(orientation * 90);
				  // Create our Preview view and set it as the content of our activity.
				  mPreview = new CameraPreview(this, mCamera);
				  FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
				  preview.addView(mPreview);
				  
				  setContentView(preview, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				  addContentView(drawView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				 
				 */
				  

				setContentView(R.layout.gr);//gr
				// Create an instance of Camera
				mCamera = getCameraInstance();
				mCamera.setDisplayOrientation(orientation * 90);
				// Create our Preview view and set it as the content of our activity.
		     //   mPreview = new CameraPreview(cm, mCamera,view);
				mPreview = new CameraPreview(this, mCamera);
				//FrameLayout preview = (FrameLayout) findViewById(
				preview = (FrameLayout) findViewById(
						R.id.camera_preview);
				preview.addView(mPreview);
				//mPreview.startCameraPreview();
				addContentView(drawView, new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
			} else {
				setContentView(drawView);
			} //^^2017-09-08gt
			drawView.requestFocus();
			synchronized (drawView) {
				mCreated = true;
				condReleaseLOCK();
			}
			//drawView.postInvalidate(); //2018-03-17gt //Make sure, that GR screen is displayed. RSA
			drawView.refreshDrawableState(); //2018-03-17gt //Make sure, that GR screen is displayed. RSA
		} catch (Exception e) {
			// Log.d(TAG, "Error starting camera preview: " + e.getMessage());
			Run.PrintShow("Error at camera preview: " + e.getMessage());
		}

	}
	
	
private View View(GR gr) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	protected void onStart() {
		super.onStart();
		Log.v(LOGTAG, "onStart");
	}

	@Override
	protected void onResume() {//At start from GR
		Log.v(LOGTAG, "onResume " + this.toString());
		if (context != this) {
			Log.d(LOGTAG, "Context changed from " + context + " to " + this);
			context = this;
		}
		Basic.getContextManager().onResume(ContextManager.ACTIVITY_GR);
		Run.mEventList.add(new Run.EventHolder(GR_STATE, ON_RESUME, null));
   	  /*
     try {
      mCamera.stopPreview();
 			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();//2017-12-14gt
     //if (mCamera != null)finish();
		} catch (Exception e) {
			Run.PrintShow("Error at onRestart(): " + e.getMessage() );
		}
   //    mCamera.startPreview();
			*/

		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.v(LOGTAG, "onPause " + this.toString());
		Basic.getContextManager().onPause(ContextManager.ACTIVITY_GR);
		Run.mEventList.add(new Run.EventHolder(GR_STATE, ON_PAUSE, null));
		if (drawView.mKB != null) { drawView.mKB.forceHide(); }
		//if (mCamera != null)	mCamera.stopPreview();//2017-12-14gt
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.v(LOGTAG, "onStop");
		//if (mCamera != null)	mCamera.stopPreview();//2017-12-14gt
		super.onStop();
	}

	@Override
	protected void onRestart() {
		/*
      try {
      mCamera.stopPreview();
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();//2017-12-14gt
	     if (mCamera != null)finish();

		} catch (Exception e) {
			Run.PrintShow("Error at onRestart(): " + e.getMessage() );
		}
		*/	

		super.onRestart();
		Log.v(LOGTAG, "onRestart");
	}

	@Override
	public void finish() {
		// Tell the ContextManager we're done, if it doesn't already know.
		Basic.getContextManager().unregisterContext(ContextManager.ACTIVITY_GR, this);
		super.finish();
	}

	@Override
	protected void onDestroy() {
		Log.v(LOGTAG, "onDestroy " + this.toString());
		// if a new instance has started, don't let this one mess it up
		if (context == this) {
			Running = mCreated = false;
			context = null;
			releaseLOCK();								// don't leave GR.command hanging
		}
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		Run.mEventList.add(new Run.EventHolder(GR_BACK_KEY_PRESSED, 0, null));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Log.v(LOGTAG, "keyDown " + keyCode);
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return super.onKeyDown(keyCode, event);
		}
		
	  	if (!Run.mBlockVolKeys && ( //Rerouting -- Do not put this KeyEvents on the EventList.
				(keyCode == KeyEvent.KEYCODE_VOLUME_UP)   ||
				(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) ||
				(keyCode == KeyEvent.KEYCODE_VOLUME_MUTE) ||
				(keyCode == KeyEvent.KEYCODE_MUTE)        ||
				(keyCode == KeyEvent.KEYCODE_HEADSETHOOK) ))
		{
	  		Run.mEventList.add(new Run.EventHolder(KEY_DOWN, keyCode, event));// 2017-11-04gt
			return super.onKeyDown(keyCode, event);
		}
	  	
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			// Do not put the KeyEvent on the EventList. This keeps Run.onKeyDown() from building a menu.
			//Run.mEventList.add(new Run.EventHolder(KEY_DOWN, keyCode, null));//2017-02-20gt
			event = null;//2017-02-20gt
		}
		Run.mEventList.add(new Run.EventHolder(KEY_DOWN, keyCode, event));//2017-02-20gt
		return true;									// ignore anything else
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)  {						// The user hit a key
		// Log.v(LOGTAG, "keyUp " + keyCode);
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return super.onKeyUp(keyCode, event);
		}
		/* Trouble with MenuKey Android 4.12
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			// Do not put the KeyEvent on the EventList. This keeps Run.onKeyDown() from building a menu.
			event = null;
		}
		*/
		if (keyCode == KeyEvent.KEYCODE_MENU)return false; //2018-02-14gt Run.java is too fast.
		Run.mEventList.add(new Run.EventHolder(KEY_UP, keyCode, event));
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
        case Run.REQUEST_CONNECT_DEVICE_SECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, true);
            }
            break;
        case Run.REQUEST_CONNECT_DEVICE_INSECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, Run.bt_Secure);
            }
            break;
        case Run.REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
            	Run.bt_enabled = 1;
            } else {
                Run.bt_enabled = -1;
            }
            break;

		case Run.VOICE_RECOGNITION_REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				Run.sttResults = new ArrayList<String>();
				Run.sttResults = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			}
			Run.sttDone = true;
		}
	}

	private void condReleaseLOCK() {					// conditionally release LOCK
		if (mCreated & Running) { releaseLOCK(); }
	}

	private void releaseLOCK() {						// unconditionally release LOCK
		if (waitForLock) {
			synchronized (LOCK) {
				waitForLock = false;
				LOCK.notify();							// release GR.OPEN or .CLOSE if it is waiting
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private void setOrientation(int orientation) {		// Convert and apply orientation setting
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

    public void connectDevice(Intent data, boolean secure) {

        String address = data.getExtras()
            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        Run.btConnectDevice = null;
        try {
        	Run.btConnectDevice = Run.mBluetoothAdapter.getRemoteDevice(address);
	        if ( Run.btConnectDevice != null) Run.mChatService.connect(Run.btConnectDevice, secure);
        }
        catch (Exception e){ 
//        	RunTimeError("Connect error: " + e);
        }
        // Attempt to connect to the device
    }

    public void startsBTConnect() {
    	Intent serverIntent = null;
        serverIntent = new Intent(this, DeviceListActivity.class);
        if (serverIntent != null){
        	if (Run.bt_Secure) {
        		startActivityForResult(serverIntent, Run.REQUEST_CONNECT_DEVICE_SECURE);
        	}else {
        		startActivityForResult(serverIntent, Run.REQUEST_CONNECT_DEVICE_INSECURE);
        	}
        }

    }

    public void enableBT() {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, Run.REQUEST_ENABLE_BT);
    }

	public class DrawView extends View {
		private static final String LOGTAG = "GR.DrawView";

		public KeyboardManager mKB;

		@SuppressLint("NewApi")
		public DrawView(Context context) {
			super(context);
			super.
			setFocusable(true);
			setFocusableInTouchMode(true);
			mKB = new KeyboardManager(context, this, new KeyboardManager.KeyboardChangeListener() {
				@Override
				public void kbChanged() {
					Run.mEventList.add(new Run.EventHolder(GR_KB_CHANGED, 0, null));
				}
			});

			if (Build.VERSION.SDK_INT >= 11) {				// Hardware acceleration is supported starting API 11
				// Assume hardware acceleration is enabled for the app.
				// Choose whether to use it in DrawView based on user Preference.
				int layerType = Settings.getGraphicAcceleration(context)
								? View.LAYER_TYPE_HARDWARE	// use hardware acceleration
								: View.LAYER_TYPE_SOFTWARE;	// disable hardware acceleration
				setLayerType(layerType, null);
			}
		}

		synchronized public void setOrientation(int orientation) {	// synchronized orientation change
			Log.v(LOGTAG, "Set orientation " + orientation);
			GR.this.setOrientation(orientation);
		}

		@Override
		public boolean onKeyPreIme(int keyCode, KeyEvent event) {
			return (mKB != null) && mKB.onKeyPreIme(keyCode, event); // delegate to KeyboardManager
		}

		@SuppressWarnings("deprecation")
		@SuppressLint("NewApi")
		public int getWindowMetrics(Point outSize) {	// return size in Point, density as return value
			// This can be called when the DrawView does not yet know what size it is,
			// so get the size from the WindowManager.
			Display display = getWindowManager().getDefaultDisplay();
			if (Build.VERSION.SDK_INT < 13) {
				outSize.set(display.getWidth(), display.getHeight());
			} else {
				display.getSize(outSize);
			}
			DisplayMetrics dm = new DisplayMetrics();
			display.getMetrics(dm);
			return dm.densityDpi;
		}

		/*
		public boolean onTouchEvent(MotionEvent event) {
			super.onTouchEvent(event);
			int action = event.getAction() & MotionEvent.ACTION_MASK;	// Get action type, mask off index field
			int numPointers = event.getPointerCount();

			for (int i = 0; i < numPointers; i++) {
				int pid = event.getPointerId(i);
				if (pid > 1)  { continue; }				// currently, we allow only two pointers

				Run.TouchX[pid] = (double)event.getX(i);
				Run.TouchY[pid] = (double)event.getY(i);
				if (action == MotionEvent.ACTION_DOWN ||
					action == MotionEvent.ACTION_POINTER_DOWN) {
					Run.NewTouch[pid] = true;			// which pointer (0 or 1), cleared on UP
					Run.mEventList.add(new Run.EventHolder(GR_TOUCH, 0, null));
				}
				else if	(action == MotionEvent.ACTION_MOVE) {
					Run.NewTouch[pid] = true;
				} else if (action == MotionEvent.ACTION_UP ||
					action == MotionEvent.ACTION_POINTER_UP) {
					Run.NewTouch[pid] = false;
				}
			}
			return true;
		}
*/
	    @Override//vv2017-10-22gt
	    public boolean onTouchEvent(MotionEvent event) {
	   	 super.onTouchEvent(event);
	   	 try{
	        // get pointer index from the event object
	        int pointerIndex = event.getActionIndex();

	        // get pointer ID
	        int pointerId = event.getPointerId(pointerIndex);

	        // get masked (not specific to a pointer) action
	        int maskedAction = event.getActionMasked();

	        switch (maskedAction) {

	        case MotionEvent.ACTION_DOWN:
	        case MotionEvent.ACTION_POINTER_DOWN: {
	            // We have a new pointer. Lets add it to the list of pointers

	            PointF f = new PointF();
	            f.x = event.getX(pointerIndex);
	            f.y = event.getY(pointerIndex);
	            Run.mNewTouch = pointerId;
	            Run.mActivePointers.put(pointerId, f);
	            if (pointerId < 2){// For legacy
						Run.TouchX[pointerId] = event.getX(pointerIndex)*touchScaleX + touchTransX;
						Run.TouchY[pointerId] = event.getY(pointerIndex)*touchScaleY + touchTransY;
						Run.NewTouch[pointerId] = true;			// which pointer (0 or 1), cleared on UP
	            }
					Run.mEventList.add(new Run.EventHolder(GR_TOUCH, 0, null));
	            break;
	        }
	        case MotionEvent.ACTION_MOVE: { // a pointer was moved
	            for (int size = event.getPointerCount(), i = 0; i < size; i++) {
	            	PointF point = Run.mActivePointers.get(event.getPointerId(i));
	                if (point != null) {
	                    point.x = event.getX(i);
	                    point.y = event.getY(i);
	                 //   Run.mActiveTouches.put(event.getPointerId(i),0);
	                    if (i < 2){// For legacy
	     						Run.TouchX[pointerId] = event.getX(pointerIndex)*touchScaleX + touchTransX;
	   						Run.TouchY[pointerId] = event.getY(pointerIndex)*touchScaleY + touchTransY;
	                  	  Run.NewTouch[i] = true;
	                    }
	                }
	            }
	            break;
	        }
	        case MotionEvent.ACTION_UP:
	        case MotionEvent.ACTION_POINTER_UP:
	        case MotionEvent.ACTION_CANCEL: {
	            Run.mActivePointers.remove(pointerId);
               if (pointerId < 2){// For legacy
               	Run.NewTouch[pointerId] = false;
               }
	     //       Run.mActiveTouches.remove(pointerId);
	            
	            break;
	        }
	        }
	  //      invalidate(); // Display is blinking
				}catch (Exception e) {
				//	Run.PrintShow(e.toString()); //Ignore exeption
					}//2016-10-27 gt

	        return true;
	    }//^^2017-10-22gt

		public Paint newPaint(Paint fromPaint) {
			Typeface tf = fromPaint.getTypeface();
			Paint rPaint = new Paint(fromPaint);
			rPaint.setTypeface(tf);
			return rPaint;
		}

		@Override
		synchronized public void onDraw(Canvas canvas) {
			if (doEnableBT) {							// If this activity is running
				enableBT();								// Bluetooth must be enabled here
				doEnableBT = false;
			}

			if (startConnectBT) {
				startsBTConnect();
				startConnectBT = false;
			}

			if (doSTT) {
				//Intent intent = Run.buildVoiceRecognitionIntent();
				try{//vv2017-11-25gt
					Context context = getApplicationContext();
					Intent intent = Run.buildVoiceRecognitionIntent(context);
					startActivityForResult(intent, Run.VOICE_RECOGNITION_REQUEST_CODE);
					} catch(Exception e) {//vv2017-11-25gt
						Run.PrintShow("GR Error in STT_LISTEN\n"+e);
					}//^^2017-11-25gt
				doSTT = false;
			}

			if ((Run.RealDisplayList == null) || (Run.DisplayList == null)) {
				Log.e(LOGTAG, "GR.onDraw: null DisplayList");
				finish();								// lost context, bail out
				return;
			}

			// float scale = getResources().getDisplayMetrics().density;
			drawView.setDrawingCacheEnabled(true);
			canvas.scale(scaleX, scaleY);
			canvas.translate(transX, transY); //2017-10-22gt

			if (Brightness != -1) {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.screenBrightness = Brightness;
				getWindow().setAttributes(lp);
				Brightness = -1;						// do it only once
			}

			synchronized (Run.DisplayList) {
				for (int di : Run.RealDisplayList) {
					if (di >= Run.DisplayList.size()) continue;
					BDraw b = Run.DisplayList.get(di);
					if (!doDraw(canvas, b)) {
						finish();
						break;
					}
				}
				condReleaseLOCK();
			}
		} // onDraw()

		public boolean doDraw(Canvas canvas, BDraw b) {
			float fx1;
			float fy1;
			RectF rectf;

			Paint thePaint;
			Bitmap theBitmap;
//			canvas.scale(scaleX, scaleY);

			thePaint = null;
			Type type = Type.Null;
			if ((b != null) && b.isVisible()) {
				type = b.type();
				int pIndex = b.paint();
				if (Run.PaintList.size() == 0)						return true;
				if (pIndex < 1 || pIndex >= Run.PaintList.size())	return true;

				thePaint = Run.PaintList.get(pIndex);
				int alpha = b.alpha();
				if ((alpha < 256) && (alpha != thePaint.getAlpha())) {
					thePaint = newPaint(thePaint);
					thePaint.setAlpha(alpha);
				}
			}

			switch (type) {
				case Group:
				case Null:
					break;
				case Open:
					Running = true;						// flag for GR.Open
					break;
				case Close:
					Running = false;
					return false;

				case Circle:
					canvas.drawCircle(b.x(),b.y(),b.radius(), thePaint);
					break;
				case Rect:
					if(b.rx() == 0 && b.ry() == 0){//Is a little faster 2016-10-27 gt
						canvas.drawRect(b.left(), b.top(), b.right(), b.bottom(), thePaint);
					}else{//vv 2016-10-27 gt
						rectf = new RectF(b.left(), b.top(), b.right(), b.bottom());
						canvas.drawRoundRect(rectf, b.rx(), b.ry(), thePaint);
					}//^^ 2016-10-27 gt
					break;
				case Clip:
					canvas.clipRect(b.left(), b.top(), b.right(), b.bottom(), b.clipOp());
					break;
				case Oval:
					rectf = new RectF(b.left(), b.top(), b.right(), b.bottom());
					canvas.drawOval(rectf, thePaint);
					break;
				case Arc:
					rectf = new RectF(b.left(), b.top(), b.right(), b.bottom());
					canvas.drawArc(rectf, b.arcStart(), b.arcSweep(), b.useCenter(), thePaint);
					break;
				case Line:
					canvas.drawLine(b.x1(),b.y1(),b.x2(),b.y2(), thePaint);
					break;
				case Point:
					canvas.drawPoint(b.x(),b.y(), thePaint);
					break;
				case SetPixels:
					fx1 = b.x();
					fy1 = b.y();
					Var.ArrayDef array = b.array();
					int pBase = b.arrayStart();
					int pLength = b.arraySublength();
					float[] pixels = new float[pLength];
					for (int j = 0; j < pLength; ++j) {
						pixels[j] = (float)array.nval(pBase + j) + fx1;
						++j;
						pixels[j] = (float)array.nval(pBase + j) + fy1;
					}
					canvas.drawPoints(pixels, thePaint);
					break;
				case Path://vv 2016-10-31 gt
				   //String[] commandNames = {"_MoveTo","_ArcTo","_CubicTo","_LineTo","_QuadTo","_Close","_End"};
				   //0.0001 commandNameIndex 00 neededEntries
				   //float[] neededEntries = {0.00011002f,0.00012006f,0.00013006f,0.00014002f,0.00015004f,0.00016000f,0.00017000f};
					ArrayList<Float> thisPathList = b.listF();
					// User may have changed the list. If it has
					// an odd number of coordinates, ignore the last.
					int pathEntries = thisPathList.size();// / 2;
					if (pathEntries >= 2) {					// do nothing if only one point
						fx1 = b.x();
						fy1 = b.y();
						Path path = new Path();
						Iterator<Float> listIt = thisPathList.iterator();
						float div = 10f;
						for (int p = 0; p < pathEntries; ++p) {
							float number = listIt.next();
							//Run.PrintShow(p + " : "+number);
							//Run.PrintShow(" floor: "+Math.floor(number));
							//Run.PrintShow(" frac: "+(number -Math.round(number)));
							//Run.PrintShow(" frac: "+100000*(number -Math.round(number)));
							float frac1 = 100000*(number -Math.round(number));
							if(frac1 > 0){
								//float frac2 = 1000*(number*100000 -Math.round(number*100000));
								//Run.PrintShow(" neededEntriesFloat: "+( frac2));
								//Run.PrintShow(" neededEntries: "+((int) Math.round(frac2)));
								int commandIndex = Math.round(frac1)-11;
								//Run.PrintShow(" commandIndex: "+commandIndex);
								//Run.PrintShow(" commandName: "+commandNames[ commandIndex]);
								if(commandIndex == 0 ){ // _MoveTo
									path.moveTo(listIt.next()/div+ fx1, listIt.next()/div+fy1);
								}
								/*
								ArcTo
								Add the specified arc to the path as a new contour.
								Parameters:oval The bounds of oval defining the shape and size 
								of the arcstartAngle Starting angle (in degrees) where the arc 
								begins sweep
								Angle Sweep angle (in degrees) measured clockwise
								*/
								if(commandIndex == 1 ){ // _ArcTo
									// path.arcTo(oval, startAngle, sweepAngle);
									rectf = new RectF(listIt.next()/div+ fx1,  listIt.next()/div+fy1, listIt.next()/div+ fx1, listIt.next()/div+fy1);
									path.arcTo(rectf, listIt.next()/div, listIt.next()/div);
								}
								if(commandIndex == 2 ){ // _CubicTo
									path.cubicTo(listIt.next()/div+ fx1, listIt.next()/div+fy1, listIt.next()/div+fx1,listIt.next()/div+ fy1, listIt.next()/div+fx1, listIt.next()/div+fy1);
								}
								if(commandIndex == 3 ){ // _LineTo
									path.lineTo(listIt.next()/div+ fx1, listIt.next()/div+fy1);
								}
								if(commandIndex == 4 ){ // _QuadTo
									path.quadTo(listIt.next()/div+fx1,listIt.next()/div+ fy1, listIt.next()/div+fx1, listIt.next()/div+fy1);
								}
								if(commandIndex == 5 )path.close(); // _Close
								if(commandIndex == 6 )break; // _End
							}
						}
						//RectF bounds = new RectF(0,0,0,0);
						//path.computeBounds(bounds, true);
						canvas.drawPath(path, thePaint);
					}
					break;//^^ 2016-10-31 gt
				case Poly:
					ArrayList<Double> thisList = b.list();
					// User may have changed the list. If it has
					// an odd number of coordinates, ignore the last.
					int points = thisList.size() / 2;
					if (points >= 2) {					// do nothing if only one point
						fx1 = b.x();
						fy1 = b.y();
						Path path = new Path();
						Iterator<Double> listIt = thisList.iterator();
						float firstX = listIt.next().floatValue() + fx1;
						float firstY = listIt.next().floatValue() + fy1;
						path.moveTo(firstX, firstY);
						for (int p = 1; p < points; ++p) {
							float x = listIt.next().floatValue() + fx1;
							float y = listIt.next().floatValue() + fy1;
							path.lineTo(x, y);
						}
						//path.lineTo(firstX, firstY);// Obsolete, because path.close(); 2016-10-27 gt
						path.close();
						canvas.drawPath(path, thePaint);
					}
					break;
				case Text:
					canvas.drawText(b.text(), b.x(), b.y(), thePaint);
					break;
				case Bitmap:
					int bitmapIndex = b.bitmap();
					theBitmap = Run.BitmapList.get(bitmapIndex);
					if (theBitmap != null) {
						if (theBitmap.isRecycled()) {
							Run.BitmapList.set(bitmapIndex, null);
							theBitmap = null;
						}
					}
					if (theBitmap != null) {
						canvas.drawBitmap(theBitmap, b.x(), b.y(), thePaint);
					} else {
						NullBitMap = true;
					}
					break;
				case RotateStart:
					canvas.save();
					canvas.rotate(b.angle(), b.x1(), b.y1());
					break;
				case RotateEnd:
					canvas.restore();
					break;
				default:
					break;
			}
			return true;
		} // doDraw()

	} // class DrawView

}
