package net.sourceforge.fullsync;

import java.io.Serializable;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class State implements Serializable
{
	private static final long serialVersionUID = 1;
	
    public static final int NodeInSync = 1;
    public static final int Orphan = 2;
    public static final int FileChange = 3;
    public static final int DirHereFileThere = 4;
    
    private int type;
    private int location;
    
    public State( int type, int location ) {
        this.type = type;
        this.location = location;
    }
    public int getLocation()
    {
        return location;
    }
    public int getType()
    {
        return type;
    }
    
    public boolean equals( int type, int location )
    {
        return (this.type==type && this.location==location);
    }
}