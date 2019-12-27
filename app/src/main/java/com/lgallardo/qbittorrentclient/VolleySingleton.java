/*
 *   Copyright (c) 2014-2015 Luis M. Gallardo D.
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the GNU Lesser General Public License v3.0
 *   which accompanies this distribution, and is available at
 *   http://www.gnu.org/licenses/lgpl.html
 *
 */
package com.lgallardo.qbittorrentclient;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

/**
 * Created by lgallard on 9/11/16.
 */
public class VolleySingleton {

    private static VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private static Context context;


    // Volley Singleton constructor
    private VolleySingleton(Context context) {
        VolleySingleton.context = context;
        requestQueue = getRequestQueue();
    }

    // Singleton getter
    public static synchronized VolleySingleton getInstance(Context context) {
        if (volleySingleton == null) {
            volleySingleton = new VolleySingleton(context);
        }
        return volleySingleton;
    }

    // Get requestQueue
    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    // Get requestQueue
    private RequestQueue getRequestQueueHttps(String keystore_path, String keystore_password) {
//        if (requestQueue == null) {
//            Log.d("Debug", "[SSLSocketFactory] getRequestQueueHttps");
//
//            requestQueue = Volley.newRequestQueue(context.getApplicationContext(),  new HurlStack(null, getSocketFactory(keystore_path,keystore_password)));
//        }
//        else{
//            Log.d("Debug", "[SSLSocketFactory] requestQueue is NOT null");
//        }

//        Log.d("Debug", "[SSLSocketFactory] getRequestQueueHttps");
//        Log.d("Debug", "[SSLSocketFactory] keystore_path: " + keystore_path);
//        Log.d("Debug", "[SSLSocketFactory] keystore_password: " + keystore_password);

        requestQueue = Volley.newRequestQueue(context.getApplicationContext(), new HurlStack(null, getSocketFactory(keystore_path, keystore_password)));

        return requestQueue;
    }

    // Add request to the queue
//    public void addToRequestQueue(Request request) {
//        getRequestQueue().add(request);
//    }

    public void addToRequestQueueHttps(Request request, String keystore_path, String keystore_password) {
        getRequestQueueHttps(keystore_path, keystore_password).add(request);
    }

    private TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {
        final X509TrustManager originalTrustManager = (X509TrustManager) trustManagers[0];
        return new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return originalTrustManager.getAcceptedIssuers();
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0) {
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkClientTrusted(certs, authType);
                            }
                        } catch (CertificateNotYetValidException e) {
                            e.printStackTrace();
                        } catch (CertificateExpiredException e) {
                            e.printStackTrace();
                        } catch (java.security.cert.CertificateException e) {
                            e.printStackTrace();
                        }
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0) {
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkServerTrusted(certs, authType);
                            }
                        } catch (CertificateNotYetValidException e) {
                            e.printStackTrace();
                        } catch (CertificateExpiredException e) {
                            e.printStackTrace();
                        } catch (java.security.cert.CertificateException e) {
                            e.printStackTrace();
                        }
                    }
                }
        };
    }

    private SSLSocketFactory getSocketFactory(String keystore_path, String keystore_password) {


        CertificateFactory cf = null;
        SSLContext sslContext = null;
        try {
            cf = CertificateFactory.getInstance("X.509");
            File localTrustStoreFile = null;
            InputStream caInput = null;

            KeyStore keyStore = KeyStore.getInstance("BKS");

            try {

                localTrustStoreFile = new File(keystore_path);
                caInput = new FileInputStream(localTrustStoreFile);

                Log.d("Debug", "[SSLSocketFactory] File path: " + localTrustStoreFile.getPath());
//            Log.d("Debug", "[SSLSocketFactory] localTrustStoreFile path: " + localTrustStoreFile.getPath());

                if(caInput == null || localTrustStoreFile == null || !localTrustStoreFile.exists()){

                    Log.d("Debug", "[SSLSocketFactory] caInput or localTrustStoreFile is null or empty: ");

                    keyStore.load(null, null);

                }else {

                    if (keystore_password == null || keystore_password.isEmpty()) {

                        Log.d("Debug", "[SSLSocketFactory] keystore_password is null or empty: ");

                        keyStore.load(caInput, "".toCharArray());

                    } else {

                        Log.d("Debug", "[SSLSocketFactory] keystore_password is NOT  null ");
                        keyStore.load(caInput, keystore_password.toCharArray());
                    }

                }

                caInput.close();

            } catch (FileNotFoundException e) {
                Log.d("Debug", "[SSLSocketFactory] FileNotFoundException: " + e.getMessage());
                Log.d("Debug", "[SSLSocketFactory] keyStore.load(null, null) ");
//                e.printStackTrace()

                keyStore.load(null, null);
            }

            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return hostname.equals(hostname); //The Hostname of your server
                }
            };


            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);


            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);
            TrustManager[] wrappedTrustManagers = getWrappedTrustManagers(tmf.getTrustManagers());
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, wrappedTrustManagers, null);


        } catch (CertificateException e) {
            Log.d("Debug", "[SSLSocketFactory] CertificateException: " + e.getMessage());
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            Log.d("Debug", "[SSLSocketFactory] NoSuchAlgorithmException: " + e.getMessage());
            e.printStackTrace();
        } catch (KeyStoreException e) {
            Log.d("Debug", "[SSLSocketFactory] KeyStoreException: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            Log.d("Debug", "[SSLSocketFactory] IOException: " + e.getMessage());
            e.printStackTrace();

        } catch (KeyManagementException e) {
            Log.d("Debug", "[SSLSocketFactory] KeyManagementException: " + e.getMessage());
            e.printStackTrace();
        }

        try {

            Log.d("Debug", "[SSLSocketFactory] Return SocketFactory!");

            SSLSocketFactory sf = sslContext.getSocketFactory();
            HttpsURLConnection.setDefaultSSLSocketFactory(sf);

            return sf;

        } catch (Exception e) {

//            Log.d("Debug", "[SSLSocketFactory] Return null");
            return null;
        }
    }


}