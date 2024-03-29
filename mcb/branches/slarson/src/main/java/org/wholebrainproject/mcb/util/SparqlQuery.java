package org.wholebrainproject.mcb.util;

/* Copyright (C) 2010 contact@wholebraincatalog.org
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
 * limitations under the License
 */

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.collections15.multimap.MultiHashMap;



/**
 * Executes a SPARQL query against a specified end point.  Can't be reused, so
 * throw it away after you get your query results out of it.
 *
 * Provides abstractions to automate query construction.
 *
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class SparqlQuery
{
	public static final String UNION = "} UNION {";

	/**
	 * The URL of the sparql end point that will be queries.
	 */
	private String sparqlEndPointURL = null;

	/**
	 * The list of triplets that will be used to query.
	 */
	private List<String> queryTriplets = null;

	/**
	 * The list of variables to select with.
	 */
	private List<String> variableList = null;

	/**
	 * A map of RDF prefixes mapped to their URIs.
	 */
	private Map<String,String> prefixMap = null;

	/**
	 * Constructor for SparqlQuery
	 * @param sparqlEndPoint - the URL of the SPARQL
	 * end point you wish to execute queries against.
	 */
	public SparqlQuery(String sparqlEndPoint) {
		this.sparqlEndPointURL = sparqlEndPoint;
		this.queryTriplets = new ArrayList<String>();
		this.variableList = new ArrayList<String>();
		this.prefixMap = new HashMap<String,String>();
	}

	/**
	 * Adds one triplet line to a SPARQL query.  Variables
	 * that are used here will need to be included as
	 * an argument to {@link #runSelectQuery(String)} when
	 * executed.
	 * @param queryTriplet - a triplet separated by spaces that
	 *  is part of a SPARQL query,
	 *  such as $s <http://someurl/property> $y. NOTE:
	 * you can include "} UNION {" between sets of queryTriplets.
	 */
	public void addQueryTriplet(String queryTriplet) {
		queryTriplets.add(queryTriplet);
	}

	/**
	 * Get the string of the URL of the SPARQL endpoint
	 * that is being used.
	 * @return - the sparql end point URL
	 */
	public String getSparqlEndPoint() {
		return this.sparqlEndPointURL;
	}

	/**
	 * Add a variable to select output from.
	 * @param selectVariable
	 */
	public void addSelectVariable(String selectVariable) {
		variableList.add(selectVariable);
	}

	/**
	 * Add prefix mapping to the SPARQL query.  The prefix can then be used
	 * in {@link #addQueryTriplet(String)}.
	 * @param prefix
	 * @param uri - must begin and end with angle brackets
	 */
	public void addPrefixMapping(String prefix, String uri) {
		if (uri.startsWith("<") == false || uri.endsWith(">") == false) {
			throw new IllegalArgumentException();
		}
		prefixMap.put(prefix, uri);
	}

	/**
	 * Put the queryTriplet list together into a SPARQL query
	 * @return - a string containing a SPARQL query
	 * @see #addQueryTriplet(String)
	 * @see #addSelectVariable(String)
	 */
	protected String getComposedQuery() {

		if (this.variableList.isEmpty()) {
			throw new IllegalArgumentException("Can't compose a query with no " +
			"select variables! Add some variables using addSelectVariable()!");
		}
		// variables that are used in the SPARQL query
		String variables = "";
		// SPARQL query
		String queryString = "";

		// append variables
		for(String var : this.variableList ) {
			variables+=" "+var;
		}

		//cheesy mechanism to allow UNION statements to parse correctly
		String startBracket = " {";
		String endBracket = "} ";
		for (String queryTriplet : queryTriplets) {
			if (queryTriplet.contains("UNION")) {
				startBracket = " {{";
				endBracket = " }}";
			}
		}

		//add prefixes into the query statement if they have been added.
		if (prefixMap.keySet().isEmpty() == false ) {
			for (String prefix : prefixMap.keySet()) {
				queryString += "PREFIX " + prefix + ": " +
				prefixMap.get(prefix) + " ";
			}
		}

		// make sure we have some variables
		if(variables != "")
			queryString += "select DISTINCT" + variables + startBracket;

		// wrap up query string from queryTripletList
		for (String queryTriplet : queryTriplets) {
			queryString += queryTriplet;
			if (queryTriplet.contains("UNION") == false &&
					queryTriplet.contains("FILTER") == false)
				queryString += " . ";

		}

		queryString += endBracket;
		System.out.println(queryString);
		return queryString;
	}

	/**
	 * Get the number of triplets added;
	 * @return - number of triplets
	 */
	public int getTripletCount() {
		return queryTriplets.size();
	}

	/**
	 * Execute a SPARQL query built up from a set of query triplets.
	 * @see #addQueryTriplet(String)
	 * @return a Map with one key per $variable and a list of results as the value
	 * @see MultiHashMap
	 */
	public MultiHashMap<String, String> runSelectQuery() {
		String queryString = getComposedQuery();

		try {
			// URL encode query string
			queryString = URLEncoder.encode(queryString, "UTF-8");

			// compose the final URL
			URL sparqlConnection = new URL(this.sparqlEndPointURL +
					"?query=" + queryString);

			//System.out.println(sparqlConnection.toString());


			HttpURLConnection httpConnection = (HttpURLConnection)sparqlConnection.openConnection();
			httpConnection.setRequestProperty("accept", "application/sparql-results+xml");
			InputStream queryResult = httpConnection.getInputStream();

			return parseSPARQLResult(queryResult);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Execute a SPARQL query built up from a set of query triplets.
	 * @see #addQueryTriplet(String)
	 * @return a Map with one key per $variable and a list of results as the value
	 * @see MultiHashMap
	 */
	public MultiHashMap<String, String> runSelectQuery1() {
		String queryString = getComposedQuery();

		try {
			// URL encode query string
			queryString = URLEncoder.encode(queryString, "UTF-8");

			// compose the final URL
			URL sparqlConnection = new URL(this.sparqlEndPointURL +
					"?query=" + queryString);

			//System.out.println(sparqlConnection.toString());


			HttpURLConnection httpConnection = (HttpURLConnection)sparqlConnection.openConnection();
			httpConnection.setRequestProperty("accept", "application/sparql-results+xml");
			InputStream queryResult = httpConnection.getInputStream();

			return parseSPARQLResult1(queryResult);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Execute a SPARQL query built up from a set of query triplets.
	 * @see #addQueryTriplet(String)
	 * @return a Map with one key per $variable and a list of results as the value
	 * @see MultiHashMap
	 */
	public MultiHashMap<String, String> runSelectQueryForNeurolexData() {
		String queryString = getComposedQuery();

		try {
			// URL encode query string
			queryString = URLEncoder.encode(queryString, "UTF-8");

			// compose the final URL
			URL sparqlConnection = new URL(this.sparqlEndPointURL +
					"?query=" + queryString);

			//System.out.println(sparqlConnection.toString());


			HttpURLConnection httpConnection = (HttpURLConnection)sparqlConnection.openConnection();
			httpConnection.setRequestProperty("accept", "application/sparql-results+xml");
			InputStream queryResult = httpConnection.getInputStream();

			return parseSPARQLResultForNeurolexData(queryResult);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 *
	 * @param queryResult - an input stream that contains a SPARQL result XML
	 * @return a Map with one key per $variable and a list of results as the value
	 * @see MultiHashMap
	 * @throws Exception
	 */
	private MultiHashMap<String, String> parseSPARQLResult(InputStream queryResult)
	throws Exception {

		MultiHashMap<String, String> resultMap =
			new MultiHashMap<String,String>();
		String currentVariable = null;
		String selectedName = null;
		String selectedChildURI = null;
		String selectedChildName = null;
		String nameVariable = null;
		String childURIVariable = null;
		String childNameVariable = null;

		//create a parser for the XML that we will be getting
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader parser =
			factory.createXMLStreamReader(new BufferedInputStream(queryResult));

		while (true) {

			int event = parser.next();

			if (event == XMLStreamConstants.START_ELEMENT) {
				if ("binding".equals(parser.getLocalName())) {
					// look through variable list to see if we have a match
					String selectedVariable = null;
					for (String variable : this.variableList) {
						// check for matching. search the first attribute and
						// leave off the "$" of the variable.
						// if there's a match, put it in the selectedVariable
						if (parser.getAttributeValue(0).equals(
								variable.substring(1))) {
							selectedVariable = variable;
						}
					}
					if (selectedVariable != null) {

						// skip to the URI start element
						event = parser.next();
						while (event != XMLStreamConstants.START_ELEMENT) {
							event = parser.next();
						}
						//get the unique variable name for a particular brain region
						//date.
						if(selectedVariable.contains("_"))
							currentVariable = getElementVariableName(selectedVariable);

						// variable pretends to the variable name.
						if(selectedVariable.equalsIgnoreCase(currentVariable+"_name")){
							nameVariable= selectedVariable;
							selectedName = parser.getElementText();
							selectedName = selectedName.replaceAll("[ \t]+", " ");
							resultMap.put(nameVariable, selectedName);

						}
						//variable pretends to the child's uri.
						else if(selectedVariable.equalsIgnoreCase(currentVariable+"_child_uri")){
							childURIVariable = selectedVariable;
							selectedChildURI = parser.getElementText();
							selectedChildURI = selectedChildURI.replaceAll("[ \t]+", " ");
						}
						//variable pretends to the child's name.
						else if(selectedVariable.equalsIgnoreCase(currentVariable+"_child_name")){
							childNameVariable = selectedVariable;
							selectedChildName = parser.getElementText();
							selectedChildName = selectedChildName.replaceAll("[ \t]+", " ");
						}

						//once a set of data is completed store the complete set in
						//the data structure.
						if(childURIVariable != null &&
								childNameVariable != null){

							resultMap.put(childURIVariable, selectedChildURI);
							resultMap.put(childNameVariable, selectedChildName);

							//reset the variables for the data set in order to
							//obtain the next set of data.
							//nameVariable = null;
							childURIVariable = null;
							childNameVariable = null;
						}
					}
				}
			}
			if (event == XMLStreamConstants.END_DOCUMENT) {
				parser.close();
				break;
			}
		}
		System.out.println("Data processing finalized.");
		queryResult.close();

		return resultMap;
	}

	/**
	 * Method returns a list of web pages associated with a given variable.
	 * The page pretends to PubMed information pretending to a connection
	 * between brain regions.
	 * @param queryResult - an input stream that contains a SPARQL result XML
	 * @return a Map with one key per $variable and a list of results as the value
	 * @see MultiHashMap
	 * @throws Exception
	 */
	private MultiHashMap<String, String> parseSPARQLResult1(InputStream queryResult)
	throws Exception {

		MultiHashMap<String, String> resultMap =
			new MultiHashMap<String,String>();


		//create a parser for the XML that we will be getting
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader parser =
			factory.createXMLStreamReader(new BufferedInputStream(queryResult));

		while (true) {

			int event = parser.next();

			if (event == XMLStreamConstants.START_ELEMENT) {
				if ("binding".equals(parser.getLocalName())) {
					// look through variable list to see if we have a match
					String selectedVariable = null;
					for (String variable : this.variableList) {
						// check for matching. search the first attribute and
						// leave off the "$" of the variable.
						// if there's a match, put it in the selectedVariable
						if (parser.getAttributeValue(0).equals(
								variable.substring(1))) {
							selectedVariable = variable;
						}
					}
					if (selectedVariable != null) {

						// skip to the URI start element
						event = parser.next();
						while (event != XMLStreamConstants.START_ELEMENT) {
							event = parser.next();
						}

						//get the unique variable name for a particular brain region
						//date.
						resultMap.put(selectedVariable, parser.getElementText());
					}
				}
			}
			if (event == XMLStreamConstants.END_DOCUMENT) {
				parser.close();
				break;
			}
		}
		System.out.println("Data processing finalized.");
		queryResult.close();

		return resultMap;
	}
	/**
	 *
	 * @param queryResult - an input stream that contains a SPARQL result XML
	 * @return a Map with one key per $variable and a list of results as the value
	 * @see MultiHashMap
	 * @throws Exception
	 */
	private MultiHashMap<String, String> parseSPARQLResultForNeurolexData(InputStream queryResult)
	throws Exception {

		MultiHashMap<String, String> resultMap =
			new MultiHashMap<String,String>();

		//create a parser for the XML that we will be getting
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader parser =
			factory.createXMLStreamReader(new BufferedInputStream(queryResult));

		while (true) {

			int event = parser.next();

			if (event == XMLStreamConstants.START_ELEMENT) {
				if ("binding".equals(parser.getLocalName())) {
					//look through variable list to see if we have a match
					String selectedVariable = null;
					for (String variable : this.variableList) {
						//check for matching.  search the first attribute and
						//leave off the "$" of the variable.
						//if there's a match, put it in the selectedVariable
						if (parser.getAttributeValue(0).equals(variable.substring(1))) {
							selectedVariable = variable;
						}
					}
					if (selectedVariable != null) {

						// skip to the URI start element
						event = parser.next();
						while (event != XMLStreamConstants.START_ELEMENT) {
							event = parser.next();
						}

						String elementText =
							parser.getElementText();
						elementText = elementText.replaceAll("[ \t]+", " ");
						resultMap.put(selectedVariable, elementText);
					}
				}
			}
			if (event == XMLStreamConstants.END_DOCUMENT) {
				parser.close();
				break;
			}
		}
		System.out.println("Data processing finalized.");
		queryResult.close();

		return resultMap;
	}


	/**
	 * Method returns the first two characters of the variable.  This two
	 * characters are unique to a brain region and are necessary to identify
	 * data that pretends to a given brain region.
	 * @param nameVariable - string from which to extract the unique variable.
	 * @return 			   - return the unique variable name.
	 */
	public String getElementVariableName(String nameVariable){
		return nameVariable.substring(0,nameVariable.indexOf("_"));
	}
}
