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

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;

import com.rfo.basicTest.R;
import android.view.Window;
import android.view.WindowManager;


// **************************** Using the device camera UI ******************************

public class CameraView extends Activity implements SurfaceHolder.Callback,
													OnClickListener{

	public static final int PICTURE_MODE_USE_UI = 0;
	public static final int PICTURE_MODE_AUTO = 1;
	public static final int PICTURE_MODE_MANUAL = 2;
	public static final int PICTURE_MODE_BLIND = 3; // preview on dummy surface, view is black, implies PICTURE_MODE_AUTO
	public static final int PICTURE_MODE_USE_VIDEO_UI = 4;//2018-01-08gt
	public static final String EXTRA_PICTURE_MODE = "picture_mode";
	public static final String EXTRA_VIDEO_MODE = "video_mode";//2018-01-08gt
	
	public static final String EXTRA_CAMERA_NUMBER = "camera_number";
	public static final String EXTRA_FLASH_MODE = "flash_mode";
	public static final String EXTRA_FOCUS_MODE = "focus_mode";
	public static final String EXTRA_ORIENTATION = "orientation";
	public static final String EXTRA_CAMPARAMS = "cam_params";
	public static final String EXTRA_FILENAME = "file_name";
	public static final String EXTRA_DURATION_LIMIT = "duration_limit";//2018-01-08gt
	public static final String EXTRA_SIZE_LIMIT = "size_limit";//2018-01-08gt

	private int mPictureMode;
	private int mCameraNumber;
	private int mNewFlashMode;
	private int mNewFocusMode;
	private int durationLimit;//2018-01-08gt
	private long sizeLimit;//2018-01-08gt
	private boolean mAutoFocus;
	public static String fileNameForSaving = "";//2017-12-14gt
	public static int orientation = 0;//2017-12-14gt
	public static int cam_params = 0;//2017-12-14gt


	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Intent intent = getIntent();
		mPictureMode = intent.getIntExtra(EXTRA_PICTURE_MODE, 0);
		mCameraNumber = intent.getIntExtra(EXTRA_CAMERA_NUMBER, 0);
		mNewFlashMode = intent.getIntExtra(EXTRA_FLASH_MODE, 0);
		mNewFocusMode = intent.getIntExtra(EXTRA_FOCUS_MODE, 0);
		orientation = intent.getIntExtra(EXTRA_ORIENTATION, 0);
		cam_params = intent.getIntExtra(EXTRA_CAMPARAMS, 0);
		fileNameForSaving = intent.getStringExtra(EXTRA_FILENAME);
		durationLimit = intent.getIntExtra(EXTRA_DURATION_LIMIT, -1);
		sizeLimit = intent.getLongExtra(EXTRA_SIZE_LIMIT, -1L);
		
		if (mPictureMode == PICTURE_MODE_USE_UI) {
			doCameraUI();
		} else if (mPictureMode == PICTURE_MODE_USE_VIDEO_UI) {
			doVideoUI();
		} else {
			doCameraNow();
		}
	}

private void doCameraUI(){
	  final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	  intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(getFileName("jpg"))) ); 
	  startActivityForResult(intent, 254);
}

/* unused now 2017-12-14gt
	private String getTempFileName() { return getTempFileName("jpg"); }//changed to most common jpg //2017-12-14gt

	private String getTempFileName(String ext){		// ext is "png" or "jpg"
		return Basic.getDataPath("image." + ext);
	}
*/
private void doVideoUI(){
	final Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
	intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Basic.getDataPath(fileNameForSaving))) ); 
	if (durationLimit > -1) intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,durationLimit ); // OK! in sec.
	//intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);// 0, 1 or maybe higher 
	if (sizeLimit > -1)intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, sizeLimit); //OK! 12*1048*1048=12MB 
	//intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, 3); // or 1 Does it work??? No!
	//overridePendingTransition(0, 0); //??????? See eclipse help popup
	startActivityForResult(intent, 255);
}

@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		if ((resultCode == RESULT_OK) && (requestCode == 254)) {
			String fn = getFileName("jpg");
			try {
				BitmapFactory.Options BFO = new BitmapFactory.Options();
				//BFO.inSampleSize = 4;
				BFO.inSampleSize = 1;//vv2017-12-14gt
				Run.CameraBitmap = BitmapFactory.decodeFile(fn, BFO); // Make the bit map from the file
				DisplayMetrics dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);
				int wD = Math.max(dm.widthPixels, dm.heightPixels);
				int hD = Math.min(dm.widthPixels, dm.heightPixels);
				double bw = Math.max(Run.CameraBitmap.getWidth(),
						Run.CameraBitmap.getHeight()); // Get the image width
				double bh = Math.min(Run.CameraBitmap.getWidth(),
						Run.CameraBitmap.getHeight()); // Get the image height
				double ar = Math.max(wD / bw, hD / bh); //2017-12-21gt
				//Run.PrintShow("ar: "+ar+" "+wD+" "+bw+" "+ hD+" "+bh);
				if (ar < 1.0) {
					Run.CameraBitmap = Bitmap.createScaledBitmap(Run.CameraBitmap,
							(int) fix(bw * ar), (int) fix(bh * ar), true);
				} //^^2017-12-14gt
			} catch (Exception e) {
			}
		}
		if ((resultCode == RESULT_OK) && (requestCode == 255)) {
			// May Be code to bind Video file name in a html file for viewing.
			//Run.PrintShow("Video is finished: ");
		}

		Run.CameraDone = true;
		finish();
	}

	@Override
	protected void onDestroy() {
		Run.CameraDone = true;
		if (mPreviewRunning || mAutoFocus || (mCamera != null)) {
			surfaceDestroyed(null);
		}
		super.onDestroy();
	}

// **************************** Auto/Manual shutter, No UI **********************************

	private static final String TAG = "CameraTest";
	private Camera mCamera = null;
	boolean mPreviewRunning = false;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;


	private void doCameraNow() {
		setOrientation(orientation);//2017-12-14gt

		if (mPictureMode != PICTURE_MODE_BLIND) {
			getWindow().setFormat(PixelFormat.TRANSLUCENT);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			setContentView(R.layout.camera);
			mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);
		} else {
			mSurfaceView = new SurfaceView(getApplicationContext());
		}
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		switch (mPictureMode) {
		case PICTURE_MODE_MANUAL:
			mSurfaceView.setOnClickListener(this);
			/** FALL THROUGH to add callback **/
		case PICTURE_MODE_AUTO:
			mSurfaceHolder.addCallback(this);
			break;
		case PICTURE_MODE_BLIND:
			surfaceCreated(mSurfaceHolder);
			surfaceChanged(mSurfaceHolder, 0, 0, 0);
			break;
		default:
			Log.e(TAG, "Invalid picture mode " + mPictureMode);
			break;
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	Camera.ErrorCallback errorCallback = new Camera.ErrorCallback() {
		
		@Override
		public void onError(int error, Camera camera) {
		}
	};
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private void setOrientation(int orientation) {		// Convert and apply orientation setting
		//Log.v(LOGTAG, "Set orientation " + orientation);
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
/*
	synchronized private void setOrientation(int orientation) {	// synchronized orientation change
		//Log.v(LOGTAG, "Set orientation " + orientation);
		CameraView.this.setOrientation(orientation);
	}
*/

	Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
		// The callback is not currently being called
		@Override
		public void onPictureTaken(byte[] imageData, Camera c) {
			if (imageData != null) {

				BitmapFactory.Options options=new BitmapFactory.Options();
				options.inSampleSize = 0;
				
				Run.CameraBitmap = BitmapFactory.decodeByteArray(imageData, 0,
						imageData.length,options);
				Run.CameraDone = true;
			    System.gc();

				finish();

			}

		}
	};

	Camera.AutoFocusCallback focusCallback = new Camera.AutoFocusCallback() {	
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			try{
				//mCamera.takePicture(null, null, null, pngCallback);//vv2017-12-14gt
				mCamera.takePicture(null, null, null, picCallback);//^^2017-12-14gt
			} catch (Exception e){
				Log.e(TAG, "Camera Exception " + e);
			}
		}
	};
	/*// unused now 2017-12-14gt
	Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
		// The callback is not currently being called
		public void onPictureTaken(byte[] data, Camera camera) {
			savePicture("jpg", data);
		}
	};

	Camera.PictureCallback pngCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			savePicture("png", data);
		}
	};
	*/
	Camera.PictureCallback picCallback = new Camera.PictureCallback() {//vv2017-12-14gt
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
      try {
			if (getPictureFormat().equals("jpeg")) {
				savePicture("jpg", data);
			} else {
				savePicture("png", data);
			}
			//Run.CameraDone = true;
			
			
		} catch (Exception e) {
			Run.PrintShow("Error at onPictureTaken(): " + e.getMessage() );
		}

		}
	};//^^2017-12-14gt

	/*
	private void savePicture(String ext, byte[] data) {
		try {
		//	Run.PrintShow("data.length: " + data.length);
		
		FileOutputStream outStream = null;
		String fileName = getFileName(ext);//fileName / vv2017-12-14gt
		//String fileName = Basic.getDataPath("image.jpg");
		//Run.PrintShow(fileName);
			outStream = new FileOutputStream(fileName);//^^2017-12-14gt
			outStream.write(data);
			outStream.close();
//			Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
		System.gc();
		BitmapFactory.Options BFO = new BitmapFactory.Options();
		BFO.inSampleSize = 4;
		//Run.CameraBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);//.decodeFile(fileName,BFO);           // Make the bit map from the file
		Run.CameraBitmap = BitmapFactory.decodeFile(fileName,BFO);           // Make the bit map from the file
		} catch (Exception e) {
			Run.PrintShow("Error BFO CameraView: \n" + e.getMessage());
		}
		// if (Run.CameraManual) mCamera.startPreview();
		Run.CameraDone = true;
		finish();
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
		finish();
	}	

	private String getFileName(String ext) { // ext is "png" or "jpg"
		try {
			if (fileNameForSaving.length() == 0) {//Temp File Name
				return Basic.getDataPath("image." + ext);
			} else {
				int w = fileNameForSaving.lastIndexOf(".");
				fileNameForSaving = fileNameForSaving.substring(0, w);
				return Basic.getDataPath(fileNameForSaving + "." + ext);
			}
		} catch (Exception e) {
			return Basic.getDataPath("image." + ext);
		}
	}

	private String getPictureFormat() { //2013-09-26 gt 
      try {

		String CameraParameters = mCamera.getParameters().flatten();//Parameters/2017-12-14gt
		int start = CameraParameters.indexOf("picture-format=");
		if (start>=0){
			String extr = CameraParameters.substring(start, start+40);
			String[] mem = extr.split("[=;]");
			return mem[1];
		}else{
			return "jpeg";
		}
		} catch (Exception e) {
			Run.PrintShow("Error getPictureFormat CameraView: \n" + e.getMessage());
			return "jpeg";
		}
	}
	private int getJpegQuality() { //2013-09-26 gt 
      try {

		String CameraParameters = mCamera.getParameters().flatten();//Parameters/2017-12-14gt
		int start = CameraParameters.indexOf("jpeg-quality=");
		if (start>=0){
			String extr = CameraParameters.substring(start, start+40);
			String[] mem = extr.split("[=;]");
			return Integer.parseInt(mem[1]);
		}else{
			return 90;
		}
		} catch (Exception e) {
			Run.PrintShow("Error getJpegQuality CameraView: \n" + e.getMessage());
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
	}//^^2017-12-14gt


	
	
	
	 @Override
	@SuppressLint("NewApi")// 2017-02-18gt
	public void surfaceCreated(SurfaceHolder holder) {
//		Log.d(TAG, "surfaceCreated");

		try {														// Run has previously determined SDK Level
			if (mCameraNumber == -1 ) { mCamera = Camera.open(); }	// If number = -1 then use level < 9 command
			else { mCamera = Camera.open(mCameraNumber); }			// else use level >= 9 command
		}
		catch (Exception e){										// If open fails, we are done.
			Log.e(TAG, "Camera open error: " + e);
			mCamera = null;
		}
		if (mCamera != null) {
			if (!setParameters()) {
				mCamera = null;
			} else try {
				mCamera.setPreviewDisplay(holder);
				if (orientation > -1)mCamera.setDisplayOrientation(orientation * 90);
				mCamera.startPreview();
				mPreviewRunning = true;
			} catch (Exception e) {
				Log.e(TAG, "Camera start preview error : " + e);
				try {
					mCamera.stopPreview();
				} catch (Exception e1) {
					// ignore: the preview didn't get started
				} finally {
					mCamera = null;
				}
			}
		}
		if (mCamera == null) {
			Run.CameraDone = true;
			finish();
		}
	}

	@Override
	public void onClick(View arg0) {

		if (mPictureMode == PICTURE_MODE_MANUAL) {
			if (!mAutoFocus) {
				try {
					//mCamera.takePicture(null, null, null, pngCallback);//vv2017-12-14gt
					mCamera.takePicture(null, null, null, picCallback);//^^2017-12-14gt
				} catch (Exception e) {
					Log.e(TAG, "CameraManual Exception " + e);
				}
			} else {
				mCamera.autoFocus(focusCallback);
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
//		Log.d(TAG, "surfaceChanged");
		if (mCamera == null) {					// If no camera link then done
			Log.e(TAG, "Surface changed. mCamera NULL ");
			Run.CameraDone = true;
			finish();
			return;
		}

		// If Auto shutter, take picture now.
		
		if (mPictureMode == PICTURE_MODE_AUTO || mPictureMode == PICTURE_MODE_BLIND) {
			if (!mAutoFocus) {
				try{
					//mCamera.takePicture(null, null, null, pngCallback);//vv2017-12-14gt
					mCamera.takePicture(null, null, null, picCallback);//^^2017-12-14gt
				} catch (Exception e){
					Log.e(TAG, "CameraAuto Exception " + e);
				}
			} else {
				mCamera.autoFocus(focusCallback);
			}
		}
	}

	private boolean setParameters() {	
		Camera.Parameters p = mCamera.getParameters();
		if (cam_params == 1 && Run.mCameraParameters != null) p = Run.mCameraParameters;//vv2017-12-14gt
		if (cam_params == 2 && GR.camParams != null) p = GR.camParams;//vv2017-12-14gt
		if (orientation > -1)p.setRotation(orientation*90);//^^2017-12-14gt
		//Run.PrintShow("Camera.Parameters: "+ p.toString());
		String oldMode = p.getFlashMode();
		String newMode;
		switch (mNewFlashMode) {
		case 0: newMode = Camera.Parameters.FLASH_MODE_AUTO;    break;
		case 1: newMode = Camera.Parameters.FLASH_MODE_ON;      break;
		case 2: newMode = Camera.Parameters.FLASH_MODE_OFF;     break;
		case 3: newMode = Camera.Parameters.FLASH_MODE_TORCH;   break;	//Full Power/Flashlight/2013-07-25-16-42 gt
		case 4: newMode = Camera.Parameters.FLASH_MODE_RED_EYE; break;	//Flashlight/2013-07-27-19-12 gt
		default: newMode = ""; break;
		}
		List<String> supportedModes = p.getSupportedFlashModes();
		if ((oldMode != null) && (supportedModes != null)) {
			for (String mode : supportedModes) {
				if (mode.equals(newMode)) {
					p.setFlashMode(newMode);
					break;
				}
			}
		} else {														// "Supported Modes" not supported!
			p.setFlashMode(newMode);									// Just do it. May not work, but doesn't seem to hurt.
		}

		oldMode = p.getFocusMode();
		switch (mNewFocusMode) {											//Focus/2013-07-25-17-37 gt
		case 0: newMode = Camera.Parameters.FOCUS_MODE_AUTO;     break;
		case 1: newMode = Camera.Parameters.FOCUS_MODE_FIXED;    break;
		case 2: newMode = Camera.Parameters.FOCUS_MODE_INFINITY; break;
		case 3: newMode = Camera.Parameters.FOCUS_MODE_MACRO;    break;
		// (4 CONTINUOUS_VIDEO permissions?)
		//case 5: newMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO; break;
		default: newMode = ""; break;
		}
		supportedModes = p.getSupportedFocusModes();
		if ((oldMode != null) && (supportedModes != null)) {
			for (String mode : supportedModes) {
				if (mode.equals(newMode)) {
					p.setFocusMode(newMode);
					mAutoFocus = mode.equals(Camera.Parameters.FOCUS_MODE_AUTO);
					break;
				}
			}
		} else {														// "Supported Modes" not supported!
			p.setFlashMode(newMode);									// Just do it. May not work, but doesn't seem to hurt.
		}

		// if (Run.CameraSceneModeBarcode == 1) p.setSceneMode(Camera.Parameters.SCENE_MODE_BARCODE);

		// Use the largest preview size
		
		List<Camera.Size> sizes = p.getSupportedPreviewSizes();
		Camera.Size cs = sizes.get(0);
		for (Camera.Size ps : sizes) {	//2013-07-27-01-15 gt
			if (cs.width < ps.width) {
				cs = ps;
			}
		}
		p.setPreviewSize(cs.width, cs.height);

		// Use the largest picture size
		
		sizes = p.getSupportedPictureSizes();
		cs = sizes.get(0);
		for (Camera.Size ps : sizes) {	//2013-07-27-01-15 gt
			if (cs.width < ps.width) {
				cs = ps;
			}
		}
		p.setPictureSize(cs.width, cs.height);

		// Now set the parameters
		
		try {
			mCamera.setParameters(p);
		} catch (Exception e) {
			Log.e(TAG, "Camera parameter set error : " + e);
			Run.CameraDone = true;
			finish();
			return false;

		}
		return true;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
//		Log.d(TAG, "surfaceDestroyed");
		if (mPreviewRunning) {
			if (mCamera != null) { mCamera.stopPreview(); }
			mPreviewRunning = false;
		}
		if (mAutoFocus) {
			if (mCamera != null) {mCamera.cancelAutoFocus(); }
			mAutoFocus = false;
		}
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}

}

