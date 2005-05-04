//$Id$
/**
 * Copyright (C) 2004 Bas Peters
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

import java.io.Serializable;

/**
 * Represents a variable field in a MARC record.
 *
 * @author Bas Peters
 * @version $Revision$
 */
public interface VariableField extends Serializable {

  /**
   * Returns the tag name.
   * 
   * @return String - the tag name
   */
  public String getTag();
  
  /**
   * Sets the tag name.
   * 
   * @param tag
   *          the tag name
   */
  public void setTag(String tag); 
  
}
