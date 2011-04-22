package com.wholebrain.reader;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;

/**
 * This application contains the main driver for a XML Stax reader.
 * This allows for the parsing of XML files in segments.
 *
 * The class needs some improvements that will be latter documented and
 * implemented.
 *
 * The base code was taken from
 * {@link http://www.ibm.com/developerworks/xml/library/x-tipstx2/index.html}
 * @author ruggero
 * @date 04/22/2011
 */
public class StaxReader {

	public static void main(String[] args)
	throws XMLStreamException, IOException {
		// URL and URL content.
		URL                url;
	    URLConnection      urlConn;


	    url = new URL("http://www.connectomics.org/neurolex/cwikidump.xml");
	    urlConn = url.openConnection();
	    urlConn.setDoInput(true);
	    urlConn.setUseCaches(false);

		// Use  reference implementation
		System.setProperty(
				"javax.xml.stream.XMLInputFactory",
		"com.bea.xml.stream.MXParserFactory");
		// Create the XML input factory
		XMLInputFactory factory = XMLInputFactory.newInstance();
		// Create event reader
		//FileReader reader = new FileReader("somefile.xml");
		XMLEventReader eventReader = factory.createXMLEventReader(urlConn.getInputStream());
		// Create a filtered reader
		XMLEventReader filteredEventReader =
			factory.createFilteredReader(eventReader, new EventFilter() {
				public boolean accept(XMLEvent event) {
					// Exclude PIs
					return (!event.isProcessingInstruction());
				}
			});
		// Main event loop
		while (filteredEventReader.hasNext()) {
			XMLEvent e = (XMLEvent) filteredEventReader.next();
			System.out.println(e);
		}
	}
}
