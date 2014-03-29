package ld.ldhomework.crawler;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

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

    public void crawl() throws IllegalStateException {

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
		    CloseableHttpResponse document = fetchDocument(currentURI);

		    // TODO: error handling for document fetching
		    // TODO: parse document
		    FileParser parser;
		    try {
			parser = new FileParser(document.getEntity()
				.getContent(), currentURI);
			parser.parse();

			List<Triple> triples = parser.getTriples();

			for (Triple triple : triples) {
			    for (TripleEntry entry : triple
				    .toTripleEntryArray()) {
				if (entry.getType() == EntryType.IRI) {
				    /*
				     * found IRI
				     */
				    // TODO: find new URLs and add them to
				    // this.nextUriQueue
				    LOG.fine("found IRI:" + entry.getValue());
				    nextUriQueue.add(entry.getValue());
				}
			    }
			}
		    } catch (IOException e) {
			LOG.log(Level.SEVERE, "", e);
		    } finally {
			try {
			    document.close();
			} catch (IOException e) {
			    LOG.log(Level.SEVERE, "Could not close file", e);
			}
		    }

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

    }

    // TODO: HTTP status handling...
    // TODO: throw exception if unrecoverable error
    private CloseableHttpResponse fetchDocument(String currentURI) {
	HttpGet httpget = new HttpGet(currentURI);
	CloseableHttpResponse response = null;
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

	    return response;
	} catch (ClientProtocolException e) {

	    e.printStackTrace();
	} catch (IOException e) {

	    e.printStackTrace();
	} finally {
	    try {
		response.close();
	    } catch (IOException e) {
		LOG.warning("could not close response");
	    }
	}

	return response;
    }

}
