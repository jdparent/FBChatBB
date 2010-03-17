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

package org.biff.fbchatbb;

import java.io.IOException;
import java.util.Vector;
import java.lang.Thread;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import net.sourceforge.jxa.XmlReader;
import net.sourceforge.jxa.XmlWriter;
import net.sourceforge.jxa.XmppListener;
import org.biff.auth.SaslChallenge;
import org.biff.auth.SaslChallengeResponse;

import net.sourceforge.jxa.Base64;

/**
 *
 * @author jdpare
 */
public class FBChatAPI extends Thread
{
  private final String username, password, myId;
  private final String host = "chat.facebook.com";
  private String sessionKey;
  private XmlReader reader;
  private XmlWriter writer;

  private Vector listeners = new Vector();

  public FBChatAPI(final String username, final String password)
  {
    this.username = username;
    this.password = password;

    this.myId = this.username + "@" + this.host;
    this.sessionKey = null;
  }

  public void run()
  {
    try
    {
      final StreamConnection connection =
        (StreamConnection)Connector.open("socket://" + this.host + ":5222");

      this.reader = new XmlReader(connection.openInputStream());
      this.writer = new XmlWriter(connection.openOutputStream());
    }
    catch (final Exception e)
    {
      java.lang.System.out.println(e);
      this.connectionFailure(e.toString());
      return;
    }

    java.lang.System.out.println("connected");

    try
    {
      this.login();
      this.parse();
    }
    catch (final Exception e)
    {
      this.connectionFailed(e.toString());
    }
  }

  public void addListener(final XmppListener xl)
  {
    if (!listeners.contains(xl))
    {
      listeners.addElement(xl);
    }
  }

  public void removeListener(final XmppListener xl)
  {
    listeners.removeElement(xl);
  }

  public void login() throws IOException, InterruptedException
  {
    // Send Version
    
    // Start Stream
    this.writer.startTag("stream:stream");
    this.writer.attribute("to", this.host);
    this.writer.attribute("xmlns", "jabber:client");
    this.writer.attribute("xmlns:stream", "http://etherx.jabber.org/streams");
    this.writer.attribute("version", "1.0");
    this.writer.flush();
    
    java.lang.Thread.sleep(100);

    // Read Features
    do
    {
      this.reader.next();
    } while((reader.getType() != XmlReader.END_TAG) ||
      (!reader.getName().equals("stream:features")));
    
    // Send Login type request
    this.writer.startTag("auth");
    this.writer.attribute("xmlns", "urn:ietf:params:xml:ns:xmpp-sasl");
    this.writer.attribute("mechanism", "DIGEST-MD5");
    this.writer.attribute("xmlns:ga", "http://www.google.com/talk/protocol/auth");
    this.writer.attribute("ga:client-uses-full-bind-result", "true");
    this.writer.endTag();
    this.writer.flush();
    
    java.lang.Thread.sleep(100);
    
    // Read Challenge
    do
    {
      this.reader.next();
    } while((reader.getType() != XmlReader.START_TAG) &&
      (!reader.getName().equals("challenge")));

    if (reader.getName().equals("challenge"))
    {
      reader.next();
      
      SaslChallenge challenge = SaslChallenge.parse(reader.getText());

      SaslChallengeResponse response =
        new SaslChallengeResponse(challenge, this.username, this.password);

      this.writer.startTag("response");
      this.writer.attribute("xmlns", "urn:ietf:params:xml:ns:xmpp-sasl");
      this.writer.text(response.getEncodedContent());
      this.writer.endTag();
      this.writer.flush();

      java.lang.Thread.sleep(100);
      
      // Read rspauth
      do
      {
        this.reader.next();
      } while((this.reader.getType() != XmlReader.END_TAG) ||
        (!this.reader.getName().equals("challenge")));

      // Send empty response
      this.writer.startTag("response");
      this.writer.attribute("xmlns", "urn.ietf:params:xml:ns:xmpp-sasl");
      this.writer.endTag();
      this.writer.flush();

      java.lang.Thread.sleep(100);
      
      // Read Success
      do
      {
        this.reader.next();
      } while((this.reader.getType() != XmlReader.END_TAG) ||
        (!this.reader.getName().equals("success")));

    }
    else
    {
      throw new IOException("Login failed");
    }
  }

  private void parse ()
  {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  private void connectionFailure (String toString)
  {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  private void connectionFailed (String toString)
  {
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
