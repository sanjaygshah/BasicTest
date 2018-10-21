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
// Credits to:
// https://github.com/dazza5000/IcyStreamMetaDataExample
// http://stackoverflow.com/questions/8970548/how-to-get-metadata-of-a-streaming-online-radio
// http://uniqueculture.net/2010/11/stream-metadata-plain-java/
 
 package com.rfo.basicTest;


	import java.io.IOException;
	import java.io.InputStream;

	import java.net.URL;
	import java.net.URLConnection;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;
	import java.util.regex.Matcher;
	import java.util.regex.Pattern;

import android.app.Activity;

	public class IcyStreamMeta extends Activity {
	   protected URL streamUrl;
	     //public URL streamUrl;
	    //private Map<String, String> metadata;
	    public Map<String, String> metadata;
	    private boolean isError;
	    private Map<String, String> data;

	    public IcyStreamMeta() {
	        isError = false;
	    }

	    /**
	     * Get artist using stream's title
	     *
	     * @return String
	     * @throws IOException
	     */
	    public String getArtist() throws IOException {
	        data = getMetadata();

	        if (!data.containsKey("StreamTitle"))
	            return "";

	        String streamTitle = data.get("StreamTitle");
	        String title = streamTitle.substring(0, streamTitle.indexOf("-"));
	        return title.trim();
	    }

	    /**
	     * Get streamTitle
	     *
	     * @return String
	     * @throws IOException
	     */
	    public String getStreamTitle() throws IOException {
	        data = getMetadata();

	        if (!data.containsKey("StreamTitle"))
	            return "";

	        return data.get("StreamTitle");
	    }

	    /**
	     * Get title using stream's title
	     *
	     * @return String
	     * @throws IOException
	     */
	    /*
	    public String getTitle() throws IOException {
	        data = getMetadata();

	        if (!data.containsKey("StreamTitle"))
	            return "";

	        String streamTitle = data.get("StreamTitle");
	        String artist = streamTitle.substring(streamTitle.indexOf("-") + 1);
	        return artist.trim();
	    }
		*/
	    public Map<String, String> getMetadata() throws IOException {
	        if (metadata == null) {
	            refreshMeta();
	        }

	        return metadata;
	    }

	    synchronized public void refreshMeta() throws IOException {
	        retreiveMetadata();
	    }

	    synchronized private void retreiveMetadata() throws IOException {
				Run.PrintShow("1");

	        URLConnection con = streamUrl.openConnection();
	        con.setRequestProperty("Icy-MetaData", "1");
	        con.setRequestProperty("Connection", "close");
	        con.setRequestProperty("Accept", null);
	        con.connect();
				Run.PrintShow("2");

	        int metaDataOffset = 0;
	        Map<String, List<String>> headers = con.getHeaderFields();
	        InputStream stream = con.getInputStream();
				Run.PrintShow("3");

	        if (headers.containsKey("icy-metaint")) {
	            // Headers are sent via HTTP
					Run.PrintShow("4");

	            metaDataOffset = Integer.parseInt(headers.get("icy-metaint").get(0));
	        } else {
					Run.PrintShow("5");

	            // Headers are sent within a stream
	            StringBuilder strHeaders = new StringBuilder();
	            char c;
					Run.PrintShow("6");

	            while ((c = (char) stream.read()) != -1) {
	                strHeaders.append(c);
	                if (strHeaders.length() > 5 && (strHeaders.substring((strHeaders.length() - 4), strHeaders.length()).equals("\r\n\r\n"))) {
	                    // end of headers
	                    break;
	                }
	            }
					Run.PrintShow("7");

	            // Match headers to get metadata offset within a stream
	            Pattern p = Pattern.compile("\\r\\n(icy-metaint):\\s*(.*)\\r\\n");
	            Matcher m = p.matcher(strHeaders.toString());
	            if (m.find()) {
	                metaDataOffset = Integer.parseInt(m.group(2));
	            }
	        }
				Run.PrintShow("8");

	        // In case no data was sent
	        if (metaDataOffset == 0) {
	            isError = true;
	            return;
	        }

	        // Read metadata
	        int b;
	        int count = 0;
	        int metaDataLength = 4080; // 4080 is the max length
	        boolean inData = false;
	        StringBuilder metaData = new StringBuilder();
	        // Stream position should be either at the beginning or right after headers
	        while ((b = stream.read()) != -1) {
	            count++;

	            // Length of the metadata
	            if (count == metaDataOffset + 1) {
	                metaDataLength = b * 16;
	            }

	            if (count > metaDataOffset + 1 && count < (metaDataOffset + metaDataLength)) {
	                inData = true;
	            } else {
	                inData = false;
	            }
	            if (inData) {
	                if (b != 0) {
	                    metaData.append((char) b);
	                }
	            }
	            if (count > (metaDataOffset + metaDataLength)) {
	                break;
	            }
	        }
				Run.PrintShow("9");

	        // Set the data
	        metadata = IcyStreamMeta.parseMetadata(metaData.toString());
				Run.PrintShow("10");

	        // Close
	        stream.close();

	    }

	    public boolean isError() {
	        return isError;
	    }

	    public URL getStreamUrl() {
	        return streamUrl;
	    }

	    public void setStreamUrl(URL streamUrl) {
	        this.metadata = null;
	        this.streamUrl = streamUrl;
	        this.isError = false;
	    }
	    
	/*    public void setStreamUrl(URL streamUrl2) {
	        metadata = null;
	        streamUrl = streamUrl2;
	        isError = false;
	    }
*/
	    public static Map<String, String> parseMetadata(String metaString) {
	        Map<String, String> metadata = new HashMap();
	        String[] metaParts = metaString.split(";");
	        Pattern p = Pattern.compile("^([a-zA-Z]+)=\\'([^\\']*)\\'$");
	        Matcher m;
	        for (int i = 0; i < metaParts.length; i++) {
	            m = p.matcher(metaParts[i]);
	            if (m.find()) {
	                metadata.put(m.group(1), m.group(2));
	            }
	        }

	        return metadata;
	    }
}
