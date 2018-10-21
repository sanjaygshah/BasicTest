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
import java.util.ArrayList;

import android.util.Log;


// A read thread for su or system.read.line. Required because readLine is blocking.

public class SUReader {
	private static final String LOGTAG = "SUReader";

	public BufferedReader theReader;
	public ReadThread theReadThread;
	ArrayList<String> theReadBuffer;
	public boolean stop;

	public SUReader (BufferedReader stream, ArrayList<String> buffer) {
		theReader = stream;
		theReadBuffer = buffer;
	}

	public synchronized void start() {							// Start
		theReadThread = new ReadThread();
		theReadThread.start();
		stop = false;
	}

	public void stop() {										// Stop
		stop = true;
		theReader = null;
	}

	private class ReadThread extends Thread {

		@Override
		public void run() {										// Do the actual reading
			while (!stop) {
				String input = null;
				try {
					if (theReader != null) { input = theReader.readLine(); }
				}
				catch (Exception e) { }
				if (input == null) { input = ""; }

				if (theReadBuffer != null) {
					synchronized (theReadBuffer) {
						theReadBuffer.add(input);				// Put the input line into the buffer.
					}
				}
			}
			Log.i(LOGTAG, "ReadThread stopped");
		}
	}

}
