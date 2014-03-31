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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;
import org.semanticweb.yars.nx.parser.ParseException;
import org.semanticweb.yars2.rdfxml.RDFXMLParser;

public class FileParser implements Callable<List<Triple>> {

    private static final Logger LOG = java.util.logging.Logger
	    .getLogger(TripleEntry.class.getName());

    private List<Triple> triples;

    private InputStream is;
    private String baseUrl;
    private String contentType;

    public FileParser(InputStream is, String baseUrl, String contentType) {
	triples = new ArrayList<Triple>();
	this.baseUrl = baseUrl;
	this.is = is;
	this.contentType = contentType;
    }

    public void parse() { // TODO: add the right contentTypes here!
	if (contentType.contains("application/rdf+xml")) {
	    parseXML();
	} else if (contentType.contains("text/n3")) {
	    parseN3();
	} else if (contentType.contains("text/turtle")) {
	    parseN3();
	}
    }

    private void parseN3() {
	try {
	    NxParser nxp = new NxParser(is);
	    Node[] nxx;
	    while (nxp.hasNext()) {
		nxx = nxp.next();
		triples.add(new Triple(nxx[0], nxx[1], nxx[2]));
	    }
	} catch (Throwable e) {
	    LOG.severe("parsing error");
	    LOG.severe(contentType);
	}
    }

    public void parseXML() {
	try {
	    RDFXMLParser nxp = new RDFXMLParser(is, baseUrl);

	    Node[] nxx;
	    while (nxp.hasNext()) {
		nxx = nxp.next();
		triples.add(new Triple(nxx[0], nxx[1], nxx[2]));
	    }

	} catch (ParseException pe) {
	    LOG.log(Level.WARNING, "Error at parsing file", pe);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public List<Triple> call() throws Exception {
	parse();
	return triples;
    }

}
