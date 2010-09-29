
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
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
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

	private int LIMIT = 0;

	private int OFFSET = 0;

	private boolean neurolexData = false;
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
	 * Method clears variables for next search.
	 */
	public void resetVariables(){
		this.queryTriplets.clear();
		this.variableList.clear();
		this.prefixMap.clear();
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
		if(this.neurolexData)
			queryString +="ORDER BY $name "+"LIMIT "+getCurrentLimit()+" OFFSET "+getCurrentOffset();

		System.out.println(queryString);
		return queryString;
	}

	public void setFlagNeurolexData(boolean value){
		this.neurolexData = value;
	}
	/**
	 * Method return the current limit in the search.
	 * @return LIMIT - current limit
	 */
	private int getCurrentLimit(){
		return this.LIMIT;
	}

	/**
	 * Method returns the current offset in the search.
	 * @return OFFSET - current offset 
	 */
	private int getCurrentOffset(){
		return this.OFFSET;
	}
	/**
	 * Set the limit and offset for the searches.
	 * @param LIMIT
	 * @param OFFSET
	 */
	public void setCurrentLimitAndOffset(int LIMIT, int OFFSET){
		this.LIMIT = LIMIT;
		this.OFFSET = OFFSET;
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
	public HashMap<Integer,NeurolexPageId> runSelectQueryBAMS() {
		String queryString = getComposedQuery();

		try {
			// URL encode query string
			queryString = URLEncoder.encode(queryString, "UTF-8");

			// compose the final URL
			URL sparqlConnection = new URL(this.sparqlEndPointURL + 
					"?query=" + queryString);

			HttpURLConnection httpConnection = (HttpURLConnection)sparqlConnection.openConnection();
			httpConnection.setRequestProperty("accept", "application/sparql-results+xml");
			InputStream queryResult = httpConnection.getInputStream();

			return parseSPARQLResultBAMS(queryResult);

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
	public HashMap<Integer,brainRegionSynonyms> runSelectQueryNeurolexHashCode() {
		String queryString = getComposedQuery();
		try {
			// URL encode query string
			queryString = URLEncoder.encode(queryString, "UTF-8");

			// compose the final URL
			URL sparqlConnection = new URL(this.sparqlEndPointURL + 
					"?query=" + queryString);

			HttpURLConnection httpConnection = (HttpURLConnection)sparqlConnection.openConnection();
			httpConnection.setRequestProperty("accept", "application/sparql-results+xml");
			InputStream queryResult = httpConnection.getInputStream();

			return parseSPARQLResultNeurolexHashCode(queryResult);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public HashMap<Integer,NeurolexPageId> runSelectQueryNeurolexHashCodeNoSynonym() {
		String queryString = getComposedQuery();
		try {
			// URL encode query string
			queryString = URLEncoder.encode(queryString, "UTF-8");

			// compose the final URL
			URL sparqlConnection = new URL(this.sparqlEndPointURL + 
					"?query=" + queryString);

			HttpURLConnection httpConnection = (HttpURLConnection)sparqlConnection.openConnection();
			httpConnection.setRequestProperty("accept", "application/sparql-results+xml");
			InputStream queryResult = httpConnection.getInputStream();
			if(queryResult != null)
				return parseSPARQLResultNeurolexHashCodeNoSynonyms(queryResult);

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
	private HashMap<Integer,NeurolexPageId> parseSPARQLResultBAMS(InputStream queryResult) 
	throws Exception {
		HashMap<Integer,NeurolexPageId> vec = new HashMap<Integer,NeurolexPageId>();
		int index = 0;
		String openParanthesesString;
		String closedParanthesesString;
		String elementName = null;
		String elementDescription = null;
		String elementSpecies = null;
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

						String elementText = parser.getElementText();
						elementText = elementText.replaceAll("[ \t]+", " ");

						//System.out.println("elementText: "+elementText);
						//if(elementText.contains("(") && elementText.contains(")")){				
						//	openParanthesesString = elementText.substring(0,elementText.indexOf("("));
						//	closedParanthesesString = elementText.substring(elementText.indexOf(")")+1,elementText.length());

						if(selectedVariable.equals("$name"))
							elementName = elementText;
						else if(selectedVariable.equals("$description"))
							elementDescription = elementText;
						else if(selectedVariable.equals("$species"))
							elementSpecies = elementText;
						if(elementName != null && elementDescription != null && elementSpecies != null){
							//elementText = openParanthesesString+closedParanthesesString;
							//elementText = elementText.replaceAll("  ", " ");
							//System.out.println("elementText: "+elementText);
							vec.put(elementName.replace(" ", "").toLowerCase().hashCode(),new NeurolexPageId(elementName.replace(" ", "").toLowerCase().hashCode(),
									elementName.toLowerCase(),elementDescription,elementSpecies));

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


		return vec;
	}

	/**
	 * 
	 * @param queryResult - an input stream that contains a SPARQL result XML
	 * @return a Map with one key per $variable and a list of results as the value
	 * @see MultiHashMap
	 * @throws Exception
	 */
	private MultiHashMap<String,String> parseSPARQLResultNeurolex(InputStream queryResult) 
	throws Exception {
		String elementKey="";
		String elementValue="";
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

						String elementText = parser.getElementText();
						elementText = elementText.replaceAll("[ \t]+", " ");

						if(selectedVariable.equals("$name"))
							elementKey = elementText;
						else if(selectedVariable.equals("$synonym"))
							elementValue = elementText;
						if(!elementKey.equals("") && !elementValue.equals("")){
							resultMap.put(elementKey.toLowerCase().replaceAll("-", "")
									, elementValue.toLowerCase().replaceAll("-",""));
							//System.out.println("elementKey: "+elementKey+" elementValue: "+elementValue);
							elementKey = "";
							elementValue = "";
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

	/* 
	 * @param queryResult - an input stream that contains a SPARQL result XML
	 * @return a Map with one key per $variable and a list of results as the value
	 * @see MultiHashMap
	 * @throws Exception
	 */
	private HashMap<Integer,brainRegionSynonyms> parseSPARQLResultNeurolexHashCode(InputStream queryResult) 
	throws Exception {
		String elementKey= null;
		String elementValue= null;
		HashMap<Integer, brainRegionSynonyms> resultMap = 
			new HashMap<Integer,brainRegionSynonyms>();

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

						String elementText = parser.getElementText();
						elementText = elementText.replaceAll("[ \t]+", " ");

						if(selectedVariable.equals("$name")){
							elementKey = elementText;
						}	
						else if(selectedVariable.equals("$synonym")){
							elementValue = elementText;
						}	

						if(elementKey != null && elementValue != null){
							if(!resultMap.containsKey(elementKey)){
								brainRegionSynonyms synonym = new brainRegionSynonyms(elementKey);
								synonym.addSynonym(elementValue.toLowerCase());
								resultMap.put(elementKey.replace(" ", "").toLowerCase().hashCode(), 
										synonym);
							}
							else if(resultMap.containsKey(elementKey)){
								brainRegionSynonyms synonym = resultMap.get(elementKey);
								synonym.addSynonym(elementValue.toLowerCase());							
								resultMap.put(elementValue.replace(" ", "").toLowerCase().hashCode(), 
										synonym);
							}
							elementKey = null;
							elementValue = null;
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
	private HashMap<Integer,NeurolexPageId> parseSPARQLResultNeurolexHashCodeNoSynonyms(InputStream queryResult)
	throws Exception {
		String elementKey="";
		String elementName = null;
		String elementPage = null;
		String elementId  = null;
		//String elementValue="";
		HashMap<Integer, NeurolexPageId> resultMap = 
			new HashMap<Integer, NeurolexPageId>();

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
						String elementText = parser.getElementText();
						elementText = elementText.replaceAll("[ \t]+", " ");

						if(selectedVariable.equals("$name")){
							elementName = elementText;
						}	
						else if(selectedVariable.equals("$page")){
							elementPage = elementText;
						}
						else if(selectedVariable.equals("$id")){
							elementId = elementText;
						}
						
						if(elementName != null && elementPage != null && elementId != null){
							NeurolexPageId neurolex = 
								new NeurolexPageId(elementName.replace(" ", "").toLowerCase().hashCode(),
										elementName.toLowerCase(), elementPage.toLowerCase(),
										elementId.toLowerCase(),"");

							resultMap.put(elementName.replace(" ", "").toLowerCase().hashCode(),
									neurolex);
							elementName = null;
							elementPage = null;
							elementId = null;
							neurolex = null;
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

	public HashMap<Integer, NeurolexPageId> runSelectQueryNeurolexNamePageId() {
		String queryString = getComposedQuery();
		try {
			// URL encode query string
			queryString = URLEncoder.encode(queryString, "UTF-8");

			// compose the final URL
			URL sparqlConnection = new URL(this.sparqlEndPointURL + 
					"?query=" + queryString);

			HttpURLConnection httpConnection = (HttpURLConnection)sparqlConnection.openConnection();
			httpConnection.setRequestProperty("accept", "application/sparql-results+xml");
			InputStream queryResult = httpConnection.getInputStream();
			if(queryResult != null)
				return parseSPARQLResultNeurolexNamePageId(queryResult);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private HashMap<Integer, NeurolexPageId> parseSPARQLResultNeurolexNamePageId(
			InputStream queryResult) throws XMLStreamException, IOException {

		String elementName = null;
		String elementPage = null;
		String elementId = null;
		String elementSpecies = null;
		//String elementValue="";
		HashMap<Integer, NeurolexPageId> resultMap = 
			new HashMap<Integer, NeurolexPageId>();

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

						String elementText = parser.getElementText();
						elementText = elementText.replaceAll("[ \t]+", " ");

						if(selectedVariable.equals("$name")){
							elementName = elementText;
						}	
						else if(selectedVariable.equals("$page")){
							elementPage = elementText;
						}
						else if(selectedVariable.equals("$id")){
							elementId = elementText;
						}

						if(elementName != null && elementPage != null && elementId != null){
							NeurolexPageId neurolex = 
								new NeurolexPageId(elementName.replace(" ", "").toLowerCase().hashCode(),
										elementName.toLowerCase(), elementPage.toLowerCase(),
										elementId.toLowerCase(),"");

							resultMap.put(elementName.replace(" ", "").toLowerCase().hashCode(),
									neurolex);
							elementName = null;
							elementPage = null;
							elementId = null;
							neurolex = null;


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


}
