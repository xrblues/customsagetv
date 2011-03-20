package org.jdna.sagetv.networkencoder;

/**
 * Interface for handling network encoder commands.
 * 
 * @author seans
 *
 */
public interface INetworkEncoder {
	/**
	 * Called when a start command is received.  A start command includes information about the
	 * channel being tuned.  It's like a call to setup the tuner and tune a channel.
	 * @param command
	 * @throws CommandException
	 */
   public void start(StartCommand command) throws CommandException;
   
   /**
    * Called to stop the tuner from tuning the currently tuned channel.
    * @param command
    * @throws CommandException
    */
   public void stop(StopCommand command) throws CommandException;
   
   /**
    * Return the size of the buffer for the currently tuned channel.
    * @param command
    * @return
    * @throws CommandException
    */
   public long getFileSize(GetFileSizeCommand command) throws CommandException;

   /**
    * return true if a channel has been tuned.
    * @return
    */
   public boolean isActive();
}
