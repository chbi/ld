package ld.ldhomework.crawler;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class Crawler {

    private static final int NUMBER_OF_DOWNLOAD_THREADS = 10;
    private int searchDepth;
    private String startUrl;

    private static final int DEFAULT_SEARCH_DEPTH = 2;

    private static final Logger LOG = java.util.logging.Logger
	    .getLogger(Crawler.class.getName());

    // choosing Linked Blocking Queue even if this code will not support
    // multithreading
    private LinkedBlockingQueue<String> currentUriQueue = null;
    private LinkedBlockingQueue<String> nextUriQueue = null;
    private HashSet<String> alreadyHandledURIs = null;

    private TripleRepository repository;

    private ExecutorService downloadExecutors = Executors
	    .newFixedThreadPool(NUMBER_OF_DOWNLOAD_THREADS);
    private ExecutorService parserExecutors = Executors
	    .newFixedThreadPool(NUMBER_OF_DOWNLOAD_THREADS);

    public Crawler(String startUrl) {
	this(DEFAULT_SEARCH_DEPTH, startUrl);
    }

    public Crawler(int searchDepth, String startUrl) {
	this.searchDepth = searchDepth;
	this.startUrl = startUrl;
	this.alreadyHandledURIs = new HashSet<String>();

	repository = new TripleRepository();

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

		    Future<DownloadedFile> submit = downloadExecutors
			    .submit(new DownloadTask(currentURI));

		    try {
			DownloadedFile downloadedFile = submit.get();
			if (downloadedFile != null) {

			    // TODO: parse document

			    FileParser parser = new FileParser(
				    downloadedFile.getContent(), currentURI,
				    downloadedFile.getContentType());
			    Future<List<Triple>> parseResult = parserExecutors
				    .submit(parser);

			    List<Triple> triples = parseResult.get();

			    for (Triple triple : triples) {
				this.repository.add(triple);
				for (TripleEntry entry : triple
					.toTripleEntryArray()) {
				    if (entry.getType() == EntryType.IRI) {
					/*
					 * found IRI
					 */
					// TODO: find new URLs and add them
					// to
					// this.nextUriQueue
					String value = entry.getValue();
					LOG.fine("found IRI:" + value);
					int index = value.indexOf('#');
					String downloadURI = value;
					if (index != -1) {
					    downloadURI = value.substring(0,
						    index);
					}
					if (!alreadyHandledURIs
						.contains(downloadURI))
					    nextUriQueue.add(downloadURI);
				    }
				}
			    }
			}

		    } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    } catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

    public String printTriples() {
	String result = "ERROR: No triples in repository!";
	if (this.repository != null) {
	    result = this.repository.toString();
	}
	return result;
    }
}
