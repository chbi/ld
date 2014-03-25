package ld.ldhomework.crawler;

public class TripleEntry {

    private String value;
    private EntryType type;

    public TripleEntry(String value) {
	this.value = value;
	this.type = discoverType(value);
    }

    private EntryType discoverType(String value2) {
	
	if (false) { // match Regex IRI
	    return EntryType.IRI;
	} else if (false) { // match Regex blank
	    return EntryType.BLANK;
	} else { // all others
	    return EntryType.LITERAL;
	}
	
	
    }

}
