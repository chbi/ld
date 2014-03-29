package ld.ldhomework.crawler;

import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.ParseException;
import org.semanticweb.yars2.rdfxml.RDFXMLParser;

public class NxparserTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test() throws IOException, ParseException {
	FileInputStream is = new FileInputStream("card.rdf");

	RDFXMLParser nxp = new RDFXMLParser(is, "http:/drTitan.info");

	Node[] nxx;
	while (nxp.hasNext()) {

	    nxx = nxp.next();

	    System.out.println(nxx[0].toN3());
	    System.out.println("\t" + nxx[1].toN3());
	    System.out.println("\t\t" + nxx[2].toN3());
	}

    }
}
