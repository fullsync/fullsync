/*
 * Created on 18.07.2004
 */
package net.sourceforge.fullsync.buffer;

import java.util.Formatter;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class Entry
{
	public int start;
	public int length;

	public long internalOffset;
	public int internalSegment;

	public EntryDescriptor descriptor;
	
	public Entry( int start, int length )
	{
		this.start = start;
		this.length = length;
		this.internalOffset = 0;
		this.internalSegment = Segment.Only;
		this.descriptor = null;
	}
	public String toString()
	{
	    Formatter format = new Formatter().format( "%10d-%10d: %s", new Object[] { new Integer(start), new Integer(start+length-1), descriptor.toString()} );
		return format.out().toString();
	}
}
