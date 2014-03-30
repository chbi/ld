package ld.ldhomework.crawler;

import java.util.HashSet;
import java.util.logging.Logger;

public class TripleRepository {

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder(triples.size() * 256);
	for (Triple triple : triples) {
	    builder.append(triple.toString());
	    builder.append('\n');
	}
	return builder.toString();
    }

    private static final Logger LOG = java.util.logging.Logger
	    .getLogger(TripleRepository.class.getName());

    private HashSet<Triple> triples;

    public TripleRepository() {
	triples = new HashSet<Triple>();
    }

    public void add(Triple t) {
	triples.add(t);
    }

    public void print(int limit) {
	LOG.info("There are " + triples.size() + " triples in the repository");
	for (Triple triple : triples) {
	    LOG.info(triple.toString());
	}
    }

    public int size() {
	return triples.size();
    }

}
