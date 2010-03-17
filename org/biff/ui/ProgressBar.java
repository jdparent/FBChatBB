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

package org.biff.ui;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.PopupScreen;

/**
 * A popup window class that displays the current status
 * of an action.
 * @author Jeff Parent <jeff.parent@gmail.com>
 */
public class ProgressBar
{
  private PopupScreen popup;
  private GaugeField gauge;
  private LabelField label;

  private ProgressStatus monitor;

  /**
   * Constructor.
   * @param monitor Class that contains information displayed during operation.
   * @param title Title displayed on popup window.
   */
  public ProgressBar(ProgressStatus monitor, String title)
  {
    this.monitor = monitor;
    monitor.setMessage(title);

    DialogFieldManager manager = new DialogFieldManager();

    popup = new PopupScreen(manager);

    label = new LabelField(title);

    gauge = new GaugeField(null, 0, 100, 20, GaugeField.PERCENT);

    manager.addCustomField(label);
    manager.addCustomField(gauge);
  }

  /**
   * Open window.
   */
  public void open()
  {
    UiApplication.getUiApplication().pushScreen(popup);

    popup.doPaint();
  }

  /**
   * Updates the progress.
   *
   * The current percentage and message are provided by monitor passed in
   * the construtor.
   * @see ProgressStatus
   */
  public void update()
  {
    int percentage = monitor.getPercents();

    if (gauge.getValue() <= percentage)
    {
      label.setText(monitor.getMessage());

      gauge.setValue(percentage);
    }

    popup.doPaint();
  }

  /**
   * Close window.
   */
  public void close()
  {
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
  }
}
