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
package org.marc4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Leader;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.impl.Verifier;

/**
 * An iterator over a collection of MARC records in ISO 2709 format.
 * <p>
 * Example usage:
 * 
 * <pre>
 * InputStream input = new FileInputStream(&quot;file.mrc&quot;);
 * MarcReader reader = new MarcStreamReader(input);
 * while (reader.hasNext()) {
 *   Record record = reader.next();
 *   // Process record
 * }
 * </pre>
 * 
 * <p>
 * When no encoding is given as an constructor argument the parser tries to
 * resolve the encoding by looking at the character coding scheme (leader
 * position 9) in MARC21 records. For UNIMARC records this position is not
 * defined.
 * </p>
 * 
 * @author Bas Peters
 * @version $Revision$
 *  
 */
public class MarcStreamReader implements MarcReader {

  private InputStream input = null;

  private Record record;

  private MarcFactory factory;

  private String encoding = null;

  private boolean hasNext = true;

  /**
   * Constructs an instance with the specified input stream.
   */
  public MarcStreamReader(InputStream input) {
    this(input, null);
  }

  /**
   * Constructs an instance with the specified input stream and character
   * encoding.
   */
  public MarcStreamReader(InputStream input, String encoding) {
    this.input = input;
    factory = MarcFactory.newInstance();
    if (encoding != null)
      this.encoding = encoding;
  }

  /**
   * Returns true if the iteration has more records, false otherwise.
   */
  public boolean hasNext() {
    try {
      if (input.available() == 0)
        return false;
    } catch (IOException e) {
      throw new MarcException(e.getMessage(), e);
    }
    return true;
  }

  /**
   * Returns the next record in the iteration.
   * 
   * @return Record - the record object
   */
  public Record next() {
    Leader ldr;
    int bytesRead = 0;

    record = factory.newRecord();

    try {

      byte[] byteArray = new byte[24];
      bytesRead = input.read(byteArray);

      if (bytesRead == -1)
        throw new MarcException("No data to read");

      while (bytesRead != -1 && bytesRead != byteArray.length)
        bytesRead += input.read(byteArray, bytesRead, byteArray.length
            - bytesRead);

      ldr = parseLeader(byteArray);

      switch (ldr.getCharCodingScheme()) {
      case '#':
        if (encoding == null)
          encoding = "ISO8859_1";
        break;
      case 'a':
        if (encoding == null)
          encoding = "UTF-8";
      }

      record.setLeader(ldr);

      int directoryLength = ldr.getBaseAddressOfData() - (24 + 1);
      if ((directoryLength % 12) != 0)
        throw new MarcException("Directory invalid");
      int size = directoryLength / 12;

      String[] tags = new String[size];
      int[] lengths = new int[size];

      byte[] tag = new byte[3];
      byte[] length = new byte[4];
      byte[] start = new byte[5];

      String tmp;

      for (int i = 0; i < size; i++) {
        bytesRead = input.read(tag);

        while (bytesRead != -1 && bytesRead != tag.length)
          bytesRead += input.read(tag, bytesRead, tag.length - bytesRead);

        tmp = new String(tag);
        tags[i] = tmp;

        bytesRead = input.read(length);

        while (bytesRead != -1 && bytesRead != length.length)
          bytesRead += input.read(length, bytesRead, length.length - bytesRead);

        tmp = new String(length);
        lengths[i] = Integer.parseInt(tmp);

        bytesRead = input.read(start);

        while (bytesRead != -1 && bytesRead != start.length)
          bytesRead += input.read(start, bytesRead, start.length - bytesRead);
      }

      if (input.read() != Constants.FT)
        throw new MarcException("Directory not terminated");

      for (int i = 0; i < size; i++) {
        if (Verifier.isControlField(tags[i])) {
          byteArray = new byte[lengths[i] - 1];
          bytesRead = input.read(byteArray);

          while (bytesRead != -1 && bytesRead != byteArray.length)
            bytesRead += input.read(byteArray, bytesRead, byteArray.length
                - bytesRead);

          if (input.read() != Constants.FT)
            throw new MarcException("Field not terminated");

          ControlField field = factory.newControlField();
          field.setTag(tags[i]);
          field.setData(getDataAsString(byteArray));
          record.addVariableField(field);

        } else {
          byteArray = new byte[lengths[i]];
          bytesRead = input.read(byteArray);

          while (bytesRead != -1 && bytesRead != byteArray.length)
            bytesRead += input.read(byteArray, bytesRead, byteArray.length
                - bytesRead);

          record.addVariableField(parseDataField(tags[i], byteArray));
        }
      }

      if (input.read() != Constants.RT)
        throw new MarcException("Record not terminated");

    } catch (IOException e) {
      throw new RuntimeException("An error occured reading input", e);
    }
    return record;
  }

  private DataField parseDataField(String tag, byte[] field) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(field);
    char ind1 = (char) bais.read();
    char ind2 = (char) bais.read();

    DataField dataField = factory.newDataField();
    dataField.setTag(tag);
    dataField.setIndicator1(ind1);
    dataField.setIndicator2(ind2);

    int code;
    int size;
    int readByte;
    byte[] data;
    Subfield subfield;
    while (true) {
      readByte = bais.read();
      if (readByte < 0)
        break;
      switch (readByte) {
      case Constants.US:
        code = bais.read();
        size = getSubfieldLength(bais);
        data = new byte[size];
        bais.read(data);
        subfield = factory.newSubfield();
        subfield.setCode((char) code);
        subfield.setData(getDataAsString(data));
        dataField.addSubfield(subfield);
        break;
      case Constants.FT:
        break;
      }
    }
    return dataField;
  }

  private int getSubfieldLength(ByteArrayInputStream bais) throws IOException {
    bais.mark(9999);
    int bytesRead = 0;
    while (true) {
      switch (bais.read()) {
      case Constants.US:
      case Constants.FT:
        bais.reset();
        return bytesRead;
      default:
        bytesRead++;
      }
    }
  }

  private Leader parseLeader(byte[] leaderData) throws IOException {
    InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(
        leaderData));
    Leader ldr = factory.newLeader();
    char[] tmp = new char[5];
    isr.read(tmp);
    ldr.setRecordLength(Integer.parseInt(new String(tmp)));
    ldr.setRecordStatus((char) isr.read());
    ldr.setTypeOfRecord((char) isr.read());
    tmp = new char[2];
    isr.read(tmp);
    ldr.setImplDefined1(tmp);
    ldr.setCharCodingScheme((char) isr.read());
    ldr.setIndicatorCount(Integer.parseInt(String.valueOf((char) isr.read())));
    ldr.setSubfieldCodeLength(Integer.parseInt(String
        .valueOf((char) isr.read())));
    tmp = new char[5];
    isr.read(tmp);
    ldr.setBaseAddressOfData(Integer.parseInt(new String(tmp)));
    tmp = new char[3];
    isr.read(tmp);
    ldr.setImplDefined2(tmp);
    tmp = new char[4];
    isr.read(tmp);
    ldr.setEntryMap(tmp);
    isr.close();
    return ldr;
  }

  private String getDataAsString(byte[] bytes) {
    String dataElement = null;
    if (encoding != null)
      try {
        dataElement = new String(bytes, encoding);
      } catch (UnsupportedEncodingException e) {
        throw new MarcException("UnsupportedEncodingException", e);
      }
    else
      dataElement = new String(bytes);
    return dataElement;
  }

}