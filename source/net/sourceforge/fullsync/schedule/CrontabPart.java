package net.sourceforge.fullsync.schedule;

import java.util.StringTokenizer;

import net.sourceforge.fullsync.DataParseException;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class CrontabPart
{
    public final static CrontabPart MINUTES = new CrontabPart( "minutes", 0, 59, 0 );
    public final static CrontabPart HOURS = new CrontabPart( "hours", 0, 23, 0 );
    public final static CrontabPart DAYSOFMONTH = new CrontabPart( "days of month", 1, 31, 0 );
    public final static CrontabPart MONTHS = new CrontabPart( "months", 1, 12, -1 );
    public final static CrontabPart DAYSOFWEEK = new CrontabPart( "days of week", 0, 8, +1 );
    public final static CrontabPart[] ALL_PARTS 
    	= new CrontabPart[] { MINUTES, HOURS, DAYSOFMONTH, MONTHS, DAYSOFWEEK };
    
    public class Instance
    {
        public final String pattern;
        public final boolean[] bArray;
        public final boolean all;
        
        public Instance()
        {
            pattern = "*";
            bArray = new boolean[high+1];
            all = true;
            
            for( int i = low; i < high; i++ )
                bArray[i] = true;
        }
        public Instance( String pattern )
        	throws DataParseException
        {
            this.pattern = pattern;
            bArray = new boolean[high+1];
            all = parseToken( pattern );
        }
        public Instance( boolean[] bArray )
        {
            this.bArray = new boolean[high+1];
            this.all = false;
            
            StringBuffer p = new StringBuffer();
            for( int i = low; i < high; i++ )
            {
                if( this.bArray[i] )
                {
                    p.append( String.valueOf( i-offset ) ).append( ',' );
                }
            }
            if( p.length() == 0 )
                 pattern = "0";
            else pattern = p.substring( 0, p.length()-1 );
        }

        private boolean parseToken( String token )
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
	                for( i = low; i < bArray.length-offset; i += each ) 
	                {
	                    bArray[i+offset] = true;
	                }
	                return each==1;
	            }
	
	            index = token.indexOf(",");
	            if(index > 0) {
	                StringTokenizer tokenizer = new StringTokenizer(token, ",");
	                while (tokenizer.hasMoreElements()) {
	                    parseToken(tokenizer.nextToken());
	                }
	                return false;
	            }
	            
	            index = token.indexOf("-");
	            if(index > 0) {
	                int start = Integer.parseInt(token.substring(0, index));
	                int end = Integer.parseInt(token.substring(index + 1));
	
	                /*if(bBeginInOne) {
	                    start--;
	                    end--;
	                }*/
	                for(int j=start; j<=end; j+=each)
	                    bArray[j+offset] = true;
	                return false;
	            }
	            
	            int iValue = Integer.parseInt(token);
	            /*if(bBeginInOne) {
	                iValue--;
	            }*/
	            bArray[iValue+offset] = true;
	            return false;
	        } catch (Exception e) {
	            throw new DataParseException( "Smth was wrong with " + token, e );
	        }
	    }
    }
    
    public final String name;
    
    public final int low;
    public final int offset;
    public final int high;
    
    
    public CrontabPart( String name, int low, int high, int offset )
    {
        this.name = name;
        this.low = low;
        this.high = high;
        this.offset = offset;
    }
    
    public Instance createInstance()
    {
        return new Instance();
    }
    public Instance createInstance( String pattern )
		throws DataParseException
	{
	    return new Instance( pattern );
	}
    public Instance createInstance( boolean[] bArray )
	{
	    return new Instance( bArray );
	}
}
