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

package ld.ldhomework.crawler;

import java.util.logging.Logger;

import org.semanticweb.yars.nx.BNode;
import org.semanticweb.yars.nx.Literal;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.Resource;

public class TripleEntry {

    private static final Logger LOG = java.util.logging.Logger
	    .getLogger(TripleEntry.class.getName());

    public static boolean isSameTripleEntry(TripleEntry tripleEntry1,
	    TripleEntry tripleEntry2) {
	boolean result = false;
	if (tripleEntry1 == null && tripleEntry2 == null) {
	    result = true;
	} else if (tripleEntry1 != null) {
	    result = tripleEntry1.equals(tripleEntry2);
	}
	return result;
    }

    private int hashCode = 0;

    private boolean hashCodeValid = false;

    private EntryType type;

    private String value;

    public TripleEntry(Node n) {
	this.value = n.toString();
	this.type = discoverType(n);
    }

    private EntryType discoverType(Node n) {
	// TODO: add regexes here for iri and blank nodes
	if (n instanceof Resource) { // match Regex IRI
	    return EntryType.IRI;
	} else if (n instanceof BNode) { // match Regex blank
	    return EntryType.BLANK;
	} else if (n instanceof Literal) {
	    return EntryType.LITERAL;
	} else {
	    LOG.warning("that is not Resource or BlankNode or Literal (=> Unbound or Variable");
	    throw new IllegalStateException(
		    "Entry type that is not Resource or BlankNode or Literal (=> Unbound or Variable)");
	}
    }

    @Override
    public boolean equals(Object obj) {
	boolean result = false;

	if (obj != null) {
	    if (obj instanceof TripleEntry) {
		TripleEntry other = (TripleEntry) obj;

		// both object could be invalid tripels
		// we do not know if this is the case, so compare everything
		// even null values
		boolean entryTypeMatches = false;
		EntryType otherEntryType = other.getType();
		if (this.getType() == null && otherEntryType == null) {
		    entryTypeMatches = true;
		} else if (this.getType() != null) {
		    entryTypeMatches = this.getType().equals(otherEntryType);
		}

		if (entryTypeMatches) {
		    boolean valueMatches = false;
		    String value = this.getValue();
		    String otherValue = other.getValue();
		    if (value == null && otherValue == null) {
			valueMatches = true;
		    } else if (value != null) {
			valueMatches = value.equals(otherValue);
		    }

		    // now both value and entryType could match and equals is
		    // maybe true
		    result = valueMatches;
		}
	    }
	}

	return result;
    }

    public EntryType getType() {
	return type;
    }

    public String getValue() {
	return value;
    }

    @Override
    public int hashCode() {
	if (hashCodeValid)
	    return this.hashCode;
	else {
	    String value = this.getValue();
	    int valueCode = 0;
	    if (value != null) {
		valueCode = value.hashCode();
	    }
	    int typeValueCode = this.getType().ordinal();
	    this.hashCode = valueCode + typeValueCode;
	    this.hashCodeValid = true;
	    return this.hashCode;
	}
    }

}
