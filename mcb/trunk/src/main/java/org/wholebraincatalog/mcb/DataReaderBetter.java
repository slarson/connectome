package org.wholebraincatalog.mcb;

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

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;



/**
 * Reads the RDF from a Category page of NeuroLex.org.  
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class DataReaderBetter implements IDataReader
{
	/**
	 * The URL of the sparql end point that will be queries
	 */
	private String sparqlEndPointURL = null;
	
	/**
	 * The list of triplets that will be used to query
	 */
	private List<String> queryTriplets = null;
	
	/**
	 * Constructor for DataReaderBetter
	 * @param sparqlEndPoint - the URL of the SPARQL
	 * end point you wish to execute queries against.
	 */
	public DataReaderBetter(String sparqlEndPoint) {
		this.sparqlEndPointURL = sparqlEndPoint;
		this.queryTriplets = new ArrayList<String>();
	}

	/**
	 * Adds one triplet line to a SPARQL query.  Variables
	 * that are used here will need to be included as 
	 * an argument to {@link #runSelectQuery(String)} when 
	 * executed.
	 * @param queryTriplet - a triplet separated by spaces that
	 *  is part of a SPARQL query, 
	 *  such as $s <http://someurl/property> $y.
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
	 * Put the queryTriplet list together into a SPARQL query
	 * @param selectVariables - the list of variables contained
	 * in the query triplets that you want to report back on.
	 * @return - a string containing a SPARQL query
	 * @see #addQueryTriplet(String)
	 */
	protected String getComposedQuery(String selectVariables) {
		String queryString = "select " + selectVariables + "{";

		// wrap up query string from queryTripletList
		for (String queryTriplet : queryTriplets) {
			queryString += queryTriplet + ".";
		}
		queryString += "}";
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
	 * @param selectVariables - the list of variables contained
	 * in the query triplets that you want to report back on.
	 * @see #addQueryTriplet(String)
	 * @return the query result as an XML document in an InputStream
	 */
	public InputStream runSelectQuery(String selectVariables) {
		String queryString = getComposedQuery(selectVariables);
		
		try {
			// URL encode query string
			queryString = URLEncoder.encode(queryString, "UTF-8");

			// compose the final URL
			URL sparqlConnection = new URL(this.sparqlEndPointURL + 
					"?query=" + queryString);

			InputStream queryResult = sparqlConnection.openStream();

			return queryResult;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void parseSPARQLResult(InputStream queryResult) throws Exception {

		//create a parser for the XML that we will be getting
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader parser = factory.createXMLStreamReader(queryResult);
	
		while (true) {

			int event = parser.next();

			if (event == XMLStreamConstants.START_ELEMENT) {
				System.out.println(parser.getLocalName());
				if ("uri".equals(parser.getLocalName())) {
					
				} else if ("binding".equals(parser.getLocalName())) {

				}
			}
			if (event == XMLStreamConstants.END_ELEMENT) {

			}
			if (event == XMLStreamConstants.END_DOCUMENT) {
				parser.close();
				break;
			}
		}
		
		System.out.println("Data processing finalized.");
		queryResult.close();
	}
	
	public Node getNode() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
