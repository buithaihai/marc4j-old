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
package org.marc4j.marc;

/**
 * <p><code>Subfield</code> defines behaviour for a subfield (a data
 * element within a data field).  </p>
 *
 * <p>A subfield consists of a delimiter followed by a data element
 * identifier (together the subfield code) and a data element. The structure
 * of a data element according to the MARC standard is as follows:</p>
 * <pre>
 * DELIMITER DATA_ELEMENT_IDENTIFIER DATA_ELEMENT
 * </pre>
 * <p>This structure is returned by the {@link #marshal()}
 * method.</p>
 *
 * @author Bas Peters
 */
public class Subfield {

    private static final char US = MarcConstants.US;

    /** The data element identifier. */
    private char code;

    /** The data element. */
    private char[] data;

    /** Default constructor */
    public Subfield() {}

    /**
     * <p>Creates a new <code>Subfield</code> instance and registers the
     * data element identifier and the data element.</p>
     *
     * @param code the data element identifier
     * @param data the data element
     */
    public Subfield(char code, char[] data) {
        setCode(code);
        setData(data);
    }

    /**
     * <p>Creates a new <code>Subfield</code> instance and registers the
     * data element identifier and the data element.</p>
     *
     * @param code the data element identifier
     * @param data the data element
     */
    public Subfield(char code, String data) {
        setCode(code);
        setData(data.toCharArray());
    }

    /**
     * <p>Registers the data element identifier.</p>
     *
     * @param code the data element identifier
     * @throws IllegalIdentifierException when the data element identifier
     *                                    is not a valid data element
     *                                    identifier
     */
    public void setCode(char code) {
	this.code = code;
    }

    /**
     * <p>Registers the data element.</p>
     *
     * @param data the data element
     */
    public void setData(char[] data) {
	    this.data = data;
    }

    /**
     * <p>Registers the data element.</p>
     *
     * @param data the data element
     */
    public void setData(String data) {
	    this.data = data.toCharArray();
    }

    /**
     * <p>Returns the data element identifier.</p>
     *
     * @return <code>char</code> - the data element identifier
     */
    public char getCode() {
	    return code;
    }

    /**
     * <p>Returns the data element.</p>
     *
     * @return <code>char[]</code> - the data element
     */
    public char[] getData() {
	    return data;
    }

    /**
     * <p>Returns a <code>String</code> representation for a data element
     * following the structure of a MARC data element.</p>
     *
     * @return <code>String</code> - the data element
     */
    public String marshal() {
        return new StringBuffer()
	    .append(US)
	    .append(code)
            .append(data)
	    .toString();
    }

}
