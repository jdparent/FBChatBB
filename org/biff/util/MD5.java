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
import net.rim.device.api.crypto.DigestOutputStream;
import net.rim.device.api.crypto.MD5Digest;

/**
 *
 * @author jdpare
 */
public class MD5
{
  private MD5Digest digest;
  private DigestOutputStream dos;

  public MD5()
  {
    digest = new MD5Digest();
    dos = new DigestOutputStream(digest, null);
  }

  public byte[] computeHash(byte[] in)
  {
    byte[] out = null;

    digest.reset();

    try
    {
      dos.write(in);
      out = digest.getDigest();
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
    
    return out;
  }
}
