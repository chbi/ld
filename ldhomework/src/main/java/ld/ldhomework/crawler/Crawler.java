package ld.ldhomework.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.CharsetUtils;

import com.hp.hpl.jena.rdf.model.Model;

public class Crawler {

    private int searchDepth;
    private String startUrl;

    private static final int DEFAULT_SEARCH_DEPTH = 2;

    // download only documents of maximum 10MB length
    private static final int MAXIMUM_DOCUMENT_LENGTH = 10485760;

    private static final Logger LOG = java.util.logging.Logger
	    .getLogger(Crawler.class.getName());

    // choosing Linked Blocking Queue even if this code will not support
    // multithreading
    private LinkedBlockingQueue<String> currentUriQueue = null;
    private LinkedBlockingQueue<String> nextUriQueue = null;
    HashSet<String> alreadyHandledURIs = null;

    private Model model;

    public Crawler(String startUrl) {
	this(DEFAULT_SEARCH_DEPTH, startUrl);
	// Todo: model =
    }

    public Crawler(int searchDepth, String startUrl) {
	this.searchDepth = searchDepth;
	this.startUrl = startUrl;
	this.alreadyHandledURIs = new HashSet<String>();
    }

    public void crawl() {

	LOG.info("startUrl = " + startUrl);

	// "initial" run zero, prepare queue
	currentUriQueue = null;
	nextUriQueue = new LinkedBlockingQueue<String>();
	nextUriQueue.add(startUrl);

	for (int i = 0; i < searchDepth + 1; i++) {
	    currentUriQueue = nextUriQueue;
	    nextUriQueue = new LinkedBlockingQueue<String>();

	    while (!currentUriQueue.isEmpty()) {
		String currentURI = currentUriQueue.poll();

		// only fetch this URI if it hasn't already been fetched
		if (alreadyHandledURIs.add(currentURI)) {
		    // TODO: FETCH document
		    String responseString = fetchDocument(currentURI);
		    // TODO: error handling for document fetching
		    // TODO: parse document
		    // TODO: error handling for document parsing
		    // create local model for document parsing? easier to find
		    // new URIs of current document?
		    // TODO: find new URLs and add them to this.nextUriQueue
		}
	    }

	    LOG.info("SEARCH" + i);
	}

	// System.out.println(model.toString());

	// input: Seed set S
	// queue Frontier : = co ( S )
	// set Visited;
	// while Frontier not empty:
	// pop next element from Frontier into Uri
	// Visited : = Visited ∪ Uri
	// HTTP GET Uri and store triples in Data
	// output Data
	// Links : = co ( iris ( Data ))
	// add Links − Visited to Frontier

	return;
    }

    // TODO: HTTP status handling...
    // TODO: throw exception if unrecoverable error
    private String fetchDocument(String currentURI) {
	String result = null;
	CloseableHttpClient httpclient = HttpClients.createDefault();
	HttpGet httpget = new HttpGet(currentURI);
	CloseableHttpResponse response = null;
	InputStream inputStream = null;
	try {
	    response = httpclient.execute(httpget);

	    /*
	     * Check the status code. 200 OK means we got the document.
	     * Everything else needs to be handled, though the standard
	     * HTTPClient Strategy includes automatic redirect handling
	     * (probably following infinite redirects).
	     */
	    StatusLine statusLine = response.getStatusLine();
	    int responseStatusCode = statusLine.getStatusCode();

	    if (responseStatusCode < 200 || responseStatusCode > 299) {
		// TODO: throw exception, we did not get the document
	    }


	    HttpEntity entity = response.getEntity();
	    if (entity != null) {
		long len = entity.getContentLength();

		Header encodingHeader = entity.getContentEncoding();
		Charset contentCharset = null;

		if (encodingHeader != null) {
		    contentCharset = CharsetUtils
			    .get(encodingHeader.getValue());
		} else {
		    contentCharset = Charset.defaultCharset();
		}

		inputStream = entity.getContent();
		Object object = parseDocument(inputStream, contentCharset);

	    }
	} catch (ParseException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} finally {
	    if (inputStream != null) {
		try {
		    inputStream.close();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	    if (response != null) {
		try {
		    response.close();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	}
	return result;
    }

    private Object parseDocument(InputStream inputStream, Charset contentCharset) {
	// TODO Auto-generated method stub
	return null;
    }

}
