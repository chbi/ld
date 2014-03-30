package ld.ldhomework.crawler;

import java.util.HashSet;
import java.util.logging.Logger;

public class TripleRepository {

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
