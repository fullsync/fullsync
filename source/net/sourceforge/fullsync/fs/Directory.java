/*
 * Created on 18.07.2004
 */
package net.sourceforge.fullsync.fs;

import java.util.Collection;


/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface Directory extends Node
{ 
    public Directory getParent();
    public Collection getChildren();
    public Node getChild( String name );
    
    // TODO currently, 'create' isnt the right word
    //		they do not exist before and may not exists after sync
    public Directory createDirectory( String name );// throws FileSystemException;
    public File createFile( String name );// throws FileSystemException;
    
    public void makeDirectory();
}
