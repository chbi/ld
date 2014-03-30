package ld.ldhomework.crawler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

public class Crawler {

    private int searchDepth;
    private String startUrl;

    private static final int DEFAULT_SEARCH_DEPTH = 2;

    // download only documents of maximum 1MB length
    private static final int MAXIMUM_DOCUMENT_LENGTH = 1024 * 1024;

    private static final Logger LOG = java.util.logging.Logger
	    .getLogger(Crawler.class.getName());
    private static final int SOCKET_TIMEOUT = 5000;
    private static final int CONNECT_TIMEOUT = 5000;

    // choosing Linked Blocking Queue even if this code will not support
    // multithreading
    private LinkedBlockingQueue<String> currentUriQueue = null;
    private LinkedBlockingQueue<String> nextUriQueue = null;
    HashSet<String> alreadyHandledURIs = null;
    private CloseableHttpClient httpclient;
    private TripleRepository repository;

    public Crawler(String startUrl) {
	this(DEFAULT_SEARCH_DEPTH, startUrl);
    }

    public Crawler(int searchDepth, String startUrl) {
	this.searchDepth = searchDepth;
	this.startUrl = startUrl;
	this.alreadyHandledURIs = new HashSet<String>();
	httpclient = HttpClients.createDefault();
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
		    String document = fetchDocument(currentURI);

		    if (document != null) {

			// TODO: error handling for document fetching
			// TODO: parse document
			FileParser parser = null;
			InputStream stream = null;
			try {
			    stream = new ByteArrayInputStream(
				    document.getBytes("UTF-8"));

			    parser = new FileParser(stream, currentURI);
			    parser.parse();

			    List<Triple> triples = parser.getTriples();

			    for (Triple triple : triples) {
				this.repository.add(triple);
				for (TripleEntry entry : triple
					.toTripleEntryArray()) {
				    if (entry.getType() == EntryType.IRI) {
					/*
					 * found IRI
					 */
					// TODO: find new URLs and add them to
					// this.nextUriQueue
					LOG.fine("found IRI:"
						+ entry.getValue());
					nextUriQueue.add(entry.getValue());
				    }
				}
			    }
			} catch (UnsupportedEncodingException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			} catch (IOException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			} finally {
			    if (stream != null) {
				try {
				    stream.close();
				} catch (IOException e) {
				    LOG.severe("Error closing internal stream!");
				}
			    }
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
    private String fetchDocument(String currentURI) {
	String result = null;
	HttpGet httpget = new HttpGet(currentURI);

	Builder configBuilder = RequestConfig.custom();
	configBuilder.setConnectTimeout(CONNECT_TIMEOUT);
	configBuilder.setSocketTimeout(SOCKET_TIMEOUT);
	configBuilder.setCircularRedirectsAllowed(false);
	httpget.setConfig(configBuilder.build());

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
	    LOG.info(currentURI + " cnttype: "
		    + response.getEntity().getContentType());
	    int responseStatusCode = statusLine.getStatusCode();
	    LOG.info("Got status code " + responseStatusCode);

	    if (responseStatusCode < 200 || responseStatusCode > 299) {
		// TODO: throw exception, we did not get the document
	    }

	    result = consumeToString(response.getEntity(), (Charset) null);

	    return result;
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

	return result;
    }

    public String printTriples() {
	String result = "ERROR: No triples in repository!";
	if (this.repository != null) {
	    result = this.repository.toString();
	}
	return result;
    }

    /**
     * Get the entity content as a String, using the provided default character
     * set if none is found in the entity. If defaultCharset is null, the
     * default "ISO-8859-1" is used.
     * 
     * @param entity
     *            must not be null
     * @param defaultCharset
     *            character set to be applied if none found in the entity
     * @return the entity content as a String. May be null if
     *         {@link HttpEntity#getContent()} is null.
     * @throws ParseException
     *             if header elements cannot be parsed
     * @throws IllegalArgumentException
     *             if entity is null or if content length > Integer.MAX_VALUE
     * @throws IOException
     *             if an error occurs reading the input stream
     * @throws UnsupportedCharsetException
     *             Thrown when the named charset is not available in this
     *             instance of the Java virtual machine
     */
    public static String consumeToString(final HttpEntity entity,
	    final Charset defaultCharset) throws IOException, ParseException {
	Args.notNull(entity, "Entity");
	final InputStream instream = entity.getContent();
	if (instream == null) {
	    return null;
	}
	try {
	    Args.check(entity.getContentLength() <= Integer.MAX_VALUE,
		    "HTTP entity too large to be buffered in memory");
	    int assumedContentLength = (int) entity.getContentLength();
	    if (assumedContentLength < 0) {
		assumedContentLength = 4096;
	    }
	    Charset charset = null;
	    try {
		final ContentType contentType = ContentType.get(entity);
		if (contentType != null) {
		    charset = contentType.getCharset();
		}
	    } catch (final UnsupportedCharsetException ex) {
		throw new UnsupportedEncodingException(ex.getMessage());
	    }
	    if (charset == null) {
		charset = defaultCharset;
	    }
	    if (charset == null) {
		charset = HTTP.DEF_CONTENT_CHARSET;
	    }
	    final Reader reader = new InputStreamReader(instream, charset);
	    final CharArrayBuffer buffer = new CharArrayBuffer(
		    assumedContentLength);
	    final char[] tmp = new char[1024];
	    int l = 0;
	    ExecutorService executor = Executors.newCachedThreadPool();

	    Future<Integer> future = null;
	    while (l != -1) {

		Callable<Integer> task = new Callable<Integer>() {
		    public Integer call() {
			Integer result = new Integer(-1);
			try {
			    result = new Integer(reader.read(tmp));
			} catch (IOException e) {
			    // TODO: use an ExecutionException
			    LOG.severe("I/O Error: " + e.getMessage());
			}
			return result;
		    }
		};
		future = executor.submit(task);
		try {
		    l = future.get(5, TimeUnit.SECONDS).intValue();
		    if (l != -1)
			buffer.append(tmp, 0, l);
		} catch (TimeoutException ex) {
		    // handle the timeout
		    l = -1;
		    throw new IOException("HTTP connection too slow. Aborting.");
		} catch (InterruptedException e) {
		    // handle the interrupts
		    l = -1;
		    throw new IOException("HTTP connection too slow. Aborting.");
		} catch (ExecutionException e) {
		    // handle other exceptions
		    l = -1;
		    throw new IOException(
			    "Something gone wrong while downloading. Aborting.");
		} finally {
		    future.cancel(true); // may or may not desire this
		}
	    }
	    return buffer.toString();
	} finally {
	    instream.close();
	}
    }

}
