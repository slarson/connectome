package com.date.reader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;


/**
 * This class takes a XML file and reads the relevant information.
 * The information is then written to file in the appropriate format.
 * @author ruggero
 *
 */
public class XMLDataReaderAndFormat {

	/**
	 * Main driver for the XML reader.  Takes the XML file as
	 * input.
	 * @param args
	 * @throws IOException
	 * @throws SAXException
	 * @throws XMLStreamException
	 */
	public static void main(String[] args) throws SAXException, IOException, XMLStreamException {

		FileInputStream file = new FileInputStream("sparql");
		FileWriter fileout =  new FileWriter("sparql_brain_cell.txt");
		BufferedWriter out = new BufferedWriter(fileout);
		writeToFile(file,out);
		file.close();
		out.close();
	}

	/**
	 * Method takes a file input stream and a buffered writer.
	 * The file is read and the relevant information is written
	 * to file.
	 * @param file
	 * @param out
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	private static void writeToFile(FileInputStream file,
			BufferedWriter out) throws XMLStreamException, IOException {
		//create a parser for the XML that we will be getting
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader parser =
			factory.createXMLStreamReader(new BufferedInputStream(file));

		String selectedVariable = null;
		String selectedElement;

		out.write("brain_region_id,brain_region_name,cellinstance_cell_id,celltype_cell_name \n");
		while (true) {

			int event = parser.next();

			if (event == XMLStreamConstants.START_ELEMENT) {
				if ("binding".equals(parser.getLocalName())) {
					selectedVariable = parser.getAttributeValue(0);

				}
				if (selectedVariable != null) {
					// skip to the URI start element
					event = parser.next();
					while (event != XMLStreamConstants.START_ELEMENT) {
						event = parser.next();
					}
					if(selectedVariable.contains("brain_region_id")){
						System.out.println(selectedVariable);
						selectedElement = parser.getElementText();
						selectedElement = selectedElement.replaceAll("[ \t]+", " ");
						System.out.println(selectedElement);
						out.write(selectedElement);
					}
					if(selectedVariable.contains("brain_region_name")){
						System.out.println(selectedVariable);
						selectedElement = parser.getElementText();
						selectedElement = selectedElement.replaceAll("[ \t]+", " ");
						out.write("*"+selectedElement);
					}
					if(selectedVariable.contains("cellinstance_cell_id")){
						System.out.println(selectedVariable);
						selectedElement = parser.getElementText();
						selectedElement = selectedElement.replaceAll("[ \t]+", " ");
						out.write("*"+selectedElement);
					}
					if(selectedVariable.contains("celltype_cell_name")){
						System.out.println(selectedVariable);
						selectedElement = parser.getElementText();
						selectedElement = selectedElement.replaceAll("[ \t]+", " ");
						out.write("*"+selectedElement+"\n");
						selectedVariable = null;
					}
				}
			}
			if (event == XMLStreamConstants.END_DOCUMENT) {
				parser.close();
				break;
			}

		}
		System.out.println("Data processing finalized.");
	}
}

