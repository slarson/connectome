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



import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.wholebrainproject.wbc.util.NetworkingUtil;
import org.wholebrainproject.wbc.util.exception.WBCException;


/**
 * Reads the RDF from a Category page of NeuroLex.org.  
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class DataReader
{
	
	/**
	 * The definition.
	 */
	private String uri = null;
	
	/**
	 * The synonyms.
	 */
	private Set<String> synonyms = new HashSet<String>();
	
	/**
	 * The neurolex page.
	 */
	private URL thalisNeurolexStore = null;
	
	/**
	 * The NIF ID of the class you are looking up.
	 */
	private String url = null;
	
	
	/**
	 * Node to store data
	 */
	private Node node;
	
	private String label = null;
	/**
	 * Construct with the NIF ID.  Will access the page
	 * http://neurolex.org/wiki/NIFID
	 * @param nifID - the ID of the Category page you wish to read.
	 * @throws Exception 
	 */
	public DataReader(String url,String str)throws Exception{
		this.url = url;
		node = new Node(this.url,str,countURI(url));
		preProcessID(url);
	}

	/**
	 * This method returns the node containing the data.
	 * @return node - the node containing the data.
	 */
	public Node getNode(){
		return node;
	}
	/**
	 * Returns the contents of the "Definition" property, if it exists.
	 * @return a String containing the contents of the "Definition" property. 
	 * Null if no definition
	 
	public String getURI()throws WBCException{
		return uri;
	}
	**/
	/**
	 * Returns the contents of the "Label" property, if it exists.
	 * @return a String containing the contents of the "Label" property.  Null
	 * if no label
	 **/
	public String getLabel()throws WBCException{
		return label;
	}
	
	/**
	 * Returns one or more synonyms associated from the "Synonym" property, 
	 * if any exist.  If there are none, will return an empty set.
	 * @return - a set of Strings with contents from the "Synonym" property, in
	 * no particular order.
	 
	public Set<String> getSynonyms()throws WBCException{
		return synonyms;
	}
	**/
	/**
	 * Returns the URL for the page.
	 * @return a well formed URL
	 * @throws WBCException - if the page doesn't exist.
	 
	public String getNeuroLexPage()throws WBCException{
		if (thalisNeurolexStore == null) {
			System.err.print("URL was bad!");
		}
		return "http://neurolex.org/wiki/" + url;
	}
	**/
	/**
	 * Populate the data members by opening a connection to the RDF page, 
	 * reading it, and setting the data members.
	 * @param nifID - the NIF ID of the class you want to see.
	 * @throws WBCException - if anything goes wrong
	 */ 
	private void preProcessID(String url) throws WBCException
    {
              try
              {       
                      //set the URL for the RDF actual page.
                      thalisNeurolexStore = new URL(url);
                      
                      //with that URL, we can get the RDF page with the real content.
                      //populate the desired data from that RDF page.
                      populateDataFromRDFURL(thalisNeurolexStore);
              }
              catch (MalformedURLException e)
              {
                      throw new WBCException("This ID doesn't make for a valid URL! " 
                                      + url, e);
              }
              catch (IOException e)
              {
                      throw new WBCException("Can't access this URL! " + url, e);
              }
              catch (XMLStreamException e)
              {
                      throw new WBCException("Trouble parsing the RDF! " + url, e);
              } catch (Exception e) {
                      throw new WBCException("Trouble parsing the RDF! " + url, e);
              }
              
      }

	/**
	 * This method counts the number of URIs in the current URL.  This data is used to 
	 * instantiate the array used to store the URIs.  
	 * @param thalisNeurolexStore - URL containing the URIs.
	 * @return uriCount - the number of URIs in URL.
	 * @throws Exception
	 */
	
	private int countURI(String uri)throws Exception {
		int uriCount = 0;
		URL permThalisURI = new URL(uri);
		//open up a new stream to the neurolexPage
		InputStream incount = 
			NetworkingUtil.getInstance().getStreamViaRestlet(permThalisURI.toString());

		//create a parser for the XML that we will be getting
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader parser = factory.createXMLStreamReader(incount);
	
		//variable to set when the parser is looking at tags
		//inside the "swivt:Subject" element.
		boolean withinSubjectElement = true;
		while (true) {
			
		    int event = parser.next();
		    if (event == XMLStreamConstants.END_DOCUMENT) {
		       parser.close();
		       break;
		    }
			if (event == XMLStreamConstants.START_ELEMENT)
			{
				//Assuming only one Class element with these properties
				//ever appears on these pages.
				if (withinSubjectElement)
				{
					if ("uri".equals(parser.getLocalName()))
					{
						//set definition
						uri = parser.getElementText();
						uriCount++;
						
					}
					
				}
			}
			if (event == XMLStreamConstants.END_ELEMENT)
			{
				//if we are within the "Class" element, set the flag to 
				//false
				if ("Subject".equals(parser.getLocalName()))
				{
					withinSubjectElement = false;
				}
			}
		}
		return uriCount;
	}
	
	
	/**
	 * The method populates the data node from the particular URI.
	 * @param neurolexPage - the URL of the RDF page you want to populate data 
	 * 						from
	 * @throws Exception 
	 */
	private void populateDataFromRDFURL(URL thalisNeurolexStore) throws Exception {
		//open up a new stream to the neurolexPage
		InputStream in = 
			NetworkingUtil.getInstance().getStreamViaRestlet(thalisNeurolexStore.toString());

		//create a parser for the XML that we will be getting
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader parser = factory.createXMLStreamReader(in);
	
		//variable to set when the parser is looking at tags
		//inside the "swivt:Subject" element.
		boolean withinSubjectElement = true;
		
		//Find the number of URIs in document.
		//int uriCount = countURI(thalisNeurolexStore);
		
		//array index of uris.
		int count = 0;
		
		//Brain part
		String str;
		System.out.println("Populating data from URL: "+thalisNeurolexStore.toString());
		
		System.out.println("......");
		
		while (true) {
			
		    int event = parser.next();
		    if (event == XMLStreamConstants.END_DOCUMENT) {
		       parser.close();
		       break;
		    }
			if (event == XMLStreamConstants.START_ELEMENT)
			{
				//Assuming only one Class element with these properties
				//ever appears on these pages.
				if (withinSubjectElement)
				{
					if ("uri".equals(parser.getLocalName()))
					{
						//set definition
						uri = parser.getElementText();
						str = uri.substring(uri.indexOf("BAMS#")+5);
						str = str.replace('_', ' ');
						node.store(str);

					}
					else if ("sameAs".equals(parser.getLocalName()))
					{
						//we parse the label from the sameAsURL, since there
						//is no other easy way to get it.
						String sameAsURL = parser.getAttributeValue(0);
						
						//chop off text before and including the '3A'
						int cutOff = sameAsURL.indexOf("3A") + 2;
						sameAsURL = sameAsURL.substring(cutOff);
						
						//swap underscores with spaces
						sameAsURL = sameAsURL.replace('_', ' ');
					}
					else if ("Synonym".equals(parser.getLocalName()))
					{
						//add to synonyms
						synonyms.add(parser.getElementText());
					}
				}
			}
			if (event == XMLStreamConstants.END_ELEMENT)
			{
				//if we are within the "Class" element, set the flag to 
				//false
				if ("Subject".equals(parser.getLocalName()))
				{
					withinSubjectElement = false;
				}
			}
		}
		System.out.println("Data processing finalized.");
		in.close();
	}
}
