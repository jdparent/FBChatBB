/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
