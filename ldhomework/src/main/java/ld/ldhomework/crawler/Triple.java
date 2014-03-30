package ld.ldhomework.crawler;

import org.semanticweb.yars.nx.Node;

public class Triple {

    private int hashCode = 0;
    private boolean hashCodeValid = false;

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

    @Override
    public int hashCode() {
	if (this.hashCodeValid) {
	    return this.hashCode;
	} else {
	    int hashCodeSubject = 0;
	    int hashCodePredicate = 0;
	    int hashCodeObject = 0;

	    TripleEntry entrySubject = this.getSubject();
	    if (entrySubject != null) {
		hashCodeSubject = entrySubject.hashCode() / 3;
	    }

	    TripleEntry entryPredicate = this.getPredicate();
	    if (entryPredicate != null) {
		hashCodePredicate = entryPredicate.hashCode() / 3;
	    }

	    TripleEntry entryObject = this.getObject();
	    if (entryObject != null) {
		hashCodeObject = entryObject.hashCode() / 3;
	    }

	    this.hashCode = hashCodeSubject + hashCodePredicate
		    + hashCodeObject;
	    this.hashCodeValid = true;

	    return this.hashCode;
	}
    }

    private boolean isSameObject(TripleEntry otherObject) {
	boolean result = false;
	TripleEntry ourObject = this.getObject();

	result = TripleEntry.isSameTripleEntry(ourObject, otherObject);

	return result;
    }

    private boolean isSamePredicate(TripleEntry otherPredicate) {
	boolean result = false;
	TripleEntry ourPredicate = this.getPredicate();

	result = TripleEntry.isSameTripleEntry(ourPredicate, otherPredicate);

	return result;
    }

    private boolean isSameSubject(TripleEntry otherSubject) {
	boolean result = false;
	TripleEntry ourSubject = this.getSubject();

	result = TripleEntry.isSameTripleEntry(ourSubject, otherSubject);

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
