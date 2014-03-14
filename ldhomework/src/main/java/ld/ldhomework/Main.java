package ld.ldhomework;

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
	public static void main(String[] args) {
		Crawler crawler = new Crawler(
				"http://www.w3.org/People/Berners-Lee/card.rdf");
		crawler.crawl();
	}
}
