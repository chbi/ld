package ld.ldhomework;

import java.util.logging.Level;
import java.util.logging.Logger;

import ld.ldhomework.crawler.Crawler;

/**
 * This is a crawler that does a hop-2 breadth-first search of
 * http://www.w3.org/People/Berners-Lee/card.rdf, where card.rdf is hop-2
 * 
 * Used frameworks:
 * 
 * http://code.google.com/p/nxparser or http://jena.apache.org/
 * 
 * Submit by e-mail not later than 2014-03-31.
 * 
 * @author Christian Bitschnau
 */
public class Main {
    private static final Logger LOG = java.util.logging.Logger
	    .getLogger(Crawler.class.getName());

    static {
	System.setProperty("java.util.logging.SimpleFormatter.format",
		"%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n");
    }

    public static void main(String[] args) {


	Crawler crawler = null;
	if (args.length == 1) {
	    crawler = new Crawler(args[0]);
	} else {
	    // use the followning construct to get an easy way to test urls from
	    // the IDE - set startUrl to different value ;)
	    String startUrl = "http://www.w3.org/People/Berners-Lee/card.rdf";
	    crawler = new Crawler(startUrl);
	}

	try {
	    crawler.crawl();
	    String result = crawler.printTriples();
	    System.out.println(result);
	} catch (IllegalStateException e) {
	    LOG.log(Level.SEVERE, "IllegalState", e);
	}

	LOG.info("THE END");
    }
}
