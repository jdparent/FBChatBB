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

package org.biff.auth;

import java.util.Hashtable;
import net.sourceforge.jxa.Base64;

/**
 * XMPP SASL Request message.
 *
 * Ported from the C# version written by Charles Chen <www.charliedigital.com>
 * @version 0.0.1
 * @author Jeff Parent
 */
public class SaslChallenge
{
  private String algorithm;
  private String charset;
  private String nonce;
  private String qop;
  private String realm;

  public SaslChallenge(
    String algorithm,
    String charset,
    String nonce,
    String qop,
    String realm)
  {
    this.algorithm = algorithm;
    this.charset = charset;
    this.nonce = nonce;
    this.qop = qop;
    this.realm = realm;
  }
  
  private SaslChallenge(String rawDecodedText)
  {
    Hashtable values = getQueryTable(rawDecodedText);

    algorithm = values.get("algorithm").toString();
    charset = values.get("charset").toString();
    nonce = values.get("nonce").toString();
    qop = values.get("qop").toString();
    realm = values.get("realm").toString();
  }

  public String getAlgorithm()
  {
    return algorithm;
  }

  public String getCharset()
  {
    return charset;
  }

  public String getNonce()
  {
    return nonce;
  }

  public String getQop()
  {
    return qop;
  }

  public String getRealm()
  {
    return realm;
  }

  public static SaslChallenge parse(String encodedString)
  {
    byte[] decoded = Base64.decode(encodedString);

    return new SaslChallenge(new String(decoded));
  }

  private static Hashtable getQueryTable(String query)
  {
    Hashtable values = new Hashtable();

    int s = 0;
    int e = 0;

    while (s != -1)
    {
      e = query.indexOf("=", s);
      String name = query.substring(s, e);
      s = e + 1;
      e = query.indexOf(",", s);

      if (e < 0)
      {
        String val = query.substring(s, query.length());
        
        if (val.charAt(0) == '\"')
        {
          val = val.substring(1);
        }
        
        if (val.charAt(val.length() - 1) == '\"')
        {
          val = val.substring(0, val.length() - 1);
        }
        
        values.put(name, val);
        s = -1;
      }
      else
      {
        String val = query.substring(s, e);
        
        if (val.charAt(0) == '\"')
        {
          val = val.substring(1);
        }
        
        if (val.charAt(val.length() - 1) == '\"')
        {
          val = val.substring(0, val.length() - 1);
        }
        
        values.put(name, val);
        s = e + 1;
      }
    }

    return values;
  }

}
