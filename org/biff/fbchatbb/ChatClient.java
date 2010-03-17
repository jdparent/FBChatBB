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

import net.sourceforge.jxa.XmppListener;
import org.biff.ui.ProgressBar;
import org.biff.ui.ProgressStatus;

/**
 *
 * @author jdpare
 */
public class ChatClient implements XmppListener, ProgressStatus
{
  private FBChatAPI fb;
  private BuddyList buddyList = new BuddyList();
  private String user;
  private String pass;
  private ProgressBar loginProgress;
  private int loginPercent;
  private String loginText;

  public ChatClient(final String user, final String pass)
  {
    this.user = user;
    this.pass = pass;

    this.fb = null;

    this.loginPercent = 0;
    this.loginText = "Logging In";
    this.loginProgress = new ProgressBar(this, "Logging In");

  }

  public void open()
  {
    if (fb == null)
    {
      fb = new FBChatAPI(this.user, this.pass);

      fb.addListener(this);
    }

    this.loginProgress.open();

    this.loginText = "Connecting";
    this.loginPercent = 10;

    this.loginProgress.update();

    fb.start();
  }

  public void onConnFailed (String msg)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void onAuth (String resource)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void onAuthFailed (String message)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void onMessageEvent (String from, String body)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void onContactEvent (String jid, String name, String group, String subscription)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void onContactOverEvent ()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void onStatusEvent (String jid, String show, String status)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void onSubscribeEvent (String jid)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void onUnsubscribeEvent (String jid)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public int getPercents ()
  {
    return this.loginPercent;
  }

  public String getMessage ()
  {
    return this.loginText;
  }

  public void setMessage (String message)
  {
    this.loginText = message;
  }
}
