/**
 * Copyright (C) 2002 Bas Peters
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
package org.marc4j.marcxml;

import java.io.*;
import java.net.URL;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.AttributesImpl;
import org.marc4j.MarcHandler;
import org.marc4j.ErrorHandler;
import org.marc4j.MarcReader;
import org.marc4j.marc.MarcConstants;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Tag;
import org.marc4j.util.AnselToUnicode;

/**
 * <p><code>MarcXmlfilter</code> produces SAX2 events from MARC records.  </p>
 *
 * <p> </p>
 *
 * @author Bas Peters
 *
 * @see ExtendedFilter
 * @see org.marc4j.MarcHandler
 */
public class MarcXmlFilter extends ExtendedFilter 
    implements MarcHandler {

    /** Converts ansel to unicode */
    private boolean convert = false;

    /** Enables pretty printing */
    private boolean prettyPrinting = true;

    /** Empty attributes */
    private static final Attributes EMPTY_ATTS = 
	new AttributesImpl();

    /** MARC4J error handler property */
    private static final String ERROR_HANDLER = 
	"http://marc4j.org/properties/error-handler";

    /** MARC4J ansel to unicode conversion */
    private static final String ANSEL_TO_UNICODE = 
	"http://marc4j.org/features/ansel-to-unicode";

    /** MARC4J pretty printing */
    private static final String PRETTY_PRINTING = 
	"http://marc4j.org/features/pretty-printing";

    /** MARC4J document type declaration property */
    private static final String DOC_TYPE_DECL = 
	"http://marc4j.org/properties/document-type-declaration";

    /** MARC4J schema location property */
    private static final String SCHEMA_LOC = 
	"http://marc4j.org/properties/schema-location";

    /** Namespace for MARCXML */
    private static final String NS_URI = 
	"http://www.loc.gov/MARC21/slim";

    /** Namespace for W3C XML Schema instance */
    private static final String NS_XSI = 
	"http://www.w3.org/2001/XMLSchema-instance";

    /** Schema location */
    private String schemaLocation = null;

    /** {@link DocType} object */
    private DocType doctype = null;

    /** {@link ContentHandler} object */
    private ContentHandler ch;

    /** {@link ErrorHandler} object */
    private ErrorHandler eh;

    /**
     * <p>Set the document type declaration.</p>
     *
     * @param {@link String} the property name
     * @param {@link DocType} the property value
     */
    public void setProperty(String name, Object value) 
	throws SAXNotRecognizedException, SAXNotSupportedException {
	if (DOC_TYPE_DECL.equals(name))
	    this.doctype = (DocType)value;
	else if (ERROR_HANDLER.equals(name))
	    this.eh = (ErrorHandler)value;
	else if (SCHEMA_LOC.equals(name))
	    this.schemaLocation = (String)value;
	else
	    super.setProperty(name, value);
    }

    public void setFeature(String name, boolean value)
	throws SAXNotRecognizedException, SAXNotSupportedException {
	if (ANSEL_TO_UNICODE.equals(name))
	    this.convert = value;
	if (PRETTY_PRINTING.equals(name))
	    this.prettyPrinting = value;
	else
	    super.setFeature(name, value);
    }

    /**
     * <p>Sends the input source to the <code>MarcReader</code>.</p>
     *
     * @param input the {@link InputSource}
     */
    public void parse(InputSource input) {
	ch = getContentHandler();
	if (ch == null) {
            return;
        }

	try {
	    // convert the InputSource into a BufferedReader
	    BufferedReader br = null;
	    if (input.getCharacterStream() != null) {
		br = new BufferedReader(input.getCharacterStream());
	    } else if (input.getByteStream() != null) {
		br = new BufferedReader(new InputStreamReader(input.getByteStream()));
	    } else if (input.getSystemId() != null) {
		URL url = new URL(input.getSystemId());
		br = new BufferedReader(new InputStreamReader(url.openStream()));
	    } else {
		throw new SAXException("Invalid InputSource object");
	    }

	    // Create a new  MarcReader object.
	    MarcReader marcReader = new MarcReader();
	
	    // Register the MarcHandler implementation.
	    marcReader.setMarcHandler(this);

	    // Register the ErrorHandler implementation.
	    if (eh != null)
		marcReader.setErrorHandler(eh);

	    // Send the file to the parse method.
	    marcReader.parse(br);

	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    /**
     * <p>Returns the document handler being used, starts the document
     * and reports the root element.  </p>
     *
     */
    public void startFile() {
    	try {
	    AttributesImpl atts = new AttributesImpl();

	    // Report start of XML document.
            ch.startDocument();

	    // Report document type declaration
	    if (lh != null && doctype != null && schemaLocation == null) {
		lh.startDTD(doctype.getName(), 
			    doctype.getPublicId(), 
			    doctype.getSystemId());
		lh.endDTD();
	    }

	    // Outputting namespace declarations through the attribute object,
	    // since the startPrefixMapping refuses to output namespace declarations.	    
	    if (schemaLocation != null) {
		atts.addAttribute("", "xsi", "xmlns:xsi", "CDATA", NS_XSI);
		atts.addAttribute(NS_XSI, "schemaLocation", "xsi:schemaLocation", 
				  "CDATA", schemaLocation);
	    }

	    // Do not output the namespace declaration for MARCXML
	    // together with a document type declaration
	    if (doctype == null)
		atts.addAttribute("", "", "xmlns", "CDATA", NS_URI);

	    // Report start of prefix mapping for MARCXML
	    // OK together with Document Type Delcaration?
	    ch.startPrefixMapping("", NS_URI);

	    // Report root element
            ch.startElement(NS_URI, "collection", "collection", atts);

        } catch (SAXException se) {
            se.printStackTrace();
        }
    }

    /**
     * <p>Reports the starting element for a record and the leader node.  </p>
     *
     * @param leader the leader
     */
    public void startRecord(Leader leader) {
	try {
	    if (prettyPrinting)
		ch.ignorableWhitespace("\n  ".toCharArray(), 0, 3);
	    ch.startElement(NS_URI, "record", "record", EMPTY_ATTS);
	    if (prettyPrinting)
		ch.ignorableWhitespace("\n    ".toCharArray(), 0, 5);
	    writeElement(NS_URI,"leader","leader", EMPTY_ATTS, leader.marshal());
	} catch (SAXException se) {
	    se.printStackTrace();
	}
    }

    /**
     * <p>Reports a control field node (001-009).</p>
     *
     * @param tag the tag name
     * @param data the data element
     */
    public void controlField(String tag, char[] data) {
	try {
	    AttributesImpl atts = new AttributesImpl();
	    atts.addAttribute("", "tag", "tag", "CDATA", tag);
	    if (prettyPrinting) 
		ch.ignorableWhitespace("\n    ".toCharArray(), 0, 5);
	    writeElement(NS_URI,"controlfield","controlfield", atts, data);
	} catch (SAXException se) {
	    se.printStackTrace();
	}
    }

    /**
     * <p>Reports the starting element for a data field (010-999).</p>
     *
     * @param tag the tag name
     * @param ind1 the first indicator value
     * @param ind2 the second indicator value
     */
    public void startDataField(String tag, char ind1, char ind2) {
	try {
	    AttributesImpl atts = new AttributesImpl();
	    atts.addAttribute("", "tag", "tag", "CDATA", tag);
	    atts.addAttribute("", "ind1", "ind1", "CDATA", String.valueOf(ind1));
	    atts.addAttribute("", "ind2", "ind2", "CDATA", String.valueOf(ind2));
	    if (prettyPrinting) 
		ch.ignorableWhitespace("\n    ".toCharArray(), 0, 5);
	    ch.startElement(NS_URI,"datafield","datafield", atts);
	} catch (SAXException se) {
	    se.printStackTrace();
	}
    }

    /**
     * <p>Reports a subfield node.</p>
     *
     * @param code the data element identifier
     * @param data the data element
     */
    public void subfield(char code, char[] data) {
	try {
	    AttributesImpl atts = new AttributesImpl();
	    atts.addAttribute("", "code", "code", "CDATA", String.valueOf(code));
	    if (prettyPrinting) 
		ch.ignorableWhitespace("\n      ".toCharArray(), 0, 7);
	    ch.startElement(NS_URI,"subfield","subfield", atts);
	    if (convert) {
                char[] unicodeData = AnselToUnicode.convert(data);
                ch.characters(unicodeData, 0, unicodeData.length);
            } else {
                ch.characters(data, 0, data.length);
            }
	    ch.endElement(NS_URI,"subfield","subfield");
	} catch (SAXException se) {
	    se.printStackTrace();
    	}
    }
    
    /**
     * <p>Reports the closing element for a data field.</p>
     *
     * @param tag the tag name
     */
    public void endDataField(String tag) {
	try {
            ch.ignorableWhitespace("\n    ".toCharArray(), 0, 5);
	    ch.endElement(NS_URI,"datafield","datafield");
	} catch (SAXException se) {
	    se.printStackTrace();
	}
    }

    /**
     * <p>Reports the closing element for a record.</p>
     *
     */
    public void endRecord() {
	try {
	    if (prettyPrinting) 
		ch.ignorableWhitespace("\n  ".toCharArray(), 0, 3);
	    ch.endElement(NS_URI,"record","record");
	} catch (SAXException se) {
	    se.printStackTrace();
	}
    }
    
    /**
     * <p>Reports the closing element for the root, reports the end 
     * of the prefix mapping and the end a document.  </p>
     *
     */
    public void endFile() {
	try {
	    if (prettyPrinting) 
		ch.ignorableWhitespace("\n".toCharArray(), 0, 1);
	    ch.endElement(NS_URI,"collection","collection");
	    ch.endPrefixMapping("");
	    ch.endDocument();
	} catch (SAXException e) {
	    e.printStackTrace();
	}
    }

    private void writeElement(String uri, String localName,
			      String qName, Attributes atts, String content)
        throws SAXException {
        writeElement(uri, localName, qName, atts, content.toCharArray());
    }

    private void writeElement(String uri, String localName,
			      String qName, Attributes atts, char content)
        throws SAXException {
        writeElement(uri, localName, qName, atts, String.valueOf(content).toCharArray());
    }

    private void writeElement(String uri, String localName,
			      String qName, Attributes atts, char[] content)
        throws SAXException {
        ch.startElement(uri, localName, qName, atts);
        ch.characters(content, 0, content.length);
        ch.endElement(uri, localName, qName);
    }

}
