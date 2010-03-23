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

/**
	*	<applications>
	*		<app name="Application Name" version="Version Number">URL</app>
	*	</applications>
	*/
package org.biff.updates;

import org.biff.util.XmlObject;

public class UpdateChecker
{
	private String updateURL;
	private String updateVersion;
	private String appName;

	public UpdateChecker(XmlObject xml, final String appName)
	{
		this.appName = new String(appName);

		if (xml != null)
		{
			if (xml.getTag().equals("applications"))
			{
				for (int i = 0; i < xml.subObjectCount(); i++)
				{
					XmlObject tmp = xml.getSubObject(i);

					if (tmp != null)
					{
						String name = new String(tmp.getAttribute("name"));

						if (name.equals(this.appName))
						{
							updateVersion = new String(tmp.getAttribute("version"));
							updateURL = new String(tmp.getText());
							break;	// Leave loop
						}
					}
				}
			}
		}
	}

	public String getName()
	{
		return appName;
	}

	public String getUpdateVersion()
	{
		return updateVersion;
	}

	public String getUpdateURL()
	{
		return updateURL;
	}

  public boolean isValue()
  {
    boolean ret = false;

    if ((appName.length() > 0) &&
      (updateVersion.length() > 0) &&
      (updateURL.length() > 0))
    {
      ret = true;
    }

    return ret;
  }
}
