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

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.NullField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * 
 */
class FBChatBB extends UiApplication
{
    public static void main(String[] args)
    {
      FBChatBB app = new FBChatBB();
      
      app.enterEventDispatcher();
    }
    
    public FBChatBB()
    {
      pushScreen(new SigninScreen());
    }
}

final class SigninScreen extends MainScreen implements FieldChangeListener
{
  private BasicEditField usernameField;
  private PasswordEditField passField;
  private ButtonField signInButton;
  private ChatClient client;
  
  public SigninScreen()
  {
    add(new LabelField("FFChatBB", 0, -1, Field.FIELD_HCENTER));
    add(new NullField(Field.FIELD_HCENTER));
    
    usernameField = new BasicEditField("Username:", null);

    add(usernameField);

    passField = new PasswordEditField("Password:", null);

    add(passField);

    signInButton = new ButtonField("Sign In", Field.FIELD_HCENTER);

    signInButton.setChangeListener(this);

    add(signInButton);

    client = null;
  }

  public void fieldChanged (Field field, int context)
  {
    if (usernameField.getTextLength() == 0)
    {
      Dialog.alert("Invalid Username!");
    }
    else if (passField.getTextLength() == 0)
    {
      Dialog.alert("Invalid Password");
    }
    else
    {
      client = new ChatClient(
        usernameField.getText(),
        passField.getText());

      client.open();
    }
  }
} 
