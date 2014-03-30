package ld.ldhomework.crawler;

import org.semanticweb.yars.nx.Node;

public class Triple {

    private TripleEntry object;

    private TripleEntry predicate;

    private TripleEntry subject;

    public Triple(Node subject, Node predicate, Node object) {
	this.subject = new TripleEntry(subject);
	this.predicate = new TripleEntry(predicate);
	this.object = new TripleEntry(object);
    }

    @Override
    public boolean equals(Object obj) {
	boolean result = false;

	if (obj instanceof Triple) {
	    Triple otherTriple = (Triple) obj;

	    TripleEntry otherSubject = otherTriple.getSubject();
	    TripleEntry otherPredicate = otherTriple.getPredicate();
	    TripleEntry otherObject = otherTriple.getObject();

	    boolean isSameSubject = isSameSubject(otherSubject);
	    boolean isSamePredicate = isSamePredicate(otherPredicate);
	    boolean isSameObject = isSameObject(otherObject);

	    result = isSameSubject && isSamePredicate && isSameObject;
	}

	return result;
    }

    public TripleEntry getObject() {
	return object;
    }

    public TripleEntry getPredicate() {
	return predicate;
    }

    public TripleEntry getSubject() {
	return subject;
    }

    private boolean isSameObject(TripleEntry otherObject) {
	boolean result = false;
	TripleEntry ourObject = this.getObject();

	result = isSameTripleEntry(ourObject, otherObject);

	return result;
    }

    private boolean isSamePredicate(TripleEntry otherPredicate) {
	boolean result = false;
	TripleEntry ourPredicate = this.getPredicate();

	result = isSameTripleEntry(ourPredicate, otherPredicate);

	return result;
    }

    private boolean isSameSubject(TripleEntry otherSubject) {
	boolean result = false;
	TripleEntry ourSubject = this.getSubject();

	result = isSameTripleEntry(ourSubject, otherSubject);

	return result;
    }

    private boolean isSameTripleEntry(TripleEntry tripleEntry1,
	    TripleEntry tripleEntry2) {
	boolean result = false;
	if (tripleEntry1 == null && tripleEntry2 == null) {
	    result = true;
	} else if (tripleEntry1 != null) {
	    result = tripleEntry1.equals(tripleEntry2);
	}
	return result;
    }

    @Override
    public String toString() {
	return "[" + subject.getValue() + ", " + predicate.getValue() + ", "
		+ object.getValue() + "]";
    }

    public TripleEntry[] toTripleEntryArray() {
	TripleEntry[] array = { subject, predicate, object };
	return array;
    }

}
