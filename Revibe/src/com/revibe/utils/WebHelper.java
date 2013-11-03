package com.revibe.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.loopj.android.http.PersistentCookieStore;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class WebHelper {

	static private final String TAG = "WebHelper";

	static private PersistentCookieStore cookieStore;

    public static void setupCookieStore(Context context) {
        if (cookieStore == null)
            cookieStore = new PersistentCookieStore(context);
    }
    public static PersistentCookieStore getCookies() { return cookieStore; }
    public static void clearCookies() { cookieStore.clear(); }

    static public boolean networkConnected(Activity activity) {
        boolean status=false;
        try{
            ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState()== NetworkInfo.State.CONNECTED) {
                status= true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if(netInfo!=null && netInfo.getState()== NetworkInfo.State.CONNECTED)
                    status= true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return status;
    }

    static public Document getDocument(String url) {
		InputStream in = null;
		try {
			in = openHttpConnection(url);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(in);
            doc.getDocumentElement().normalize();
            return doc;
		} catch (Exception e) {
			Log.e(TAG, "getDocument(): error=" + e);
		} finally {
			if (in != null) {
				try { in.close(); } catch (IOException ioe) { }
			}				
		}
		return null;
	}
	
	static public Document getDocumentWithCookies(String url) {
        HttpClient httpclient = new DefaultHttpClient();
        InputStream in = null;
        try {
            Log.d(TAG, "getDocumentWithCookies(): url=" + url);

            //HttpClientParams.setCookiePolicy(param, CookiePolicy.RFC_2109);

            List<Cookie> cookies = cookieStore.getCookies();
            for (int i = 0; i < cookies.size(); i++)
                Log.d(TAG, "getDocumentWithCookies(): local cookie before=" + cookies.get(i));

            // Create local HTTP context
            HttpContext localContext = new BasicHttpContext();
            // Bind custom cookie store to the local context
            localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

            HttpGet httpget = new HttpGet(url);

            // Pass local context as a parameter
            HttpResponse response = httpclient.execute(httpget, localContext);
            HttpEntity entity = response.getEntity();

            cookies = cookieStore.getCookies();
            for (int i = 0; i < cookies.size(); i++)
                Log.d(TAG, "getDocumentWithCookies(): local cookie after=" + cookies.get(i));

            in = entity.getContent();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(in);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (Exception e) {
            Log.e(TAG, "getDocumentWithCookies(): error=" + e);
        } finally {
            if (in != null) {
                try { in.close(); } catch (IOException ioe) { }
            }
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
        return null;
	}
	
	
	static public String getTextResponse(String url) {
		return new String(getBytes(url, false));
	}

    static public JSONObject getJSONObject(String url) { return getJSONObject(url, false); }
	static public JSONObject getJSONObject(String url, boolean withCookies) {
        try {
            byte[] bytes = getBytes(url, withCookies);
            if (bytes != null) {
                String jsonText = new String(bytes);
                return new JSONObject(jsonText);
            }
        } catch (Exception e) {
            Log.e(TAG, "getJSONObject(): error=" + e);
        }
        return new JSONObject();
	}
    static public JSONArray getJSONArray(String url) { return getJSONArray(url, false); }
    static public JSONArray getJSONArray(String url, boolean withCookies) {
		try {
			byte[] bytes = getBytes(url, withCookies);
			if (bytes != null) {
				String jsonText = new String(bytes);
				return new JSONArray(jsonText);
			}
		} catch (Exception e) {
			Log.e(TAG, "getJSONArray(): error=" + e);
		}
		return new JSONArray();
	}
	
	static public byte[] getBytes(String url, boolean withCookies) {
        InputStream in = null;
        try {
            in = (withCookies ? openHttpConnectionWithCookies(url) : openHttpConnection(url));
            Vector<Byte> bytes = new Vector<Byte>();
            byte[] buffer = new byte[Math.max(1, in.available())];
            int bytesRead = in.read(buffer);
            while (bytesRead != -1 && !Thread.interrupted()) {
                for (int i = 0; i < bytesRead; ++i)	{
                    bytes.add(buffer[i]);
                }
                bytesRead = in.read(buffer);
            }
            byte[] result = new byte[bytes.size()];
            int i = 0;
            for (Byte byteVal : bytes)
                result[i++] = byteVal.byteValue();
            return result;
        } catch (Exception e) {
            Log.e(TAG, "getBytes(): error=" + e);
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "getBytes(): error=" + e);
        } finally {

            if (in != null) {
                try { in.close(); } catch (IOException ioe) { }
            }
        }
        return null;
    }

    static public InputStream openHttpConnectionWithCookies(String url) {
        HttpClient httpclient = new DefaultHttpClient();
        InputStream in = null;
        try {
            Log.d(TAG, "openHttpConnectionWithCookies(): url=" + url);

            //HttpClientParams.setCookiePolicy(param, CookiePolicy.RFC_2109);

            List<Cookie> cookies = cookieStore.getCookies();
            for (int i = 0; i < cookies.size(); i++)
                Log.d(TAG, "openHttpConnectionWithCookies(): local cookie before=" + cookies.get(i));

            // Create local HTTP context
            HttpContext localContext = new BasicHttpContext();
            // Bind custom cookie store to the local context
            localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

            HttpGet httpget = new HttpGet(url);

            // Pass local context as a parameter
            HttpResponse response = httpclient.execute(httpget, localContext);
            HttpEntity entity = response.getEntity();

            cookies = cookieStore.getCookies();

            for (int i = 0; i < cookies.size(); i++)
                Log.d(TAG, "openHttpConnectionWithCookies(): local cookie after=" + cookies.get(i));

            in = entity.getContent();
            return in;
        } catch (Exception e) {
            Log.e(TAG, "openHttpConnectionWithCookies(): error=" + e);
        }
        return null;
    }

    static private InputStream openHttpConnection(String urlString) throws IOException {
        InputStream in = null;
        int response = -1;
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        if (!(conn instanceof HttpURLConnection))
        	throw new IOException("Not an HTTP connection");
        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.setConnectTimeout(120000);  // Two-minute timeout
            httpConn.setReadTimeout(120000);
            
            httpConn.connect(); 
            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
            	in = httpConn.getInputStream();
            }   
        }
        catch (Exception ex) {
        	throw new IOException("Error connecting");
        }
        return in;     
    }
}
