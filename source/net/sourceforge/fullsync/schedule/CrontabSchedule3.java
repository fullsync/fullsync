package net.sourceforge.fullsync.schedule;

import java.util.Calendar;
import java.util.StringTokenizer;

import net.sourceforge.fullsync.DataParseException;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class CrontabSchedule3 implements Schedule
{
    private String origPattern;
    
    private boolean[] bMinutes = new boolean[60];
    private boolean[] bHours = new boolean[24];
    private boolean[] bDaysOfMonth = new boolean[1+31];
    private boolean[] bMonths = new boolean[1+12];
    private boolean[] bDaysOfWeek = new boolean[7];
    
    private boolean allMinutes;
    private boolean allHours;
    private boolean allDaysOfMonth;
    private boolean allMonths;
    private boolean allDaysOfWeek;
    
    
    public CrontabSchedule3( String pattern )
    	throws DataParseException
    {
        read( pattern );
    }
    
    /**
     * Reads a crontab schedule as specified in the crontab man document:
     * 
     * The time and date fields are:
     *    field	     allowed values
     *    -----	     --------------
     *    minute         0-59
     *    hour           0-23
	 *    day of month   1-31
	 *    month          1-12 (or names, see below)
	 *    day of week    0-7 (0 or 7 is Sun, or use names)
	 * A field may be an asterisk (*), which always stands for ``first-last''.
	 * Ranges of numbers are allowed.  Ranges are two numbers separated with a
	 * hyphen. The specified range is inclusive. For example, 8-11 for an
	 * 'hours' entry specifies execution at hours 8, 9, 10 and 11.
	 * 
	 * Lists are allowed. A list is a set of numbers (or ranges) separated by
	 * commas. Examples: '1,2,5,9', '0-4,8-12'.
	 * 
	 * Step values can be used in conjunction with ranges. Following a range
	 * with '/<number>' specifies skips of the number's  value  through  the
	 * range. For example, '0-23/2' can be used in the hours field to spec-
	 * ify command execution every other hour (the alternative in the V7 stan-
	 * dard is '0,2,4,6,8,10,12,14,16,18,20,22'). Steps are also permitted
	 * after an asterisk, so if you want to say 'every two hours', just use
	 * '* /2'.
     **/
    
    public void read(String pattern)
    	throws DataParseException
    {
        origPattern = pattern;
        
        StringTokenizer tokenizer = new StringTokenizer(pattern);
        allMinutes = parseToken(tokenizer.nextToken(),bMinutes,false);
        allHours = parseToken(tokenizer.nextToken(),bHours,false);
        allDaysOfMonth = parseToken(tokenizer.nextToken(),bDaysOfMonth,true);
        allMonths = parseToken(tokenizer.nextToken(),bMonths,true);
        allDaysOfWeek = parseToken(tokenizer.nextToken(),bDaysOfWeek,false);
    }
    
    public String toString()
    {
        return origPattern;
    }
        
    public boolean parseToken(String token, boolean[] arrayBool, boolean bBeginInOne)
    	throws DataParseException
    {
        int i;
        int index;
        int each=1;
        
        try {
        	// Look for step first
			index = token.indexOf("/");
			
			if(index > 0) 
			{
				each = Integer.parseInt(token.substring(index + 1));
				if (each == 0) 
					throw new DataParseException("Never use expressions like */0 ");

				token = token.substring(0,index);
			}
        	
            if(token.equals("*")) 
            {
                for( i=bBeginInOne?1:0; i < arrayBool.length; i += each ) 
                {
                    arrayBool[i] = true;
                }
                return each==1;
            }

            index = token.indexOf(",");
            if(index > 0) {
                StringTokenizer tokenizer = new StringTokenizer(token, ",");
                while (tokenizer.hasMoreElements()) {
                    parseToken(tokenizer.nextToken(), arrayBool, bBeginInOne);
                }
                return false;
            }
            
            index = token.indexOf("-");
            if(index > 0) {
                int start = Integer.parseInt(token.substring(0, index));
                int end = Integer.parseInt(token.substring(index + 1));

                /*
                if(bBeginInOne) {
                    start--;
                    end--;
                }
                */
                for(int j=start; j<=end; j+=each)
                    arrayBool[j] = true;
                return false;
            }
            
                int iValue = Integer.parseInt(token);
                /*
                if(bBeginInOne) {
                    iValue--;
                }
                */
                arrayBool[iValue] = true;
                return false;
        } catch (Exception e) {
            throw new DataParseException( "Smth was wrong with " + token, e );
        }
    }
    
    public long getNextOccurrence( long now )
    {
        Calendar calNow = Calendar.getInstance();
        calNow.setTimeInMillis( now );
        Calendar calDest = (Calendar)calNow.clone();
        
        while( calDest.get( Calendar.YEAR ) <= calNow.get(Calendar.YEAR)+1 )
        {
            if( !allMonths )
            {
                if( gotoNext( bMonths, calDest, Calendar.MONTH ) )
                    continue;
            }
            
            if( !allDaysOfMonth )
            {
                if( gotoNext( bDaysOfMonth, calDest, Calendar.DAY_OF_MONTH ) )
                    continue;
            }
            
            if( !allHours )
            {
                if( gotoNext( bHours, calDest, Calendar.HOUR_OF_DAY ) )
                    continue;
            }
            
            if( !allMinutes )
            {
                if( gotoNext( bMinutes, calDest, Calendar.MINUTE ) )
                    continue;
            }

            calDest.set( Calendar.SECOND, 0 );
            return calDest.getTimeInMillis();
        }
        
        return 0;
    }
    private boolean gotoNext( boolean[] bArray, Calendar cal, int field )
    {
        // FIXME we assume that there is a true in the array,
        //       but we should avoid a deadloop anyways.
        
        // TODO omg, we need to descent to find out whether the current month
        //      is ok and ascent to set the correct next month (i.e.)
        
        int orig = cal.get( field );
        int now = orig;
        int max = cal.getActualMaximum( field );
        int min = cal.getActualMinimum( field );
        
        while( !bArray[now] ) 
        {
            now++;
            if( now > max )
            {
                //cal.set( field, now );
                
                // the next match is in the next cycle,
                // so goto first possible, goto next cycle and repeat all before
                now = min;
                while( !bArray[now] ) 
                {
                    now++;
                    if( now > max )
                        throw new RuntimeException( "deadloop" );
                }
                cal.set( field, now );
                
                // rise cycle
                switch( field )
                {
                case Calendar.MONTH:
                    cal.add( Calendar.YEAR, 1 );
                    break;
                case Calendar.DAY_OF_MONTH:
                //case Calendar.DAY_OF_WEEK:
                    cal.add( Calendar.MONTH, 1 );
                    break;
                case Calendar.HOUR_OF_DAY:
                    /*
                    if( allMonths && allDaysOfMonth && !allDaysOfWeek )
                        gotoNext( bDaysOfWeek, cal, Calendar.DAY_OF_WEEK );
                    else if( !allDaysOfMonth )
                        gotoNext( bDaysOfMonth, cal, Calendar.DAY_OF_MONTH );
                    */
                    cal.add( Calendar.DAY_OF_MONTH, 1 );
                    // TODO currently we miss out the doublecase 
                    //      allDaysOfWeek + allDaysOfMonth
                    break;
                case Calendar.MINUTE:
                    cal.add( Calendar.HOUR_OF_DAY, 1 );
                    break;
                }
                return true;
            }
        }
        
        if( now != orig )
        {
	        switch( field )
	        {
	        case Calendar.MONTH:
	            cal.set( Calendar.DAY_OF_MONTH, 1 );
	        case Calendar.DAY_OF_MONTH:
	        case Calendar.DAY_OF_WEEK:
	            cal.set( Calendar.HOUR_OF_DAY, 0 );
	        case Calendar.HOUR_OF_DAY:
	            cal.set( Calendar.MINUTE, 0 );
	        }
        }
        cal.set( field, now );
        return false;
    }
    
    public void update()
    {
        

    }
    /** 
     * Returns true if the time table entry matchs with the calendar given
     * @param cal Calendar to compare with the time table entry
     * @return true if the time table entry matchs with the calendar given
     */
	private boolean equalsCalendar(Calendar cal) 
	{
        return (
                //bSeconds[cal.get(Calendar.SECOND)] &&
                bMinutes[cal.get(Calendar.MINUTE)] &&
                bHours[cal.get(Calendar.HOUR_OF_DAY)] &&
                bDaysOfWeek[cal.get(Calendar.DAY_OF_WEEK)-1] &&
                bDaysOfMonth[cal.get(Calendar.DAY_OF_MONTH)-1] &&
                bMonths[cal.get(Calendar.MONTH)] );
        		//bYears[cal.get(Calendar.YEAR)]) ;
	}
	
	
	public static void main( String[] args ) throws DataParseException
    {
        Schedule schedule = new CrontabSchedule( "36,51 5,7,18,19 20 * *" );
        Calendar cal = Calendar.getInstance();
        System.out.println( cal.getTime().toString() );
        cal.setTimeInMillis( schedule.getNextOccurrence( cal.getTimeInMillis() ) );
        System.out.println( cal.getTime().toString() );
    }
}
