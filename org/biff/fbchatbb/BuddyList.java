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

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Container class that holds message and buddy information.
 * Revision: 0.0.1
 * Date: 2010-03-16
 * @author jdpare
 */
public class BuddyList
{
  /**
   * Archive of incoming and outgoing messages.
   */
  private Hashtable messageArchive = new Hashtable();

  /**
   * All buddies' statuses.
   */
  private Hashtable status = new Hashtable();

  /**
   * JID to Name
   */
  private Hashtable jids = new Hashtable();

  private int messageNumber = 0;

  /**
   * Constructor.
   */
  public BuddyList()
  {

  }

  /**
   * Saves a message received.
   * @param from Sender of message.
   * @param messageText Message text.
   */
  public void saveMessageExternal(String from, final String messageText)
  {
    this.messageNumber = this.messageNumber + 1;

    from = from + "*" + this.messageNumber;

    this.messageArchive.put(from, messageText);
  }

  /**
   * Saves a message sent.
   * @param from Sender of message.
   * @param to Destination of message.
   * @param messageText Message text.
   */
  public void saveMessageInternal(
    String from,
    final String to,
    final String messageText)
  {
    this.messageNumber = this.messageNumber + 1;

    from = from + "*" + to + "*" + this.messageNumber;

    this.messageArchive.put(from, messageText);
  }

  /**
   * Creates a string of messages between two users.
   * @param buddy Name of the buddy in the conversation.
   * @param me Name of your account.
   * @return Message history.
   */
  public String getMessages(final String buddy, final String me)
  {
    String history = "";
    String nameSearchString = "";

    for (int msgCount = 0; msgCount <= this.messageNumber; msgCount++)
    {
      nameSearchString = buddy + "*" + msgCount;

      if (this.messageArchive.containsKey(nameSearchString))
      {
        history = history +
          buddy +
          " " +
          this.messageArchive.get(nameSearchString) +
          "\n";
      }

      nameSearchString = me  + "*" + nameSearchString;

      if (this.messageArchive.containsKey(nameSearchString))
      {
        history = history +
          me +
          " " +
          this.messageArchive.get(nameSearchString) +
          "\n";
      }
    }

    return history;
  }

  /**
   * Creates a history of all messages.
   * @return Message history.
   */
  public String getAllMessages()
  {
    String history = "";

    history =  this.messageArchive.toString();

    return history;
  }

  /**
   * Clears all message history.
   */
  public void clearMessages()
  {
    this.messageArchive.clear();
  }

  /**
   * Sets a buddy's status.
   * @param status Buddy's status.
   * @param name Buddy's name.
   */
  public void setBuddyStatus(final String status, final String name)
  {
    this.status.put(name, status);
  }

  /**
   * Returns buddy's status.
   * @param name Buddy's name.
   * @return Current status.
   */
  public String getBuddyStatus(final String name)
  {
    String buddyStatus = "error";

    if (this.status.containsKey(name))
    {
      buddyStatus = this.status.get(name).toString();
    }

    return buddyStatus;
  }

  public void addBuddy(final String jid, final String name)
  {
    this.jids.put(jid, name);
  }

  public String getName(final String jid)
  {
    String name = "empty";

    if (this.jids.contains(jid))
    {
      name = this.jids.get(jid).toString();
    }

    return name;
  }

  public String getJID(final String name)
  {
    String jid = "empty";

    if (this.jids.contains(name))
    {
      Enumeration k = this.jids.keys();

      while(k.hasMoreElements())
      {
        String t_jid = (String)k.nextElement();

        String t_name = (String)this.jids.get(t_jid);

        if (t_name.equals(name))
        {
          jid = t_jid;
        }
      }
    }

    return jid;
  }

  public Enumeration getJIDs()
  {
    return this.jids.keys();
  }
}
