// $Id$
/**
 * Copyright (C) 2002 Bas Peters
 *
 * This file is part of MARC4J
 *
 * MARC4J is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public 
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 *
 * MARC4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with MARC4J; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.marc4j.marc;

/**
 * <p><code>IllegalTagException</code> is thrown when a tag is supplied
 * that is invalid.  </p>
 *
 * @author <a href="mailto:mail@bpeters.com">Bas Peters</a> 
 * @version $Revision$
 *
 */
public class IllegalTagException extends IllegalArgumentException {

    /**
     * <p>Creates an <code>Exception</code> indicating that the name of the
     * tag is invalid.</p>
     *
     * @param tag the tag name
     * @see Tag
     */
    public IllegalTagException(String tag) {
	super(new StringBuffer()
	    .append("The tag name ")
	    .append(tag)
	    .append(" is not a valid tag name.")
	    .toString());
  }

    /**
     * <p>Creates an <code>Exception</code> indicating that the name of the
     * tag is invalid.</p>
     *
     * @param tag the tag name
     * @param reason the reason why the exception is thrown
     */
    public IllegalTagException(String tag, String reason) {
	super(new StringBuffer()
	    .append("The tag name ")
	    .append(tag)
	    .append(" is not valid: ")
	    .append(reason)
	    .append(".")
	    .toString());
  }

}
