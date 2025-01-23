package dtu.master_of_creatures.models.network;

// Java libraries
import java.util.logging.Logger;

public class ThreadModel extends Thread
{
    private static final Logger logger = Logger.getLogger(ThreadModel.class.getName());
    private final Runnable task;

    public ThreadModel(Runnable task)
    {
        this.task = task;
    }

    @Override
    public void run()
    {
        try
        {
            logger.info(Thread.currentThread().getName() + " started.");
            task.run(); // Execute the assigned task
            logger.info(Thread.currentThread().getName() + " completed.");
        }
        catch (Exception e)
        {
            logger.severe(Thread.currentThread().getName() + " encountered an error: " + e.getMessage());
        }
    }
}