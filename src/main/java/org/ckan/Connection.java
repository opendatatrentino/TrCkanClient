/*
 CKANClient-J - Data Catalogue Software client in Java
 Copyright (C) 2013 Newcastle University
 Copyright (C) 2012 Open Knowledge Foundation

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation, either version 3 of the
 License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.ckan;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.net.URL;

import java.net.MalformedURLException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Connection holds the connection details for this session
 *
 * @author Andrew Martin <andrew.martin@ncl.ac.uk>, Ross Jones
 * <ross.jones@okfn.org>
 * @version 1.8
 * @since 2012-05-01
 */
public final class Connection {

    private String m_host;
    private int m_port;
    private String _apikey = null;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public Connection() {
        this("http://datahub.io", 80);
    }

    public Connection(String host) {
        this(host, 80);
    }

    public Connection(String host, int port) {
        this.m_host = host;
        this.m_port = port;

        try {
            URL u = new URL(this.m_host + ":" + this.m_port + "/api");
        } catch (MalformedURLException mue) {
            System.out.println(mue);
        }
    }

    public void setApiKey(String key) {
        this._apikey = key;
    }

    /**
     * Makes a POST request
     *
     * Submits a POST HTTP request to the CKAN instance configured within the
     * constructor, returning the entire contents of the response.
     *
     * @param path The URL path to make the POST request to
     * @param data The data to be posted to the URL
     * @returns The String contents of the response
     * @throws A CKANException if the request fails
     */
    protected String post(String path, String data)
            throws CKANException {
        URL url = null;

        try {
            String urlString = this.m_host + ":" + this.m_port + path;
            logger.debug("posting url: " + urlString);
            logger.debug("posting data: " + data);
            url = new URL(urlString);
        } catch (MalformedURLException mue) {
            System.err.println(mue);
            return null;
        }

        String body = "";

        BasicClientConnectionManager bccm = null;
        //ClientConnectionManager cm = null;
        try {
            /**
             * ********************************************************************
             */
            SSLContext sslContext = SSLContext.getInstance("SSL");
            // set up a TrustManager that trusts everything
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    System.out.println("getAcceptedIssuers =============");
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs,
                        String authType) {
                    System.out.println("checkClientTrusted =============");
                }

                public void checkServerTrusted(X509Certificate[] certs,
                        String authType) {
                    System.out.println("checkServerTrusted =============");
                }
            }}, new SecureRandom());
            SSLSocketFactory sf = new SSLSocketFactory(sslContext);
            Scheme httpsScheme = new Scheme("https", 443, sf);
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(httpsScheme);
            bccm = new BasicClientConnectionManager(schemeRegistry);
                // apache HttpClient version >4.2 should use BasicClientConnectionManager
            // cm = new SingleClientConnManager(schemeRegistry);
            /**
             * ********************************************************************
             */
        } catch (KeyManagementException kme) {
            logger.error("Con ex: " + kme.getMessage());
        } catch (NoSuchAlgorithmException nsae) {
            logger.error("Con ex: " + nsae.getMessage());
        }

        //HttpClient httpclient = new DefaultHttpClient(bccm);
        HttpClient httpclient = HttpClientBuilder.create().build();
        try {
            HttpPost postRequest = new HttpPost(url.toString());
            postRequest.setHeader("X-CKAN-API-Key", this._apikey);
            StringEntity input = new StringEntity(data);
            input.setContentType("application/json");
            postRequest.setEntity(input);
            HttpResponse response = httpclient.execute(postRequest);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 301) { // quick hack just for dati.trentino                                
                logger.debug("Got a 301 Moved Permanently for a POST with a json as data, see https://github.com/opendatatrentino/TrCkanClient/issues/2");
                logger.debug("As quick hack retrying using a GET. GET parameters will be taken from POST data, supposing it is a JSON");   
                try {
                      
                    JsonElement jelement = new JsonParser().parse(data);                    
                    if (!jelement.isJsonObject()){
                        throw new JsonSyntaxException("POST body is not a json object");
                    }                    
                    
                    JsonObject jobj = jelement.getAsJsonObject();
                    StringBuilder urlParams = new StringBuilder("?");
                    for (Map.Entry<String,JsonElement> entry : jobj.entrySet()) {
                        
                        String param = entry.getValue().toString();
                        String strippedParam = param;
                        if (param.length() > 1 && param.charAt(0) == '\"' && param.charAt(param.length()-1) == '\"'){
                            strippedParam = param.substring(1, param.length()-1);
                        }
                        
                        urlParams.append(URLEncoder.encode(entry.getKey(),"UTF-8"))
                                 .append("=")
                                 .append(URLEncoder.encode(strippedParam, "UTF-8"));                       
                    };
                    String getUrl = url.toString() + urlParams.toString();
                    logger.debug("GET url = " + getUrl);
                    HttpGet getRequest = new HttpGet(getUrl);                    
                    
                    getRequest.setHeader("X-CKAN-API-Key", this._apikey);
                    response = httpclient.execute(getRequest);
                    statusCode = response.getStatusLine().getStatusCode();
                } catch (JsonSyntaxException ex) {
                    throw new RuntimeException("Got a 301 Moved Permanently after a POST. Retried with a GET and failed. See https://github.com/opendatatrentino/TrCkanClient/issues/2", ex);
                }

            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent())));

            String line = "";
            while ((line = br.readLine()) != null) {
                body += line;
            }
            logger.debug("post status: " + statusCode + " post result: " + body);
        } catch (IOException ioe) {
            System.out.println(ioe);
        } finally {
            httpclient.getConnectionManager().shutdown();
        }

        return body;
    }

}
