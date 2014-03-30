package ld.ldhomework.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.ParseException;
import org.semanticweb.yars2.rdfxml.RDFXMLParser;

public class FileParser {

    private static final Logger LOG = java.util.logging.Logger
	    .getLogger(TripleEntry.class.getName());

    private RDFXMLParser nxp;
    private List<Triple> triples;

    private boolean parsed = false;
    private InputStream is;
    private String baseUrl;


    public FileParser(InputStream is, String baseUrl) {
	triples = new ArrayList<Triple>();
	this.baseUrl = baseUrl;
	this.is = is;
    }

    public boolean parse() throws IOException {
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
	}

	return parsed;
    }

    public List<Triple> getTriples() {
	if (parsed) {
	    return triples;
	}
	throw new IllegalStateException("file not yet parsed");
    }


}
