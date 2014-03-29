package ld.ldhomework.crawler;

import java.util.logging.Logger;

import org.semanticweb.yars.nx.BNode;
import org.semanticweb.yars.nx.Literal;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.Resource;

public class TripleEntry {

    private static final Logger LOG = java.util.logging.Logger
	    .getLogger(TripleEntry.class.getName());

    private String value;
    private EntryType type;

    public TripleEntry(Node n) {
	this.value = n.toString();
	this.type = discoverType(n);
    }

    private EntryType discoverType(Node n) {
	// TODO: add regexes here for iri and blank nodes
	if (n instanceof Resource) { // match Regex IRI
	    return EntryType.IRI;
	} else if (n instanceof BNode) { // match Regex blank
	    return EntryType.BLANK;
	} else if (n instanceof Literal) {
	    return EntryType.LITERAL;
	} else {
	    LOG.warning("that is not Resource or BlankNode or Literal (=> Unbound or Variable");
	    throw new IllegalStateException(
		    "Entry type that is not Resource or BlankNode or Literal (=> Unbound or Variable)");
	}
    }

    public String getValue() {
	return value;
    }

    public EntryType getType() {
	return type;
    }

}
