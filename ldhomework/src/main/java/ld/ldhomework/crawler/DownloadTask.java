/*
 * Copyright 2014 Christian Bitschnau, Wolfgang Rohregger

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package ld.ldhomework.crawler;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * The {@link DownloadTask} is used for downloading RDF documents while crawling
 * the network. It implements the {@link Callable} interface so that it may be
 * used in multithreaded setup - but the main reason for doing so is the
 * possibility to avoid blocking I/O. Internally this class uses the Apache
 * HTTPClient which does the actual HTTP GET operations including all redirect
 * handling. This also means that only HTTP URIs are supported.
 * 
 * @author wolfi
 * @author chb
 * 
 */
public class DownloadTask implements Callable<DownloadedFile> {

    private String currentURI;
    private CloseableHttpClient httpClient;

    private static final int SOCKET_TIMEOUT = 5000;
    private static final int CONNECT_TIMEOUT = 5000;

    private static final Logger LOG = java.util.logging.Logger
	    .getLogger(DownloadTask.class.getName());
    private static final long ONE_MB = 1024 * 1024;

    /**
     * Construct a new {@link DownloadTask} set for a specified uri.
     * 
     * @param uri
     *            the location and protocol of the document to Download.
     */
    public DownloadTask(String uri) {
	this.currentURI = uri;
	httpClient = HttpClients.createDefault();
    }

    /**
     * Execute the actual download as separate thread. Best used together with
     * {@link Executors}. Handles mime types text/turtle and
     * application/rdf+xml. Avoids other content types and files larger than 1
     * MB.
     */
    public DownloadedFile call() throws Exception {
	DownloadedFile downloadedFile = null;
	HttpGet httpget = new HttpGet(currentURI);

	Builder configBuilder = RequestConfig.custom();
	configBuilder.setConnectTimeout(CONNECT_TIMEOUT);
	configBuilder.setSocketTimeout(SOCKET_TIMEOUT);
	configBuilder.setCircularRedirectsAllowed(false);
	httpget.setConfig(configBuilder.build());
	httpget.addHeader("Accept", "text/turtle,application/rdf+xml");

	CloseableHttpResponse response = null;
	try {
	    LOG.info("Executing GET for " + currentURI);
	    response = httpClient.execute(httpget);

	    /*
	     * Check the status code. 200 OK means we got the document.
	     * Everything else needs to be handled, though the standard
	     * HTTPClient Strategy includes automatic redirect handling
	     * (probably following infinite redirects).
	     */
	    StatusLine statusLine = response.getStatusLine();
	    HttpEntity entity = response.getEntity();

	    LOG.info(currentURI + " cnttype: " + entity.getContentType());
	    int responseStatusCode = statusLine.getStatusCode();
	    LOG.info("Got status code " + responseStatusCode);

	    if (responseStatusCode < 200 || responseStatusCode > 299) {
		LOG.info("Skipping document because got no 2xx (maybe even after being redirected)");
	    } else if (entity.getContentLength() > ONE_MB
		    || entity.getContentType().getValue()
			    .contains("application/rdf+xml")
		    || entity.getContentType().getValue().contains("text/n3")
		    || entity.getContentType().getValue()
			    .contains("text/turtle")) {

		downloadedFile = new DownloadedFile(entity.getContent(), entity
			.getContentType().getValue());

	    }

	} catch (ClientProtocolException e) {

	    LOG.severe("ClientProtocolException");
	} catch (IOException e) {
	    LOG.severe("IOException");

	} catch (Exception e) {
	    LOG.log(Level.SEVERE, "can't fetch document", e);
	} finally {
	    try {
		if (response != null) {
		    response.close();
		}
	    } catch (IOException e) {
		LOG.warning("could not close response");
	    }
	}

	return downloadedFile;

    }
}
