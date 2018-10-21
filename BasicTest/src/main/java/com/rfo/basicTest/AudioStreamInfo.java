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
	import android.os.AsyncTask;
	import android.os.Bundle;
	import android.util.Log;
	import android.widget.TextView;
	import java.io.IOException;
	import java.net.MalformedURLException;
	import java.net.URL;
	import java.util.Timer;
	import java.util.TimerTask;

	public class AudioStreamInfo extends Activity {

	    IcyStreamMeta streamMeta;
	    MetadataTask2 metadataTask2;
	    String title_artist;
	    TextView textView;


	    @Override
	    protected void onCreate(Bundle savedInstanceState) {


	        String streamUrl = "http://198.105.223.94:8008";
	        //streamUrl = "http://amp1.cesnet.cz:8000/cro1.ogg";
	        streamMeta = new IcyStreamMeta();
	        try {
	            streamMeta.setStreamUrl(new URL(streamUrl));
	        } catch (MalformedURLException e) {
	            e.printStackTrace();
	        }
	        metadataTask2 =new MetadataTask2();
	        try {
	            metadataTask2.execute(new URL(streamUrl));
	        } catch (MalformedURLException e) {
	            e.printStackTrace();
	        }

	        Timer timer = new Timer();
	        MyTimerTask task = new MyTimerTask();
	        timer.schedule(task,100, 10000);


	    }


	    protected class MetadataTask2 extends AsyncTask<URL, Void, IcyStreamMeta>
	    {
	        @Override
	        protected IcyStreamMeta doInBackground(URL... urls)
	        {
	            try
	            {
	                streamMeta.refreshMeta();
	                Log.e("Retrieving MetaData","Refreshed Metadata");
	            }
	            catch (IOException e)
	            {
	                Log.e(MetadataTask2.class.toString(), e.getMessage());
	            }
	            return streamMeta;
	        }

	        @Override
	        protected void onPostExecute(IcyStreamMeta result)
	        {
	            try
	            {
	                title_artist=streamMeta.getStreamTitle();
	                Log.e("Retrieved title_artist", title_artist);
	                if(title_artist.length()>0)
	                {
	                    //textView.setText(title_artist);
	                    Run.PrintShow(title_artist);
	                }
	            }
	            catch (IOException e)
	            {
	                Log.e(MetadataTask2.class.toString(), e.getMessage());
	            }
	        }
	    }

	    class MyTimerTask extends TimerTask {
	        @Override
			public void run() {
	            try {
	                streamMeta.refreshMeta();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	            try {
	                String title_artist=streamMeta.getStreamTitle();
	                Log.i("ARTIST TITLE", title_artist);
	            } catch (IOException e) {
	                e.printStackTrace();
	            }


	        }
	    }
	}



