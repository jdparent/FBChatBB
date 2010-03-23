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
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.sourceforge.jxa.XmlReader;
import org.biff.updates.UpdateChecker;
import org.biff.util.WebLauncher;
import org.biff.util.XmlObject;

/**
 *
 * @author jdpare
 */
class UpdateCheckerManager extends Thread
{
  private UpdateChecker checker;
  private PopupScreen popup;
  private LabelField label;
  private String url;
  private String appName;
  private String version;
  private XmlReader reader;
  private HttpConnection http;

  private boolean isRunning;

  public UpdateCheckerManager (
    final String appName,
    final String version,
    final String url)
  {

    DialogFieldManager manager = new DialogFieldManager();

    popup = new PopupScreen(manager);

    label = new LabelField("Checking for Updates");

    manager.addCustomField(label);

    this.appName = appName;
    this.version  = version;
    this.url = url;
  }

  public void start()
  {
    UiApplication.getUiApplication().pushScreen(popup);

    popup.doPaint();

    super.start();
  }

  public void run()
  {
    isRunning = true;

    http = null;
    int rc;

    try
    {
      http = (HttpConnection)Connector.open(url);

      rc = http.getResponseCode();

      if (rc != HttpConnection.HTTP_OK)
      {
        clearUpdate();
      }
      else
      {
        reader = new XmlReader(http.openInputStream());
      }
    }
    catch (IOException e)
    {
      isRunning = false;
    }

    while (isRunning)
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

    clearUpdate();
  }

  private void clearUpdate()
  {
    isRunning = false;

    UiApplication.getUiApplication().invokeLater(new Runnable()
    {
      public void run()
      {
        if (popup.isDisplayed())
        {
          UiApplication.getUiApplication().popScreen(popup);
        }
      }
    });

    reader.close();
    
    try
    {
      http.close();
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
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
      checker = new UpdateChecker(obj, this.appName);

      if (checker.isValue())
      {
        isRunning = false;

        if (!checker.getUpdateVersion().equals(this.version))
        {
          this.clearUpdate();
          
          WebLauncher web = new WebLauncher();

          web.launchBroser(checker.getUpdateURL());
        }
      }
    }
  }

  public boolean isRunning()
  {
    return this.isRunning;
  }
}
