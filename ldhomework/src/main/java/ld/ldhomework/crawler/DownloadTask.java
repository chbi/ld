package ld.ldhomework.crawler;

import java.io.IOException;
import java.util.concurrent.Callable;
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

public class DownloadTask implements Callable<DownloadedFile> {

    private String currentURI;
    private CloseableHttpClient httpClient;

    private static final int SOCKET_TIMEOUT = 5000;
    private static final int CONNECT_TIMEOUT = 5000;

    private static final Logger LOG = java.util.logging.Logger
	    .getLogger(DownloadTask.class.getName());
    private static final long ONE_MB = 1024 * 1024;

    public DownloadTask(String uri) {
	this.currentURI = uri;
	httpClient = HttpClients.createDefault();
    }

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
		// TODO: throw exception, we did not get the document
	    }

	    if (entity.getContentLength() > ONE_MB
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
