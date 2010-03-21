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
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import net.sourceforge.jxa.XmlReader;
import net.sourceforge.jxa.XmlWriter;
import net.sourceforge.jxa.XmppListener;
import org.biff.auth.SaslChallenge;
import org.biff.auth.SaslChallengeResponse;
import org.biff.util.XmlObject;


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


  private final static int OFF_STATE = 0;
  private final static int STREAM_STREAM_STATE = 1;
  private final static int STREAM_FEATURES_STATE = 2;
  private final static int AUTH_STATE = 3;
  private final static int RESPONSE_STATE = 4;
  private final static int IQ_STATE = 5;
  private final static int PRESENCE_STATE = 6;

  private int state;
  private boolean isRunning;

  public FBChatAPI(final String username, final String password)
  {
    this.username = username;
    this.password = password;

    this.myId = this.username + "@" + this.host;
    this.sessionKey = null;

    state = OFF_STATE;

    isRunning = false;
  }

  public void run()
  {
    isRunning = true;

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

      for (int i = 0; i < listeners.size(); i++)
      {
        XmppListener l = (XmppListener)listeners.elementAt(i);

        l.onConnFailed("Failed to open connection.");
      }

      isRunning = false;
      return;
    }

    this.login();

    while (isRunning) // Loop for state machine
    {
      if (!readData())
      {
        try
        {
          java.lang.Thread.sleep(100);
        }
        catch (InterruptedException ex)
        {
          ex.printStackTrace();
        }
      }
    }

    // Disconnect has been called
  }

  public void disconnect()
  {
    isRunning = false;
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

  public void login()
  {
    if (this.state == OFF_STATE)
    {
      state = FBChatAPI.STREAM_STREAM_STATE;

      try
      {
        // Start Stream
        this.writer.startTag("stream:stream");
        this.writer.attribute("to", this.host);
        this.writer.attribute("xmlns", "jabber:client");
        this.writer.attribute("xmlns:stream",
          "http://etherx.jabber.org/streams");
        this.writer.attribute("version", "1.0");
        this.writer.flush();
      }
      catch (IOException ex)
      {
        for (int i = 0; i < listeners.size(); i++)
        {
          XmppListener l = (XmppListener)listeners.elementAt(i);

          l.onAuthFailed("Failed sending <stream:stream>");
        }

        state = FBChatAPI.OFF_STATE;
      }

    }
  }

  public void login2() throws IOException, InterruptedException
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

  private boolean readData ()
  {
    boolean receivedData = false;

    if (this.reader.available() > 0)
    {
      try
      {
        do
        {
          this.reader.next();
        } while ((this.reader.getType() != XmlReader.START_TAG) &&
          (this.reader.getType() != XmlReader.END_DOCUMENT));

        if (this.reader.getType() == XmlReader.START_TAG)
        {
          XmlObject obj = readXml();

          if (obj != null)
          {
            receivedData = true;

            event(obj);
          }
        }

      }
      catch (IOException e)
      {

      }
    }
    return receivedData;
  }

  private XmlObject readXml()
  {
    XmlObject obj = null;

    if (this.reader.getType() == XmlReader.START_TAG)
    {
      obj = new XmlObject();

      // Read Tag
      obj.setTag(this.reader.getName());

      // Read Attributes
      Enumeration k = this.reader.getAttributes();
      while (k.hasMoreElements())
      {
        String key = (String)k.nextElement();
        String value = (String)this.reader.getAttribute(key);

        obj.addAttribute(key, value);
      }

      try
      {
        // Start Tags or Text
        this.reader.next();

        while ((this.reader.getType() != XmlReader.END_TAG)
          && (this.reader.getType() != XmlReader.END_DOCUMENT))
        {
          if (this.reader.getType() == XmlReader.TEXT)
          {
            obj.setText(this.reader.getText());
          }
          else if (this.reader.getType() == XmlReader.START_TAG)
          {
            XmlObject subObj = readXml();

            obj.addSubObject(subObj);
          }
          
          this.reader.next();
        }
      }
      catch (IOException ex)
      {
        ex.printStackTrace();
      }
    }

    return obj;
  }


  private void event (XmlObject obj)
  {
    if (obj != null)
    {
      switch(this.state)
      {
        case FBChatAPI.OFF_STATE:
        {
          break;
        }
        case FBChatAPI.STREAM_STREAM_STATE:
        {
          eventStreamStreamState(obj);
          break;
        }
        case FBChatAPI.STREAM_FEATURES_STATE:
        {
          eventStreamFeaturesState(obj);
          break;
        }
        case FBChatAPI.AUTH_STATE:
        {
          eventAuthState(obj);
          break;
        }
        case FBChatAPI.RESPONSE_STATE:
        {
          eventResponseState(obj);
          break;
        }
        case FBChatAPI.IQ_STATE:
        {
          eventIqState(obj);
          break;
        }
        case FBChatAPI.PRESENCE_STATE:
        {
          eventPresenceState(obj);
          break;
        }
        default:
        {
          throw new UnsupportedOperationException("Unknown state");
        }
      }
    }
  }

  private void eventStreamStreamState (XmlObject obj)
  {
    XmlObject feature = null;

    if (obj.getTag().equals("stream:stream"))
    {
      if (obj.subObjectCount() > 0)
      {
        for (int i = 0; i < obj.subObjectCount(); i++)
        {
          XmlObject so = obj.getSubObject(i);

          if (so.getTag().equals("stream:features"))
          {

            feature = so;
          }
        }
      }
    }
    else if (obj.getTag().equals("stream:features"))
    {
      feature = obj;
    }
    else
    {
      // Bad messages
    }

    if (feature != null)
    {
      // Send Auth
      try
      {
        this.writer.startTag("auth");
        this.writer.attribute("xmlns", "urn:ietf:params:xml:ns:xmpp-sasl");
        this.writer.attribute("mechanism", "DIGEST-MD5");
        this.writer.attribute("xmlns:ga", "http://www.google.com/talk/protocol/auth");
        this.writer.attribute("ga:client-uses-full-bind-result", "true");
        this.writer.endTag();
        this.writer.flush();

        this.state = FBChatAPI.AUTH_STATE;
      }
      catch (IOException e)
      {
        for (int j = 0; j < listeners.size(); j++)
        {
          XmppListener l = (XmppListener)listeners.elementAt(j);

          l.onAuthFailed("Failed sending <auth>");
        }
      }
    }
    
  }

  private void eventStreamFeaturesState (XmlObject obj)
  {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  private void eventAuthState (XmlObject obj)
  {
    if (obj.getTag().equals("challenge"))
    {
      try
      {
        SaslChallenge challenge = SaslChallenge.parse(obj.getText());

        SaslChallengeResponse response =
          new SaslChallengeResponse(challenge, this.username, this.password);

        this.writer.startTag("response");
        this.writer.attribute("xmlns", "urn:ietf:params:xml:ns:xmpp-sasl");
        this.writer.text(response.getEncodedContent());
        this.writer.endTag();
        this.writer.flush();

        this.state = FBChatAPI.RESPONSE_STATE;
      }
      catch (IOException e)
      {
        for (int j = 0; j < listeners.size(); j++)
        {
          XmppListener l = (XmppListener)listeners.elementAt(j);

          l.onAuthFailed("Failed sending <response>");
        }
      }
    }
    else
    {
      // Handle Error
    }
  }

  private void eventResponseState (XmlObject obj)
  {
    if (obj.getTag().equals("challenge"))
    {
      try
      {
        this.writer.startTag("response");
        this.writer.attribute("xmlns", "urn:ietf:params:xml:ns:xmpp-sasl");
        this.writer.endTag();
        this.writer.flush();

        this.state = FBChatAPI.RESPONSE_STATE;
      }
      catch (IOException e)
      {
        for (int j = 0; j < listeners.size(); j++)
        {
          XmppListener l = (XmppListener)listeners.elementAt(j);

          l.onAuthFailed("Failed sending <response>");
        }
      }
    }
    else if (obj.getTag().equals("success"))
    {
      for (int j = 0; j < listeners.size(); j++)
      {
        XmppListener l = (XmppListener)listeners.elementAt(j);

        l.onAuth("Login Success");
      }
    }
    else
    {
      // Handle Error
    }
  }

  private void eventIqState (XmlObject obj)
  {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  private void eventPresenceState (XmlObject obj)
  {
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
