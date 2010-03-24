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

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.sourceforge.jxa.XmlWriter;

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
  private boolean hasEndTag;

  public XmlObject()
  {
    tag = new String("");
    attributes = new Hashtable();
    subObjects = new Vector();
    text = null;
    hasEndTag = false;
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

  public void setEndTag()
  {
    hasEndTag = true;
  }

  public boolean hasEndTag()
  {
    return this.hasEndTag;
  }

  public void addAttribute(final String key, final String value)
  {
    attributes.put(key, value);
  }

  public int attributeCount()
  {
    return attributes.size();
  }

  public Enumeration getAttributeKeys()
  {
    return attributes.keys();
  }

  public String getAttribute(final String key)
  {
    return (String)attributes.get(key);
  }

  public String getAttribute(final Object key)
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

  public boolean hasSubObjects()
  {
    return (subObjectCount() > 0);
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

  public void write (XmlWriter writer)
  {
    if (writer != null)
    {
      writeData(this, writer);
      try
      {
        writer.flush();
      }
      catch (IOException ex)
      {
        ex.printStackTrace();
      }
    }
  }

  private void writeData (XmlObject obj, XmlWriter writer)
  {
    try
    {
      writer.startTag(obj.getTag());

      Enumeration k = obj.getAttributeKeys();
      while (k.hasMoreElements())
      {
        String key = (String)k.nextElement();
        String value = obj.getAttribute(key);
        writer.attribute(key, value);
      }

      if (obj.hasText())
      {
        writer.text(obj.getText());
      }

      if (obj.hasSubObjects())
      {
        for (int i = 0; i < obj.subObjectCount(); i++)
        {
          XmlObject so = obj.getSubObject(i);

          writeData(so, writer);
        }
      }

      if (obj.hasEndTag())
      {
        writer.endTag();
      }
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
  }
}
