package net.sourceforge.fullsync;


/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class Action
{
    public static final int Nothing = 0;
    public static final int Add = 1;
    public static final int Update = 2;
    public static final int Delete = 3;

    public static final int NotDecidableError = 10;
    public static final int UnexpectedChangeError = 11;
    public static final int DirHereFileThereError = 12;

    public static final String[] names = new String[] { "Nothing", "Add", "Update", "Delete" };
    public static final String[] errorNames = new String[] { "NotDecidableError", "UnexpectedChangeError", "DirHereFileThereError" };
    
    private int type;
    private int location;
    private int bufferUpdate;
    private boolean beforeRecursion;
    private String explanation;
    
    public Action( int type, int location, int bufferUpdate, String explanation )
    {
        this( type, location, bufferUpdate, explanation, true );
    }
    public Action( int type, int location, int bufferUpdate, String explanation, boolean beforeRecursion )
    {
        this.type = type;
        this.location = location;
        this.bufferUpdate = bufferUpdate;
        this.beforeRecursion = beforeRecursion;
        this.explanation = explanation;
    }
    
    public int getType()
    {
        return type;
    }
    public int getLocation()
    {
        return location;
    }
    public int getBufferUpdate()
    {
        return bufferUpdate;
    }
    public boolean isBeforeRecursion()
    {
        return beforeRecursion;
    }
    public String getExplanation()
    {
        return explanation;
    }
    public boolean isError()
    {
        return (type>=10);
    }
    public static String toString( int type )
    {
        if( type >= 10 )
             return errorNames[type-10];
        else return names[type];
    }
    public String toString()
    {
        return "["+toString(type)+"("+Location.toString(location)+") - "+explanation+"]";
    }
}
