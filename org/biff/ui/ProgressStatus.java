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

/**
 * Interface that provides the status information for a ProgressBar
 * @author jdpare
 * @see ProgressBar
 */
public interface ProgressStatus
{
  /**
   * Current progress of process.
   * @return Process progress.
   */
  public int getPercents();

  /**
   * Description of current progress.
   * @return Description of stage.
   */
  public String getMessage();

  /**
   * Sets the current description of current progress.
   * @param message Description of stage.
   */
  public void setMessage(String message);
}
