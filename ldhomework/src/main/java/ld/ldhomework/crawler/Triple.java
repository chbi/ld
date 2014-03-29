package ld.ldhomework.crawler;

public class Triple {
    private TripleEntry subject;
    private TripleEntry predicate;
    private TripleEntry object;

    public Triple(String subject, String predicate, String object) {
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

    @Override
    public String toString() {
	return "[" + subject.getValue() + ", " + predicate.getValue() + ", "
		+ object.getValue() + "]";
    }


}
