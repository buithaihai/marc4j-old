/**
 * Copyright (C) 2002 Bas Peters (mail@bpeters.com)
 *
 * This file is part of MARC4J
 *
 * MARC4J is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * MARC4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MARC4J; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * As a special exception, if you link this library with other files to
 * produce an executable, this library does not by itself cause the
 * resulting executable to be covered by the GNU General Public License.
 * This exception does not however invalidate any other reasons why the
 * executable file might be covered by the GNU General Public License.
 */
package org.marc4j.util;

import java.io.Writer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;
import org.marc4j.helpers.MarcXmlConsumer;
import org.marc4j.helpers.SaxErrorHandler;
import org.marc4j.util.RecordHandlerImpl;

/**
 * <p>Provides a driver for <code>MarcXmlConsumer</code> 
 * to convert a MARCXML collection document to MARC tape format (ISO 2709).   </p>
 *
 * <p>For usage, run the command-line with the following command:</p>
 * <p><code>java org.marc4j.util.XmlMarcWriter -help</code></p>
 *
 * <p><b>Note:</b> this class requires a JAXP compliant SAX2 parser.
 * For W3C XML Schema support a JAXP 1.2 compliant parser is needed.</p> 
 *
 * <p>Check the home page for <a href="http://www.loc.gov/standards/marcxml/">
 * MARCXML</a> for more information about the MARCXML format.</p>
 *
 * @author Bas Peters
 * @see MarcXmlConsumer
 */
public class XmlMarcWriter {

    private static final String JAXP_SCHEMA_LANGUAGE =
        "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

    private static final String W3C_XML_SCHEMA =
        "http://www.w3.org/2001/XMLSchema";

    private static final String JAXP_SCHEMA_SOURCE =
        "http://java.sun.com/xml/jaxp/properties/schemaSource";

    static public void main(String[] args) {
        String input = null;
	String output = null;
        String schemaSource = null;
        boolean dtdValidate = false;
        boolean xsdValidate = false;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-dtd")) {
                dtdValidate = true;
            } else if (args[i].equals("-xsd")) {
                xsdValidate = true;
            } else if (args[i].equals("-xsdss")) {
                if (i == args.length - 1) {
                    usage();
                }
                xsdValidate = true;
                schemaSource = args[++i];
            } else if (args[i].equals("-out")) {
                if (i == args.length - 1) {
                    usage();
                }
                output = args[++i];
            } else if (args[i].equals("-usage")) {
                usage();
            } else if (args[i].equals("-help")) {
                usage();
            } else {
                input = args[i];

                // Must be last arg
                if (i != args.length - 1) {
                    usage();
                }
            }
        }
        if (input == null) {
            usage();
        }

	try {
	    // Create a JAXP SAXParserFactory instance
	    SAXParserFactory factory = SAXParserFactory.newInstance();

	    // JAXP is not by default namespace aware
	    factory.setNamespaceAware(true);

	    // Configure the validation
	    factory.setValidating(dtdValidate || xsdValidate);

	    // Create a JAXP SAXParser
	    SAXParser saxParser = factory.newSAXParser();

	    // Set the schema language if necessary
	    if (xsdValidate)
                saxParser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);

	    // Set the schema source, if any.
	    if (schemaSource != null)
		saxParser.setProperty(JAXP_SCHEMA_SOURCE, new File(schemaSource));

	    // Create a Writer.
	    Writer writer;
	    if (output == null) {
		writer = new BufferedWriter(new OutputStreamWriter(System.out));
	    } else {
		writer = new BufferedWriter(new FileWriter(output));
	    }
	    
	    // Create a new instance of the RecordHandler implementation.
	    RecordHandlerImpl handler = new RecordHandlerImpl(writer);
		
	    // Create a new SAX2 consumer.
	    MarcXmlConsumer consumer = new MarcXmlConsumer();

	    // Set the record handler.
	    consumer.setRecordHandler(handler);

	    // Get the encapsulated SAX XMLReader.
	    XMLReader xmlReader = saxParser.getXMLReader();

	    // Set the ContentHandler of the XMLReader.
	    xmlReader.setContentHandler(consumer);

	    // Set the ErrorHandler of the XMLReader.
	     xmlReader.setErrorHandler(new SaxErrorHandler());

	    // Parse the XML document
	    xmlReader.parse(new File(input).toURL().toString());

	} catch (ParserConfigurationException e) {	    
	    e.printStackTrace();
	} catch (SAXNotSupportedException e) {
	    e.printStackTrace();
	} catch (SAXNotRecognizedException e) {
	    e.printStackTrace();
	} catch (SAXException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    private static void usage() {
        System.err.println("Usage: MarcXmlWriter [-options] <file.xml>");
        System.err.println("       -dtd = DTD validation");
        System.err.println("       -xsd | -xsdss <file.xsd> = W3C XML Schema validation using xsi: hints");
        System.err.println("           in instance document or schema source <file.xsd>");
        System.err.println("       -xsdss <file> = W3C XML Schema validation using schema source <file>");
        System.err.println("       -out <file> = Output using <file>");
        System.err.println("       -usage or -help = this message");
        System.exit(1);
    }
   
}
