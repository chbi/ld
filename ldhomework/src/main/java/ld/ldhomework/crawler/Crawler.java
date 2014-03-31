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

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

/**
 * This class does Linked Data crawling. Setup with an initial URI it crawls the
 * networrk breadth-first until the specified search depth has been reached. All
 * triples are stored in an in-memory {@link TripleRepository}. The downloading
 * and parsing are handled by a number of concurrent threads with low timeouts
 * (for demonstration purposes). Downloads are very restricted in size too. For
 * details on the download process itself, see {@link DownloadTask}. For details
 * on the parsing process, see {@link FileParser}.
 * 
 * The main method to call is {@link Crawler#crawl()}. Result can the be
 * obtained as a String by calling {@link Crawler#printTriples()}.
 * 
 * @author worohregger
 * @author chb
 * 
 */
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

    /**
     * Construct a crawler with the specified start URL.
     * 
     * @param startUrl
     *            Start the crawling with the document at this location.
     */
    public Crawler(String startUrl) {
	this(DEFAULT_SEARCH_DEPTH, startUrl);
    }

    /**
     * Construct a crawler with the specified start URL. Set another search
     * depth.
     * 
     * @param searchDepth
     *            Use a custom search depth.
     * @param startUrl
     *            Start the crawling with the document at this location.
     */
    public Crawler(int searchDepth, String startUrl) {
	this.searchDepth = searchDepth;
	this.startUrl = startUrl;
	this.alreadyHandledURIs = new HashSet<String>();

	repository = new TripleRepository();

    }

    /**
     * Start the crawling. May take a long time.
     * 
     * @throws IllegalStateException
     */
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
						.contains(downloadURI)
						&& (downloadURI
							.startsWith("http://") || downloadURI
							.startsWith("https://")))
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

    /**
     * Print the triples obtained while crawling the network.
     * 
     * @return A String containing all unique triples obtained while crawling
     *         the network.
     */
    public String printTriples() {
	String result = "ERROR: No triples in repository!";
	if (this.repository != null) {
	    result = this.repository.toString();
	}
	return result;
    }
}
