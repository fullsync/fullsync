package net.sourceforge.fullsync;


/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class TraversalType
{
    private boolean fullTraversal;
    private int location;
    
    public TraversalType()
    {
        this.fullTraversal = true;
        this.location = 0;
    }
    public TraversalType( int location )
    {
        this.fullTraversal = false;
        this.location = location;
    }
    
    public boolean isFullTraversal()
    {
        return fullTraversal;
    }
    
    public int getLocation()
    {
        return location;
    }
}
