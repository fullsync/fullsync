package net.full.util;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class TimeSpan
{
    private long totalMillis;
    private static final String[] formatLong = new String[] { " milliseconds, ", " seconds, ", " mins, ", " hours, ", " days, " };
    private static final String[] formatShort = new String[] { "ms ", "s ", "m ", "h ", "d " };
    
    public TimeSpan( long totalMillis )
    {
        this.totalMillis = totalMillis;
    }
    public long getTotalMillis()
    {
        return totalMillis;
    }
    public static TimeSpan parse( String str )
        throws ParseException
    {
        Pattern pattern = Pattern.compile( "(\\d+d)? ?(\\d+h)? ?(\\d+m)? ?(\\d+s)?" );
        Matcher matcher = pattern.matcher( str );
        
        if( !matcher.find() ) {
            throw new ParseException("not a timespan: \""+str+"\"", 0);
        } else {
            String d = matcher.group( 1 );
            String h = matcher.group( 2 );
            String m = matcher.group( 3 );
            String s = matcher.group( 4 );
            
            long totalMillis = 0;
            if( d != null )
                totalMillis += Integer.parseInt(d.substring(0, d.length()-1));
            totalMillis *= 24;
            if( h != null )
                totalMillis += Integer.parseInt(h.substring(0, h.length()-1));
            totalMillis *= 60;
            if( m != null )
                totalMillis += Integer.parseInt(m.substring(0, m.length()-1));
            totalMillis *= 60;
            if( s != null )
                totalMillis += Integer.parseInt(s.substring(0, s.length()-1));
            totalMillis *= 1000;
            if( totalMillis == 0 )
                throw new ParseException("not a timespan: \""+str+"\"", 0);
            return new TimeSpan( totalMillis );            
        }
    }
    public static String format( long totalMillis, boolean longNames, boolean withDays )
    {
        int[] fields = new int[5];
        fields[0] = (int)(totalMillis % 1000);
        int total = (int)(totalMillis / 1000);
        fields[1] = total % 60;
        total = total / 60;
        fields[2] = total % 60;
        total = total / 60;
        if( withDays )
        {
            fields[3] = total % 24;
            total = total / 24;
            fields[4] = total;
        } else {
            fields[3] = total;
            fields[4] = 0;
        }
     
        String[] format;
        if( longNames )
             format = formatLong;
        else format = formatShort;
        StringBuffer sb = new StringBuffer();
        for( int i = 4; i >= 1; i-- )
        {
            if( fields[i] > 0 )
                sb.append( Integer.toString( fields[i] ) ).append( format[i] );
        }
        if( sb.length() > 1 && sb.charAt( sb.length()-2 ) == ',' )
            sb.setCharAt( sb.length()-2, ' ' );
        return sb.toString().trim();
    }
    public static String format( long totalMillis )
    {
        return format( totalMillis, false, true );
    }
    public TimeSpan add( long millis )
    {
        return new TimeSpan( totalMillis + millis );
    }
    public String toString( boolean longNames )
    {
        return format( this.totalMillis, longNames, true );
    }
    public String toString()
    {
        return toString( false );
    }
}
