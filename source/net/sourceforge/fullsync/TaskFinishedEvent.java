package net.sourceforge.fullsync;

import java.io.Serializable;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class TaskFinishedEvent implements Serializable
{
    private Task task;
    private boolean successful;
    private String errorMsg;
    private int bytesTransferred;
    
    public TaskFinishedEvent( Task task, int bytesTransferred )
    {
        this.task = task;
        this.successful = true;
        this.errorMsg = null;
        this.bytesTransferred = bytesTransferred;
    }
    public TaskFinishedEvent( Task task, String errorMsg )
    {
        this.task = task;
        this.successful = false;
        this.errorMsg = errorMsg;
        this.bytesTransferred = 0;
    }
    
    public int getBytesTransferred()
    {
        return bytesTransferred;
    }
    public String getErrorMessage()
    {
        return errorMsg;
    }
    public boolean isSuccessful()
    {
        return successful;
    }
    public Task getTask()
    {
        return task;
    }
}
