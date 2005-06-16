package net.full.fullsync.sync.files;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import net.full.fullsync.sync.Element;
import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.Site;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class FileSyncElement implements Element
{
    private FileSyncInputElement input;
    
    private String relativePath;

    private File sourceFile;
    private File destinationFile;
    // what about buffering?
    
    private State state;
    private List actions;
    private Action currentAction;
    
    private List children;
    
    public FileSyncElement( FileSyncInputElement input, String relativePath )
    {
        this.input = input;
        this.relativePath = relativePath;
    }
    public FileSyncElement( FileSyncInputElement input, File sourceFile, File destinationFile )
    {
        this.input = input;
        this.sourceFile = sourceFile;
        this.destinationFile = destinationFile;
        //this.relativePath = sourceFile.getRelativePath();
        this.relativePath = sourceFile.getPath();
    }
    
    public FileSyncInputElement getInput()
    {
        return input;
    }
    
    public File getSourceFile()
    {
        //if( sourceFile == null )
        //    sourceFile = source.getFile( relativePath );
        return sourceFile;
    }
    public void setSourceFile( File sourceFile )
    {
        this.sourceFile = sourceFile;
    }
    public File getDestinationFile()
    {
        //if( destinationFile == null )
        //    destinationFile = source.getFile( relativePath );
        return destinationFile;
    }
    public void setDestinationFile( File destinationFile )
    {
        this.destinationFile = destinationFile;
    }
    public String getRelativePath()
    {
        return relativePath;
    }
 
    
    public State getState()
    {
        return state;
    }
    public void setState( State state )
    {
        this.state = state;
    }
    
    public List getActions()
    {
        return actions;
    }
    public void setActions( List actions )
    {
        this.actions = actions;
    }
    public Action getCurrentAction()
    {
        return currentAction;
    }
    public void setCurrentAction( Action currentAction )
    {
        if( actions.contains( currentAction ) )
            this.currentAction = currentAction;
    }
    
    public void addChild( Task child )
    {
        if( children == null )
            children = new ArrayList();
        this.children.add( child );
    }
    public Enumeration getChildren()
    {
        if( children == null )
            return Collections.enumeration( Collections.EMPTY_LIST );
        return Collections.enumeration( children );
    }
}
