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

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p><code>Collection</code> defines behaviour for a collection of 
 * <code>Record</code> objects.  </p>
 *
 * @author Bas Peters
 */
public class Collection {

    private List list;

    public Collection() {
	list = new ArrayList();
    }

    public void add(Record record) {
	list.add(record);
    }

    /**
     * <p>Returns the Record object for the given index.</p>
     *
     * @param index the index of the record object
     * @return Record the {@link Record} object
     */
    public Record getRecord(int index) {
	if (list.size() < index)
	    return null;
	return (Record)list.get(index);
    }

    /**
     * <p>Returns the number of records in the collection.</p>
     *
     * @return int the number of records in the collection
     */
    public int getSize() {
	return list.size();
    }

    /**
     * <p>Marshals all the records in the collection 
     * and writes the tape format records to the Writer 
     * object.</p>
     *
     * @param out the {@link Writer} object
     */
    public void marshal(Writer out) 
	throws IOException, MarcException {
	for (Iterator i = list.iterator(); i.hasNext();) {
	    Record record = (Record)i.next();
	    out.write(record.marshal());
	}
    }

}
