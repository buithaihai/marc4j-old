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

import java.io.File;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import org.marc4j.MarcHandler;
import org.marc4j.marc.Leader;
import org.marc4j.marc.MarcException;

/**
 * <p><code>MarcXmlHandler</code> is a SAX2 <code>ContentHandler</code>
 * that reports events to the <code>MarcHandler</code> interface.</p>
 *
 * @author Bas Peters
 * @see MarcHandler
 * @see DefaultHandler
 */
public class MarcXmlHandler extends DefaultHandler {

    /** Data element identifier */
    private String code;

    /** Tag name */
    private String tag;

    /** Leader object */
    private Leader leader;

    /** StringBuffer to store data */
    private StringBuffer data;

    /** MarcHandler object */
    private MarcHandler mh;

    /** Locator object */
    private Locator locator;

    /**
     * <p>Registers the <code>MarcHandler</code> object.  </p>
     *
     * @param mh the {@link MarcHandler} object
     */
    public void setMarcHandler(MarcHandler mh) {
	    this.mh = mh;
    }

    /**
     * <p>Registers the SAX2 <code>Locator</code> object.  </p>
     *
     * @param locator the {@link Locator} object
     */
    public void setDocumentLocator(Locator locator) {
	this.locator = locator;
    }

    public void startElement(String uri, String name, String qName, 
			     Attributes atts) throws SAXParseException {
	if (name.equals("collection")) {
	    if (mh != null)
		mh.startCollection();
	} else if (name.equals("leader")) {
	    data = new StringBuffer();
	} else if (name.equals("controlfield")) {
	    data = new StringBuffer();
	    if (atts.getLength() < 1)
		throw new SAXParseException("Invalid controlfield", locator);
	    tag = atts.getValue("tag");
	} else if (name.equals("datafield")) {
	    if (atts.getLength() < 3)
		throw new SAXParseException("Invalid datafield", locator);
	    tag = atts.getValue("tag");
	    String ind1 = atts.getValue("ind1");
	    String ind2 = atts.getValue("ind2");
	    mh.startDataField(tag, ind1.charAt(0), ind2.charAt(0));
	    data = new StringBuffer();
	} else if (name.equals("subfield")) {
	    code = atts.getValue("code");
	    data = new StringBuffer();
	}
    }

    public void characters(char[] ch, int start, int length) {
	if (data != null) {
	  data.append(ch, start, length);
	}
    }

    public void endElement(String uri, String name, String qName) 
	throws SAXParseException {
	if (name.equals("collection")) {
	    if (mh != null)
		mh.endCollection();
	} else if (name.equals("record")) {
	    if (mh != null)
		mh.endRecord();
	} else if (name.equals("leader")) {
	    try {
		mh.startRecord(new Leader(data.toString()));
	    } catch (MarcException e) {
		throw new SAXParseException("Unable to unmarshal leader", locator);
	    }
	} else if (name.equals("controlfield")) {
	    if (mh != null)
		mh.controlField(tag, data.toString().toCharArray());
	} else if (name.equals("datafield")) {
	    if (mh != null)
		mh.endDataField(tag);
	    tag = null;
	} else if (name.equals("subfield")) {
	    if (mh != null)
		mh.subfield(code.charAt(0), data.toString().toCharArray());
	    code = null;
	}
	data = null;
    }

}
