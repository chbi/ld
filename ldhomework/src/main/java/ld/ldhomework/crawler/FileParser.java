package ld.ldhomework.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;
import org.semanticweb.yars.nx.parser.ParseException;
import org.semanticweb.yars2.rdfxml.RDFXMLParser;

public class FileParser {

    private static final Logger LOG = java.util.logging.Logger
	    .getLogger(TripleEntry.class.getName());

    private List<Triple> triples;

    private boolean parsed = false;
    private InputStream is;
    private String baseUrl;

    private String contentType;

    public FileParser(InputStream is, String baseUrl) {
	this(is, baseUrl, null);
    }

    public FileParser(InputStream is, String baseUrl, String contentType) {
	triples = new ArrayList<Triple>();
	this.baseUrl = baseUrl;
	this.is = is;
	this.contentType = contentType;
    }

    public void parse() throws IOException {
	if (contentType.equals("text/turtle")) {
	    parseNx();
	}

	if (contentType.equals("text/xml")) {
	    parseXml();
	}
    }

    private void parseNx() {
	NxParser nxp = new NxParser(is);
	nxp = new NxParser(is);

	Node[] nxx;
	while (nxp.hasNext()) {
	    nxx = nxp.next();
	    triples.add(new Triple(nxx[0], nxx[1], nxx[2]));
	}
	parsed = true;

    }

    private void parseXml() {
	RDFXMLParser nxp;
	try {

	    nxp = new RDFXMLParser(is, baseUrl);

	    Node[] nxx;
	    while (nxp.hasNext()) {
		nxx = nxp.next();
		triples.add(new Triple(nxx[0], nxx[1], nxx[2]));
	    }

	    parsed = true;
	} catch (ParseException pe) {
	    LOG.log(Level.WARNING, "Error at parsing file", pe);
	} catch (IOException e) {
	    LOG.log(Level.WARNING, "Error at parsing file", e);
	}
    }

    public List<Triple> getTriples() {
	if (parsed) {
	    return triples;
	}
	throw new IllegalStateException("file not yet parsed");
    }

}
