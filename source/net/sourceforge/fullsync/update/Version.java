package net.sourceforge.fullsync.update;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class Version
{
    private String orig;
    private int parts[];
    
    public Version( String versionString )
    {
        this.orig = versionString;
        String[] partsString = versionString.split(".");
        parts = new int[partsString.length];
        
        for( int i = 0; i < parts.length; i++ )
        {
            try {
                parts[i] = Integer.parseInt( partsString[i] );
            } catch( NumberFormatException nfe ) {
                parts[i] = 0;
            }
        }
    }
    
    public int[] getParts()
    {
        return parts;
    }
    
    public boolean isHigherThan( Version version )
    {
        int[] otherParts = version.getParts();
        
        for( int i = 0;; i++ )
        {
            if( parts.length < i )
                return false;
            else if( otherParts.length < i )
                return true;
            
            if( parts[i] > otherParts[i] )
                return true;
        }
    }
    
    public String toString()
    {
        return orig;
    }
}
