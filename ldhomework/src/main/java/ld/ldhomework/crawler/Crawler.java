package ld.ldhomework.crawler;

import java.util.logging.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.impl.InfModelImpl;

public class Crawler {

	private int searchDepth;
	private String startUrl;
	
	private static final int DEFAULT_SEARCH_DEPTH = 2;
	
	private static final Logger LOG = java.util.logging.Logger.getLogger(Crawler.class.getName());
	
	private Model model;
	
	public Crawler(String startUrl) {
		this(DEFAULT_SEARCH_DEPTH, startUrl);
		// Todo model = 
	}
	
	public Crawler(int searchDepth, String startUrl) {
		this.searchDepth = searchDepth;
		this.startUrl = startUrl;
	}
	
	public void crawl() {
		
		for (int i = 0; i < searchDepth; i++) {
			LOG.info("SEARCH" + i);
		}
		
		LOG.info("startUrl = " + startUrl);
		
		model.read(startUrl);
		
		// System.out.println(model.toString());
		
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
