package ld.ldhomework.crawler;

import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import com.hp.hpl.jena.rdf.model.Model;

public class Crawler {

    private int searchDepth;
    private String startUrl;

    private static final int DEFAULT_SEARCH_DEPTH = 2;

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

	for (int i = 0; i < searchDepth; i++) {
	    currentUriQueue = nextUriQueue;
	    nextUriQueue = new LinkedBlockingQueue<String>();

	    while (!currentUriQueue.isEmpty()) {
		String currentURI = currentUriQueue.poll();

		// only fetch this URI if it hasn't already been fetched
		if (alreadyHandledURIs.add(currentURI)) {
		    // TODO: FETCH document
		    // TODO: error handling for document fetching
		    // TODO: parse document
		    // TODO: error handling for document parsing
		    // create local model for document parsing? easier to find
		    // new URIs of current document?
		    model.read(startUrl);
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

}
