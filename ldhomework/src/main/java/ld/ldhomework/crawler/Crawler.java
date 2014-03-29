package ld.ldhomework.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private CloseableHttpClient httpclient;

    public Crawler(String startUrl) {
	this(DEFAULT_SEARCH_DEPTH, startUrl);
	// Todo: model =
    }

    public Crawler(int searchDepth, String startUrl) {
	this.searchDepth = searchDepth;
	this.startUrl = startUrl;
	this.alreadyHandledURIs = new HashSet<String>();
	httpclient = HttpClients.createDefault();
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

		LOG.info("Pop URI in currentQueue: " + currentURI);
		// only fetch this URI if it hasn't already been fetched
		if (alreadyHandledURIs.add(currentURI)) {
		    LOG.info("URI not handled yet. Fetching...");
		    // TODO: FETCH document
		    char[] responseDocument = fetchDocument(currentURI);
		    /*
		     * if (responseDocument != null)
		     * System.out.println(responseDocument);
		     */
		    // TODO: error handling for document fetching
		    // TODO: parse document
		    // TODO: error handling for document parsing
		    // create local model for document parsing? easier to find
		    // new URIs of current document?
		    // TODO: find new URLs and add them to this.nextUriQueue
		} else {
		    LOG.info("URI already fetched. Skipping.");
		}
	    }

	    LOG.info("SEARCH" + i);

	    if (nextUriQueue.isEmpty()) {
		LOG.info("No more links to follow. Finishing in SEARCH " + i);
		break;
	    }
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
    private char[] fetchDocument(String currentURI) {
	char[] result = null;
	HttpGet httpget = new HttpGet(currentURI);
	CloseableHttpResponse response = null;
	InputStream inputStream = null;
	try {
	    LOG.info("Executing GET for " + currentURI);
	    response = httpclient.execute(httpget);

	    /*
	     * Check the status code. 200 OK means we got the document.
	     * Everything else needs to be handled, though the standard
	     * HTTPClient Strategy includes automatic redirect handling
	     * (probably following infinite redirects).
	     */
	    StatusLine statusLine = response.getStatusLine();
	    int responseStatusCode = statusLine.getStatusCode();
	    LOG.info("Got status code " + responseStatusCode);

	    if (responseStatusCode < 200 || responseStatusCode > 299) {
		// TODO: throw exception, we did not get the document
	    }

	    HttpEntity entity = response.getEntity();
	    if (entity != null) {
		long len = entity.getContentLength();
		if (len > MAXIMUM_DOCUMENT_LENGTH)
		    throw new IOException(
			    "Maximum Document size will be exceeded according to the HTTP response header. Skipping this document.");

		Header encodingHeader = entity.getContentEncoding();
		Charset contentCharset = null;

		if (encodingHeader != null) {
		    contentCharset = CharsetUtils
			    .get(encodingHeader.getValue());
		} else {
		    contentCharset = Charset.defaultCharset();
		}

		LOG.info("Trying to read content in charset: "
			+ contentCharset.displayName());

		inputStream = entity.getContent();

		result = loadDocument(inputStream, contentCharset);

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

    private char[] loadDocument(InputStream inputStream, Charset contentCharset)
	    throws IOException {
	// TODO Auto-generated method stub
	InputStreamReader isr = new InputStreamReader(inputStream,
		contentCharset);
	char[] buffer = new char[MAXIMUM_DOCUMENT_LENGTH];
	boolean bufferExceeded = false;

	try {
	    int read = isr.read(buffer);
	    if (read == MAXIMUM_DOCUMENT_LENGTH && isr.ready()) {
		// buffer has been too small... throw exception
		bufferExceeded = true;
	    }
	    isr.close();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} finally {
	    if (isr != null) {
		try {
		    isr.close();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	}
	if (bufferExceeded) {
	    throw new IOException(
		    "Maximum Document size exceeded while already loading. Skipping this document.");
	}

	return buffer;
    }

}
