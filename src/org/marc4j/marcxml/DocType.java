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

/**
 * <p>This class defines behaviour for a document type declaration.  </p>
 *
 * @author Bas Peters
 */
public class DocType {

    String name;
    String publicId;
    String systemId;

    /** Default constructor */
    public DocType() {}

    /**
     * <p>Creates a new DocType instance</p>
     *
     * @param name the name of the root element
     * @param publicId the public identifier
     * @param systemId the system identifier
     */
    public DocType(String name, String publicId, String systemId) {
	setName(name);
	setPublicId(publicId);
	setSystemId(systemId);
    }

    /**
     * <p>Sets the name of the root element.</p>
     *
     * @param name the name of the root element
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * <p>Sets the public identifier.</p>
     *
     * @param publicId the public identifier
     */
    public void setPublicId(String publicId) {
	this.publicId = publicId;
    }

    /**
     * <p>Sets the system identifier.</p>
     *
     * @param systemId the system identifier
     */
    public void setSystemId(String systemId) {
	this.systemId = systemId;
    }

    /**
     * <p>Returns the name of the root element.</p>
     *
     * @return {@link String} - the name of the root element
     */
    public String getName() {
	return name;
    }

    /**
     * <p>Returns the public identifier.</p>
     *
     * @return {@link String} - the public identifier
     */
    public String getPublicId() {
	return publicId;
    }

    /**
     * <p>Returns the system identifier.</p>
     *
     * @return {@link String} - the system identifier
     */
    public String getSystemId() {
	return systemId;
    }

}


