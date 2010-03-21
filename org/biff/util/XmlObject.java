/*
 * Copyright (c) 2010 Jeff Parent. All rights reserved.
 *
 * License: LGPL
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package org.biff.util;

import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author jdpare
 */
public class XmlObject
{
  private String tag;
  private Hashtable attributes;
  private Vector subObjects;
  private String text;

  public XmlObject()
  {
    tag = new String("");
    attributes = new Hashtable();
    subObjects = new Vector();
    text = null;
  }

  public void setTag(final String tag)
  {
    this.tag = tag;
  }

  public String getTag()
  {
    return tag;
  }

  public void setText(final String text)
  {
    this.text = new String(text);
  }
  
  public boolean hasText()
  {
    return (text != null);
  }

  public String getText()
  {
    return text;
  }

  public void addAttribute(final String key, final String value)
  {
    attributes.put(key, value);
  }

  public int attributeCount()
  {
    return attributes.size();
  }

  public String getAttribute(final String key)
  {
    return (String)attributes.get(key);
  }

  public void addSubObject(XmlObject obj)
  {
    this.subObjects.addElement(obj);
  }

  public int subObjectCount()
  {
    return this.subObjects.size();
  }

  public XmlObject getSubObject(int index)
  {
    XmlObject obj = null;

    if (subObjectCount() > index)
    {
      obj = (XmlObject) this.subObjects.elementAt(index);
    }
    return obj;
  }

  public XmlObject getSubObject(final String tag)
  {
    XmlObject obj = null;

    for (int i = 0; i < subObjectCount(); i++)
    {
      XmlObject tmp = getSubObject(i);

      if (tmp.getTag().equals(tag))
      {
        obj = tmp;

        i = subObjectCount() + 1;
      }
    }

    return obj;
  }
}
