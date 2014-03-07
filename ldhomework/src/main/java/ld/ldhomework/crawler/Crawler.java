package ld.ldhomework.crawler;

import java.util.logging.Logger;

public class Crawler {

	private int searchDepth;
	
	private static final int DEFAULT_SEARCH_DEPTH = 2;
	
	private static final Logger LOG = java.util.logging.Logger.getLogger(Crawler.class.getName());
	
	public Crawler() {
		this(DEFAULT_SEARCH_DEPTH);
	}
	
	public Crawler(int searchDepth) {
		this.searchDepth = searchDepth;
	}
	
	public void crawl() {
		
		for (int i = 0; i < searchDepth; i++) {
			LOG.info("SEARCH" + i);
		}
		
//		input: Seed set S
//		queue Frontier : = co ( S )
//		set Visited;
//		while Frontier not empty:
//			pop next element from Frontier into Uri
//			Visited : = Visited ∪ Uri
//			HTTP GET Uri and store triples in Data
//			output Data
//			Links : = co ( iris ( Data ))
//			add Links − Visited to Frontier

		
		return;
	}

}
