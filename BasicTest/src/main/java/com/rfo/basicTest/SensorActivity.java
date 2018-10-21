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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;


// Log.d(SensorActivity.LOGTAG, " " + SensorActivity.CLASSTAG + " ignored Auto Run" );
public class SensorActivity implements SensorEventListener {
	private static final String LOGTAG = "SensorActivity";
	private static final String CLASSTAG = SensorActivity.class.getSimpleName();

	//public static final int MaxSensors = 20; //?????????????????
	public static final int MaxSensors = 30; // 2018-06-03gt
	public static final int UNINIT = -1;
	private SensorManager mSensorManager;
	private boolean mIsOpen = false;
	private Sensor mActiveSensors[];
	private int mActiveSensorDelays[];
	private int mPendingSensorDelays[];
	private double[][] mSensorValues;
	private int[] mSensorValuesLength; // 2018-06-03gt
	private int[] mSensorTypes; // 2018-06-03gt

	// This constructor is invoked from Run when the user executes
	// sensors.list or sensors.open

	public SensorActivity(Context context) {
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		mActiveSensors = new Sensor[MaxSensors + 1];			// holds references to active sensors
		mActiveSensorDelays = new int[MaxSensors + 1];			// holds delay settings of active sensors
		mPendingSensorDelays = new int[MaxSensors + 1];			// holds delay settings of sensors the user wants to activate
		//mSensorValues = new double[MaxSensors + 1][4];			// holds the current sensor values
		mSensorValues = new double[MaxSensors + 1][20];			// holds the current sensor values// 2018-06-03gt
		mSensorValuesLength = new int[MaxSensors + 1];		// 2018-06-03gt
		for (int i = 0; i <= MaxSensors; ++i) {
			mActiveSensors[i] = null;							// initialize the sensor references to null
			mActiveSensorDelays[i] =							// initialize the delay arrays to "uninitialized"
			mPendingSensorDelays[i] = UNINIT;
			//for (int j = 0; j < 4; ++j) {
			for (int j = 0; j < 20; ++j) {// 2018-06-03gt
				mSensorValues[i][j]= 0;							// initialize the values array to zero
			}
		}
		SensorManager oSM = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> sensorsList = oSM.getSensorList(Sensor.TYPE_ALL);
		mSensorTypes = new int[sensorsList.size()];
		for (int i = 0; i < sensorsList.size() ; i++){
			mSensorTypes[i] = sensorsList.get(i).getType();
		}		

	}

	public ArrayList<String> takeCensus() {
		ArrayList<String> census = new ArrayList<String>();
		List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);		// Get the list of sensors
		String name;
		for (Sensor sensor : sensorList) {						// and iterate through it
			int type = sensor.getType();						// adding each sensor to SensorCensus list
//            	 Log.d(SensorActivity.LOGTAG, "Sensor list add: " + type );
			switch (type) {
			case Sensor.TYPE_ACCELEROMETER:       name = "Accelerometer";       break;
			case Sensor.TYPE_GRAVITY:             name = "Gravity";             break;
			case Sensor.TYPE_GYROSCOPE:           name = "Gyroscope";           break;
//2016-10-10gt			case Sensor.TYPE_GYROSCOPE_UNCALIBRATED: name = "Uncalibrated Gyroscope"; break;
			case Sensor.TYPE_LIGHT:               name = "Light";               break;
			case Sensor.TYPE_MAGNETIC_FIELD:      name = "Magnetic Field";      break;
			//2016-10-10gt			case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED: name = "Uncalibrated Magnetic Field"; break;
			case Sensor.TYPE_ORIENTATION:         name = "Orientation";         break;
			case Sensor.TYPE_PRESSURE:            name = "Pressure";            break;
			case Sensor.TYPE_PROXIMITY:           name = "Proximity";           break;
			case Sensor.TYPE_LINEAR_ACCELERATION: name = "Linear Acceleration"; break;
//			case Sensor.TYPE_STEP_DETECTOR:       name = "Step Detector";       break;					KITKAT
//			case Sensor.TYPE_STEP_COUNTER:        name = "Step Counter";        break;					KITKAT
			//2016-10-10gt			case Sensor.TYPE_SIGNIFICANT_MOTION:  name = "Significant Motion";  break;
			case Sensor.TYPE_ROTATION_VECTOR:     name = "Rotational Vector";   break;
			//2016-10-10gt			case Sensor.TYPE_GAME_ROTATION_VECTOR:name = "Game Rotation Vector";break;
//			case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:name = "Geomagnetic Rotation Vector";break;	KITKAT
			case Sensor.TYPE_TEMPERATURE:         name = "Temperature";         break;
			case Sensor.TYPE_AMBIENT_TEMPERATURE: name = "Ambient Temperature"; break;
			case Sensor.TYPE_RELATIVE_HUMIDITY:   name = "Relative Humidity";   break;
			default:                              name = "Unknown";             break;
			}
			census.add(name + ", Type = " + type);
		}

		return census;
	}

	/* Call the markForOpen(int, int) method for each sensor the user wants to monitor.
	 * When done, call the doOpen() method to register the sensors the user requested.
	 */

	/* Record a sensor the user wants to monitor. Only record valid sensors
	 * that are not already open. If the delay value is invalid, use NORMAL.
	 * Return true if the sensor gets scheduled for open,
	 * false if the sensor type is invalid or the sensor is already registered.
	 */
	public boolean markForOpen(int type, int delay) {
		if ((type < 0) || (type >= MaxSensors)) { return false; }	// invalid
		if (mActiveSensors[type] != null) { return false; }			// already active
		/* Android < 5
		switch (type) {
		case Sensor.TYPE_ACCELEROMETER:
		case Sensor.TYPE_MAGNETIC_FIELD:
			//2016-10-10gt		case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
		case Sensor.TYPE_ORIENTATION:
		case Sensor.TYPE_GYROSCOPE:
			//2016-10-10gt		case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
		case Sensor.TYPE_LIGHT:
		case Sensor.TYPE_PRESSURE:
		case Sensor.TYPE_TEMPERATURE:
		case Sensor.TYPE_PROXIMITY:
		case Sensor.TYPE_GRAVITY:
		case Sensor.TYPE_LINEAR_ACCELERATION:
			//2016-10-10gt		case Sensor.TYPE_SIGNIFICANT_MOTION:
		case Sensor.TYPE_ROTATION_VECTOR:
			//2016-10-10gt		case Sensor.TYPE_GAME_ROTATION_VECTOR:
		case Sensor.TYPE_RELATIVE_HUMIDITY:
		case Sensor.TYPE_AMBIENT_TEMPERATURE:
//		case Sensor.TYPE_STEP_DETECTOR:											KITKAT
//		case Sensor.TYPE_STEP_COUNTER:											KITKAT
//		case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:							KITKAT
			
			
			if ((delay < 0) || (delay > SensorManager.SENSOR_DELAY_NORMAL)) {
				delay = SensorManager.SENSOR_DELAY_NORMAL;
			}
			mPendingSensorDelays[type] = delay;					// List as "pending"
			return true;
		default:
			return false;
		}
		*/
		for (int i = 0; i < mSensorTypes.length ; i++){ // vv 2018-06-03gt
			if(mSensorTypes[i] == type){
				if ((delay < 0) || (delay > SensorManager.SENSOR_DELAY_NORMAL)) {
					delay = SensorManager.SENSOR_DELAY_NORMAL;
				}
				mPendingSensorDelays[type] = delay;					// List as "pending"
				return true;
			}
		}		
		return false; // ^^ 2018-06-03gt
	}

	public void doOpen() {										// register all sensors on the "pending" list
		for (int type = 0; type <= MaxSensors; ++type) {
			int delay = mPendingSensorDelays[type];
			if (delay == UNINIT) { continue; }					// Not "pending"

			Log.d(SensorActivity.LOGTAG, "Sensor register listener: " + type);
			Sensor sensor = mSensorManager.getDefaultSensor(type);
			mSensorManager.registerListener(this, sensor, delay);

			mActiveSensors[type] = sensor;						// remember the sensor
			mActiveSensorDelays[type] = delay;					// and its delay setting
			mPendingSensorDelays[type] = UNINIT;				// not "pending" any more
			mIsOpen = true;
		}
	}

	/* Register all of the sensors that have been previously opened.
	 * This method assumes they have all been unregistered.
	 * Intended to be called from the onResume() of an Activity.
	 */
	public void reOpen() {
		for (int type = 0; type <= MaxSensors; ++type) {
			Sensor sensor = mActiveSensors[type];
			if (sensor == null) { continue; }					// Not active

			int delay = mActiveSensorDelays[type];
			mSensorManager.registerListener(this, sensor, delay);
		}
	}

	public boolean isOpen() {									// Return true if any sensor is active
		return mIsOpen;
	}

	public synchronized double[] getValues(int type) {			// Return current values of one sensor
		int m = Math.max(4, mSensorValuesLength[type]+1);// vv 2018-06-03gt
		double[] values = new double[m];
		for (int i = 1; i <= mSensorValuesLength[type]; ++i) {
			values[i] = mSensorValues[type][i];
		}// ^^ 2018-06-03gt
		return values;
	}

	public void stop() {										// Unregister all sensors
		mSensorManager.unregisterListener(this);
		mIsOpen = false;
//		Log.d(SensorActivity.LOGTAG, " " + SensorActivity.CLASSTAG + " unregister "  );
	}

	@Override
	public synchronized void onSensorChanged(SensorEvent event) {

		// Called each time a sensor value has changed.

		// A sensor has changed. If its value is reliable,
		// copy the values in the parameter variables

		Sensor sensor = event.sensor;
		int type = sensor.getType();
/*		Log.d(SensorActivity.LOGTAG, "Sensor changed" + type + " Values = " +
				event.values[0] + "," +
				event.values[1] + "," +
				event.values[2] + "," );*/

/*		if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
			Log.d(SensorActivity.LOGTAG, "Sensor Unreliable, "  + type);
			return;
		}*/
		/*
		mSensorValues[type][1] = event.values[0];
		mSensorValues[type][2] = event.values[1];
		mSensorValues[type][3] = event.values[2];
		*/
		// vv 2018-06-03gt
		if (event.values.length > 0)	mSensorValues[type][1] = event.values[0]; else mSensorValues[type][1] = 0;
		if (event.values.length > 1)	mSensorValues[type][2] = event.values[1]; else mSensorValues[type][2] = 0;
		if (event.values.length > 2)	mSensorValues[type][3] = event.values[2]; else mSensorValues[type][3] = 0;
		mSensorValuesLength[type] = event.values.length;
		for (int i = 1; i <= mSensorValuesLength[type]; ++i) {
			mSensorValues[type][i] = event.values[i-1];
		}
		// ^^ 2018-06-03gt

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

}
 
