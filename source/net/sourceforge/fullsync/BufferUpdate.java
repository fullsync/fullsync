package net.sourceforge.fullsync;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class BufferUpdate
{
    public static final int None = 0;

    public static final int Source = 1;
    public static final int Destination = 2;
    
    public static final int Both = 3;
    
    public static final String[] names = new String[] { "None", "Source", "Destination", "Both" };  

    public static int getOpposite( int location )
    {
        switch( location )
        {
        case 0: return 3;
        case 1: return 2;
        case 2: return 1;
        case 3: return 0;
        default: return 0;
        }
    }
    public static String toString( int location )
    {
        return names[location];
    }
}
