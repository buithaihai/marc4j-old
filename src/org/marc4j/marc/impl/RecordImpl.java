// $Id$
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
package org.marc4j.marc.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.marc4j.marc.ControlField;
import org.marc4j.marc.IllegalAddException;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;

/**
 * Represents a MARC record.
 * 
 * @author Bas Peters
 * @version $Revision$
 */
public class RecordImpl implements Record {

    private Leader leader;

    private List controlFields;

    private List dataFields;

    private String type;

    /**
     * Creates a new <code>Record</code>.
     */
    public RecordImpl() {
        controlFields = new ArrayList();
        dataFields = new ArrayList();
    }

    /**
     * Sets the type of record.
     * 
     * @param type
     *            the type of record
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the type of record.
     * 
     * @return String - the type of record
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the <code>Leader</code>.
     * 
     * @param leader
     *            the <code>Leader</code>
     */
    public void setLeader(Leader leader) {
        this.leader = leader;
    }

    /**
     * Returns the <code>Leader</code>.
     * 
     * @return Leader - the <code>Leader</code>
     */
    public Leader getLeader() {
        return leader;
    }

    /**
     * Adds a <code>VariableField</code> being a <code>ControlField</code>
     * or <code>DataField</code>.
     * 
     * If the <code>VariableField</code> is a control number field (001) and
     * the record already has a control number field, the field is replaced with
     * the new instance.
     * 
     * @param field
     *            the <code>VariableField</code>
     * @throws IllegalAddException
     *             when the parameter is not a <code>VariableField</code>
     *             instance
     */
    public void addVariableField(VariableField field) {
        if (field instanceof VariableField) {
            String tag = field.getTag();
            if (Verifier.isControlNumberField(tag)) {
                if (Verifier.hasControlNumberField(controlFields))
                    controlFields.set(0, field);
                else
                    controlFields.add(0, field);
            } else if (Verifier.isControlField(tag))
                controlFields.add(field);
            else
                dataFields.add(field);
        } else
            throw new IllegalAddException("Invalid object");
    }

    /**
     * Removes a variable field from the collection.
     * 
     * @param field
     *            the variable field
     */
    public void removeVariableField(VariableField field) {
        String tag = field.getTag();
        if (Verifier.isControlField(tag))
            controlFields.remove(field);
        else
            dataFields.remove(field);
    }

    /**
     * Returns the control number field or <code>null</code> if no control
     * number field is available.
     * 
     * @return ControlField - the control number field
     */
    public ControlField getControlNumberField() {
        if (Verifier.hasControlNumberField(controlFields))
            return (ControlField) controlFields.get(0);
        else
            return null;
    }

    /**
     * Returns a list of control fields
     * 
     * @return List - the control fields
     */
    public List getControlFields() {
        return controlFields;
    }

    /**
     * Returns a list of data fields
     * 
     * @return List - the data fields
     */
    public List getDataFields() {
        return dataFields;
    }

    /**
     * Returns the first instance of the variable field with the given tag.
     * 
     * @return VariableField - the variable field
     */
    public VariableField getVariableField(String tag) {
        Iterator i;
        if (Verifier.isControlField(tag))
            i = controlFields.iterator();
        else
            i = dataFields.iterator();
        while (i.hasNext()) {
            VariableField field = (VariableField) i.next();
            if (field.getTag().equals(tag))
                return field;
        }
        return null;
    }

    /**
     * Returns a list of variable fields with the given tag.
     * 
     * @return List - the variable fields
     */
    public List getVariableFields(String tag) {
        List fields = new ArrayList();
        Iterator i;
        if (Verifier.isControlField(tag))
            i = controlFields.iterator();
        else
            i = dataFields.iterator();
        while (i.hasNext()) {
            VariableField field = (VariableField) i.next();
            if (field.getTag().equals(tag))
                fields.add(field);
        }
        return fields;
    }

    /**
     * Returns a list of variable fields
     * 
     * @return List - the variable fields
     */
    public List getVariableFields() {
        List fields = new ArrayList();
        Iterator i;
        i = controlFields.iterator();
        while (i.hasNext())
            fields.add(i.next());
        i = dataFields.iterator();
        while (i.hasNext())
            fields.add(i.next());
        return fields;
    }

    public String getControlNumber() {
        return new String(getControlNumberField().getData());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.marc4j.marc.Record#getVariableFields(java.lang.String[])
     */
    public List getVariableFields(String[] tags) {
        List list = new ArrayList();
        for (int i = 0; i < tags.length; i++) {
            String tag = tags[i];
            List fields = getVariableFields(tag);
            if (fields.size() > 0)
                list.addAll(fields);
        }
        return list;
    }

    /**
     * Returns a string representation of this record.
     * 
     * <p>Example:
     * 
     * <pre>
     * 
     *  LEADER 00714cam a2200205 a 4500 
     *  001 12883376 
     *  005 20030616111422.0
     *  008 020805s2002 nyu j 000 1 eng 
     *  020   $a0786808772 
     *  020   $a0786816155 (pbk.) 
     *  040   $aDLC$cDLC$dDLC 
     *  100 1 $aChabon, Michael. 
     *  245 10$aSummerland /$cMichael Chabon. 
     *  250   $a1st ed. 
     *  260   $aNew York :$bMiramax Books/Hyperion Books for Children,$cc2002. 
     *  300   $a500 p. ;$c22 cm. 
     *  650  1$aFantasy. 
     *  650  1$aBaseball$vFiction. 
     *  650  1$aMagic$vFiction.
     *  
     * </pre>
     * 
     * @return String - a string representation of this record
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("LEADER ");
        sb.append(getLeader().toString());
        sb.append('\n');
        Iterator i = getVariableFields().iterator();
        while (i.hasNext()) {
            VariableField field = (VariableField) i.next();
            sb.append(field.toString());
            sb.append('\n');
        }
        return sb.toString();
    }
}