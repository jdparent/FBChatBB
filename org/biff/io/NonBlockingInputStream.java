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
package org.biff.io;

import java.io.IOException;
import java.io.InputStream;
import net.rim.device.api.util.ByteVector;

/**
 *
 * @author jdpare
 */
public class NonBlockingInputStream extends InputStream implements Runnable
{
  private InputStream in;
  private Thread thread;
  private ByteVector buf;
  private boolean isClosed;

  public NonBlockingInputStream(InputStream in)
  {
    this.in = in;
    this.buf = new ByteVector();
    this.isClosed = false;

    this.thread = new Thread(this);

    this.thread.start();
  }

  public int read () throws IOException
  {
    if (available() > 0)
    {
      int b = (int)this.getByte();

      return b;
    }
    else
    {
      throw new IOException("Stream is empty.");
    }
  }

  public void close()
  {
    this.isClosed = true;
  }

  public int available()
  {
    return this.byteCount();
  }

  private synchronized void addByte(byte b)
  {
    this.buf.addElement(b);
  }
  
  private synchronized void addBytes(byte[] data, int read)
  {
    for (int i = 0; i < read; i++)
    {
      this.buf.addElement(data[i]);
    }
  }

  private synchronized int byteCount()
  {
    return this.buf.size();
  }

  private synchronized byte getByte()
  {
    byte b = this.buf.firstElement();

    this.buf.removeElement(b);

    return b;
  }

  public void run ()
  {
    int byteRead = 0;
    
    while (this.isClosed == false)
    {
      try
      {
        while((byteRead = in.read()) > 0)
        {
          addByte((byte)byteRead);
        }
      }
      catch (IOException e) {}
    }
  }
}
