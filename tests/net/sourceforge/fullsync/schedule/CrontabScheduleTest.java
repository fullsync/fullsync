package net.sourceforge.fullsync.schedule;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class CrontabScheduleTest extends TestCase
{
    private Calendar now;
    private Calendar expectedResult;
    
    protected void setUp() throws Exception
    {
        now = Calendar.getInstance();
        expectedResult = Calendar.getInstance();
        
        super.setUp();
    }
    
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }
    
    protected void assertNextOccurence( String pattern )
    	throws Exception
    {
        Schedule schedule = new CrontabSchedule( pattern );

        long res = schedule.getNextOccurrence(now.getTimeInMillis());
        
        assertEquals( new Date( expectedResult.getTimeInMillis() ),
                	  new Date( res ) );
    }

    public void testGetNextOccurrenceHour1()
    	throws Exception
    {
        now.set( 2004, 0, 1, 0, 0, 0 );
        expectedResult.set( 2004, 0, 1, 10, 0, 0 );
        
        assertNextOccurence( "0 10,20 * * *" );
    }

    public void testGetNextOccurrenceHour2()
		throws Exception
	{
	    now.set( 2004, 0, 1, 10, 0, 1 );
	    expectedResult.set( 2004, 0, 1, 20, 0, 0 );
	    
	    assertNextOccurence( "0 10,20 * * *" );
	}
    
    public void testGetNextOccurrenceDayOfMonth1()
		throws Exception
	{
	    now.set( 2004, 0, 1, 0, 0, 0 );
	    expectedResult.set( 2004, 0, 10, 10, 0, 0 );
	    
	    assertNextOccurence( "0 10,20 10,20 * *" );
	}

    public void testGetNextOccurrenceDayOfMonth2()
		throws Exception
	{
	    now.set( 2004, 0, 1, 15, 0, 0 );
	    expectedResult.set( 2004, 0, 10, 10, 0, 0 );
	    
	    assertNextOccurence( "0 10,20 10,20 * *" );
	}
    
    public void testGetNextOccurrenceDayOfMonth3()
		throws Exception
	{
	    now.set( 2004, 0, 10, 20, 0, 1 );
	    expectedResult.set( 2004, 0, 20, 10, 0, 0 );
	    
	    assertNextOccurence( "0 10,20 10,20 * *" );
	}
    
    public void testGetNextOccurrenceDayOfWeek()
		throws Exception
	{
	    now.set( 2004, 0, 1, 0, 0, 1 );
	    expectedResult.set( 2004, 0, 4, 10, 0, 0 ); // saturday
	    
	    assertNextOccurence( "0 10,20 * * 7" );
	}
}
