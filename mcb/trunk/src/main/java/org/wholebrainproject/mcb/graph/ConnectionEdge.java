package org.wholebrainproject.mcb.graph;

import java.awt.BasicStroke;
import java.awt.Font;
import java.util.EnumSet;

import org.apache.commons.collections15.multimap.MultiHashMap;
import org.wholebrainproject.mcb.util.SparqlQuery;

import edu.uci.ics.jung.graph.util.Pair;

/*Copyright (C) 2010 contact@wholebraincatalog.org
 *
 * Whole Brain Catalog is Licensed under the GNU Lesser Public License (LGPL), Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the license at
 *
 * http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The class is used to create the edges for the graph.  This class is implemented 
 * by Multi-Scale Connectome Browser.
 * @date    December 10, 2009
 * @author  Ruggero Carloz
 * @version 0.0.1
 */
public class ConnectionEdge implements Edge{
	
	public enum STRENGTH {
		NOT_PRESENT("not present"),
		NOT_CLEAR("not clear"),
		EXISTS("exists"),
		VERY_LIGHT("very light"),
		LIGHT("light"),
		MODERATE("moderate"),
		STRONG_MODERATE("moderate/strong"),
		STRONG("strong");
		
		String text; 
        
        STRENGTH(String msg) {
                this.text = msg;
        }
        
        public String toString() {
                return this.text;
        }
        
        public static STRENGTH myValueOf(Object s) {
                //return MENU_STRING based on its toString() value
                for (STRENGTH ms : EnumSet.allOf(STRENGTH.class)) {
                        if (ms.toString().equals(s)) {
                                return ms;
                        }
                }
                return null;
        }
	}
	
	/**
	 * Possible edge data
	 */
	private STRENGTH strength;

	private String reference;

	/**
	 * Default constructor.
	 */
	public ConnectionEdge(){
		
	}
	/**
	 * Default constructor
	 */
	public ConnectionEdge(String strength, String reference) {
		this.strength = STRENGTH.myValueOf(strength);
		this.reference = reference;
	}
	
	public STRENGTH getStrength() {
		return this.strength;
	}
	
	public String getReference(){
		return this.reference;
	}
	
	private Node getProjectingNode() {
		Pair<Node> endpoints = 
			GraphManager.getInstance().getGraph().getEndpoints(this);
		if (endpoints == null) {
			GraphManager.getInstance().getGraph().removeEdge(this);
			return null;
		}
		return endpoints.getFirst();
	}
	
	public String getLabel() {
		String s = "";
		if (getProjectingNode() != null) {
			s = getProjectingNode().getProjectingCellsRoleAbbrevString();
		}
		return s;
	}
	
	public String getMoreDetailsURL() {
		return "http://" + reference;
	}
	
	public String getToolTipLabel() {
		String out = "";
		String reference = getReference();
		out += "<html>Projection strength: " + getStrength() + "<br>";
		if ("".equals(getProjectingNode().getProjectingCellsRoleString()) == false) {
			out += "This projection is inferred to be "
					+ getProjectingNode().getProjectingCellsRoleString()
					+ "<br>";
		}
		out += "This projection is described in "
				+ reference.subSequence(0, 20) + "...<br>"
				+ "(right-click for more)</html>";
		return out;
	}
	
	public Font getFont() {
		int style = Font.BOLD | Font.ITALIC;

		Font font = new Font ("Arial", style , 15);
		
		return font;
	}
	
	public BasicStroke getStroke() {
		float dash[] = {10.0f};
		switch (getStrength()) {
		case EXISTS:
			return new BasicStroke(0.5f, BasicStroke.CAP_BUTT, 
					BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
		case VERY_LIGHT:
			return new BasicStroke(1f);
		case LIGHT:
			return new BasicStroke(2f);
		case MODERATE:
			return new BasicStroke(3f);
		}
		return new BasicStroke(2.5f);
	}
	
	public Number getCloseness() {
		return 0.9f;
	}
	
	private String getTitleFromReference() {
		String reference = getReference();
		int startIndex = reference.indexOf(": ");
		int endIndex = reference.indexOf("..");
		int endIndex2 = reference.indexOf("(");
		if ((endIndex2 > 0) && (endIndex > endIndex2)) endIndex = endIndex2; 
		return reference.substring(startIndex+2, endIndex);
	}
	
	public String getReferenceURL() {
		SparqlQuery test = new SparqlQuery("http://api.talis.com/stores/neurolex/services/sparql");
		/*
		 * select ?d where {?a <http://brancusi1.usc.edu/RDF/title> ?c . 
		 * ?a <http://brancusi1.usc.edu/RDF/url> ?d 
		 * FILTER regex(?c, "The neocortical projection to the inferior colliculus in the albino rat")}
		 */
		test.addQueryTriplet("?a <http://brancusi1.usc.edu/RDF/title> ?c");
		test.addQueryTriplet("?a <http://brancusi1.usc.edu/RDF/url> ?d");
		test.addQueryTriplet("FILTER regex(?c, \"" + getTitleFromReference() + 
				"\")");
		test.addSelectVariable("?d");
		MultiHashMap<String, String> results = test.runSelectQuery();
		if (results.get("?d") != null) {
			String initialResults = results.get("?d").iterator().next();
			initialResults = initialResults.replace("http://www.ncbi.nmm.nih.gov", 
					"http://www.ncbi.nlm.nih.gov");
			return initialResults;
		}
		return "http://brancusi1.usc.edu/";
	}
	
	public boolean hasInferenceChain() {
		return getProjectingNode().getCellCount() > 0;
	}
	
	public String getInferenceChain() {
		String out = "<html><h1>Why is this an " + 
		getProjectingNode().getProjectingCellsRoleString() + 
		" projection?<h1>" +
		"<ul><li>This projection comes from " + getProjectingNode().getName() + 
		"</li><li>" + getProjectingNode().getName() + " has <a href=\""+ 
		getProjectingNode().getMoreDetailURL() + "\">" + 
		getProjectingNode().getUniqueCellCount() + " projection cell(s)</a>" + 
		"<ul>";
		for (int i = 0; i < getProjectingNode().getCellCount(); i++) {
			out += "<li><a href=\"" + getProjectingNode().getCellUrl(i) + "\">" 
			+ getProjectingNode().getCellName(i) + "</a>";
			out += "<ul><li>This cell has " + 
			getProjectingNode().getNeurotransmitter(i) + " as its neurotransmitter.";
			out += "<li>" + getProjectingNode().getNeurotransmitter(i) +" is an " +
			getProjectingNode().getRole(i);
			out += "</ul>";
		}
		out += "</ul>";
		
		out += "</ul></html>";
		
		return out;
	}
	
}