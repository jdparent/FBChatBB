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

import java.util.Random;
import javax.bluetooth.UUID;
import net.sourceforge.jxa.Base64;
import org.biff.util.MD5;

/**
 * XMPP SASL Response message.
 *
 * Ported from the C# version written by Charles Chen <www.charliedigital.com>
 * @version 0.0.1
 * @author Jeff Parent
 */
public class SaslChallengeResponse
{
  private SaslChallenge challenge;
  private String cnonce;
  private String digestUri;
  private String password;
  private String username;
  private String realm;
  private String decodedContent;
  private String encodedContent;

  private String a1Md5HashHex;
  private String a2Md5HashHex;
  private String responseTokenMd5HashHex;
  private String userTokenMd5HashHex;

  private MD5 md5;

  public String getEncodedContent()
  {
    return this.encodedContent;
  }

  public String getDecodecContent()
  {
    return this.decodedContent;
  }

  public String getUserTokenMd5HashHex()
  {
    return this.userTokenMd5HashHex;
  }

  public String getResponseTokenMd5HashHex()
  {
    return this.responseTokenMd5HashHex;
  }

  public String getA1Md5HashHex()
  {
    return this.a1Md5HashHex;
  }

  public String getA2Md5HashHex()
  {
    return this.a2Md5HashHex;
  }

  public SaslChallengeResponse(
    SaslChallenge challenge,
    String username,
    String password)
  {
    this(challenge, username, password, null, null, null);
  }

  public SaslChallengeResponse(
    SaslChallenge challenge,
    String username,
    String password,
    String cnonce)
  {
    this(challenge, username, password, null, null, cnonce);
  }

  public SaslChallengeResponse(
    SaslChallenge challenge,
    String username,
    String password,
    String realm,
    String digestUri,
    String cnonce)
  {
    this.md5 = new MD5();
    
    this.challenge = challenge;
    this.username = username;
    this.password = password;

    if (this.challenge.getRealm() == null)
    {
      this.realm = realm;
    }
    else
    {
      this.realm = this.challenge.getRealm();
    }

    if (this.realm == null)
    {
      throw new IllegalArgumentException("No realm was specified.");
    }

    if ((cnonce == null) || (cnonce.length() == 0))
    {
      Random r = new Random();
      
      r.setSeed(System.currentTimeMillis());

      long val = Math.abs(r.nextInt());
      
      if (val > 0xFFFFFFFF)
      {
        val = val - 0xFFFFFFFF;
      }
      
      UUID guid = new UUID(val);
      
      String gst = guid.toString() + guid.toString() + guid.toString() + guid.toString();

      this.cnonce = Base64.encode(gst.getBytes());
    }
    else
    {
      this.cnonce = cnonce;
    }

    if ((digestUri == null) || (digestUri.length() == 0))
    {
      this.digestUri = "xmpp/" + this.realm;
    }
    else
    {
      this.digestUri = digestUri;
    }

    this.decodedContent = getDecodedContent();

    byte[] bytes = this.decodedContent.getBytes();

    this.encodedContent = Base64.encode(bytes);
    
    byte[] test = Base64.decode(this.encodedContent);
    
    if (test[0] == bytes[0])
    {
      java.lang.System.out.println("ok");
    }
  }

  public String getDecodedContent()
  {
    this.responseTokenMd5HashHex = getResponse();

    String buf =
      "username=\"" +
      this.username +
      "\",realm=\"" +
      this.realm +
      "\",nonce=\"" +
      this.challenge.getNonce() +
      "\",cnonce=\"" +
      this.cnonce +
      "\",nc=00000001,qop=auth,maxbuf=4096,digest-uri=\"" +
      this.digestUri +
      "\",response=" +
      this.responseTokenMd5HashHex +
      ",charset=utf-8";

    return buf;
  }

  private String getResponse()
  {
    byte[] a1 = getA1();
    String a2 = getA2();

    byte[] a2Bytes = a2.getBytes();

    byte[] a1Hash = md5.computeHash(a1);

    byte[] a2Hash = md5.computeHash(a2Bytes);

    this.a1Md5HashHex = convertToBase16String(a1Hash);
    this.a2Md5HashHex = convertToBase16String(a2Hash);

    String kdString =
      this.a1Md5HashHex + ":" +
      this.challenge.getNonce() + ":" +
      "00000001:" +
      this.cnonce + ":" +
      "auth:" +
      this.a2Md5HashHex;

    byte[] kdBytes = kdString.getBytes();

    byte[] kd = md5.computeHash(kdBytes);
    
    String kdBase16 = convertToBase16String(kd);

    return kdBase16;
  }

  private byte[] getA1 ()
  {
    String userToken =
      this.username + ":" +
      this.realm + ":" +
      this.password;

    byte[] bytes = userToken.getBytes();

    byte[] md5Hash = md5.computeHash(bytes);

    this.userTokenMd5HashHex = convertToBase16String(md5Hash);

    String nonces =
      ":" +
      this.challenge.getNonce() + ":" +
      this.cnonce;

    byte[] nonceBytes = nonces.getBytes();

    byte[] result = new byte[md5Hash.length + nonceBytes.length];

    for (int i = 0; i < md5Hash.length; i++)
    {
      result[i] = md5Hash[i];
    }

    for (int i = 0; i < nonceBytes.length; i++)
    {
      result[md5Hash.length + i] = nonceBytes[i];
    }

    return result;
  }

  private String getA2 ()
  {
    String result = "AUTHENTICATE:" + this.digestUri;

    return result;
  }

  private String convertToBase16String (byte[] in)
  {
    final byte[] HexChars =
    {
      '0', '1', '2', '3', '4', '5',
      '6', '7', '8', '9', 'a', 'b',
      'c', 'd', 'e', 'f'
    };

    StringBuffer buf = new StringBuffer();

    for (int i = 0; i < in.length; i++)
    {
      int v = in[i] & 0xFF;

      buf.append((char)HexChars[v >> 4]);
      buf.append((char)HexChars[v & 0x0F]);
    }

    return buf.toString();
  }
}
