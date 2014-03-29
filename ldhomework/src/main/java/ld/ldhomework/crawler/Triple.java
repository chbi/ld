package ld.ldhomework.crawler;

import org.semanticweb.yars.nx.Node;

public class Triple {
    private TripleEntry subject;
    private TripleEntry predicate;
    private TripleEntry object;

    public Triple(Node subject, Node predicate, Node object) {
	this.subject = new TripleEntry(subject);
	this.predicate = new TripleEntry(predicate);
	this.object = new TripleEntry(object);
    }

    public TripleEntry getSubject() {
	return subject;
    }

    public TripleEntry getPredicate() {
	return predicate;
    }

    public TripleEntry getObject() {
	return object;
    }

    public TripleEntry[] toTripleEntryArray() {
	TripleEntry[] array = { subject, predicate, object };
	return array;
    }

    @Override
    public String toString() {
	return "[" + subject.getValue() + ", " + predicate.getValue() + ", "
		+ object.getValue() + "]";
    }


}
