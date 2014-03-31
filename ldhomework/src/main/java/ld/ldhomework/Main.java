/*
 * Copyright 2014 Christian Bitschnau, Wolfgang Rohregger

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

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
 * http://code.google.com/p/nxparser
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
