package ld.ldhomework.crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class TripleRepository {

    private static final Logger LOG = java.util.logging.Logger
	    .getLogger(TripleRepository.class.getName());

    private List<Triple> triples;

    public TripleRepository() {
	triples = new ArrayList<Triple>();
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
