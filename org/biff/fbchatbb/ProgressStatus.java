/*
 * ProgressStatus: Interface that provides the status information for
 * a ProgressBar.
 */

package org.biff.fbchatbb;

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
