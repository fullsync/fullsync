/*
 * Created on 18.07.2004
 */
package net.sourceforge.fullsync.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class BlockBuffer implements Buffer
{
	int maxSize;
	int maxEntries;
	
	int freeBytes;
	int numberBytes;
	int numberEntries;
	byte[] buffer;
	Entry[] entries;
	
	int flushes;
	
	public BlockBuffer()
	{
		maxSize = 1024*1024*10;
		maxEntries = 5000;
		numberBytes = 0;
		numberEntries = 0;
		freeBytes = maxSize;
		
		buffer = null;
		entries = null;
		
		flushes = 0;
	}
	public int getCapacity()
	{
		return maxSize;
	}
	public void clearStatus()
	{
		flushes = 0;
	}
	/*
	public void updateStatus( BackupProcessStatus status )
	{
		status.BufferFileCapacity = maxEntries;
		status.BufferByteCapacity = maxSize;
		status.BufferBytesLoaded = numberBytes;
		status.BufferFilesLoaded = numberEntries;
		status.BufferFlushes = flushes;
	}
	*/
	public void load()
	{
		if( buffer == null )
		{
			buffer  = new byte[maxSize];
			entries = new Entry[maxEntries];
		}
	}
	public void unload()
	{
		buffer = null;
		entries = null;
	}
	public void flush()
		throws IOException
	{
		for( int i = 0; i < numberEntries; i++ )
		{
		    try {
				Entry e  = entries[i];
				EntryDescriptor desc = (EntryDescriptor)e.descriptor;
				//desc.flush( this, e );
				OutputStream out = desc.getOutputStream();
				if( out != null )
				{
				    out.write( buffer, e.start, e.length );
				    out.close();
				}
				desc.finishWrite();
				
		    } catch( IOException ioe ) {
		        ioe.printStackTrace();
		    }
			
			// TODO args, large file copy will invoke a lot of
			//      open/closes which will slowdown the copy process :-/
			// what about copying large files via stream ? 
		}
		Arrays.fill( entries, null );
		numberBytes = 0;
		numberEntries = 0;
		freeBytes = maxSize;
		
		flushes++;
	}
	// Length must be the correct length (there must be enough free bytes and so on)
	/*
	long Load( Stream inStream, FileInfo dst, int length, bool lastSegment )
	{
		long offset = inStream.Position;
		int start   = numberBytes;
		int read    = inStream.Read( buffer, start, length );
		
		Segment s;
		if( offset == 0 )
			s += s.First;
		if( inStream.E
		entries[numberEntries] = new BufferEntry( dst, start, read, offset, lastSegment );

		numberBytes += read;
		numberEntries++;
		freeBytes -= read;
		
		return read;
	}*/
	// may not read as much as length says
	protected Entry storeBytes( InputStream inStream, long length )
		throws IOException
	{
		if( length > freeBytes ) length = freeBytes;
		
		int start = numberBytes;
		int read  = inStream.read( buffer, start, (int)length );
		
		numberBytes += read;
		freeBytes -= read;
		
		Entry entry = new Entry( start, read );
		entries[numberEntries]  = entry;
		numberEntries++;
		
		return entry;
	}
	
	private int store( InputStream inStream, long alreadyRead, long lengthLeft, EntryDescriptor descriptor )
		throws IOException
	{
		Entry entry = storeBytes( inStream, lengthLeft );
		
		int s = Segment.Middle;
		if( alreadyRead == 0 )
			s |= Segment.First;
		if( entry.length == lengthLeft )
			s |= Segment.Last;
		
		entry.internalOffset = alreadyRead; 
		entry.internalSegment = s;
		entry.descriptor = descriptor;

		return entry.length;
	}
	public boolean storeEntry( InputStream data, long size, EntryDescriptor descriptor )
    {
        long alreadyRead = 0;
		long lengthLeft = size;
		
		try {
			do {
				if( lengthLeft > freeBytes || numberEntries == maxEntries )
					flush();
				int read = store( data, alreadyRead, lengthLeft, descriptor );
				alreadyRead += read;
				lengthLeft  -= read;
			} while( lengthLeft > 0 );
			data.close();
			
			return true;
		} catch( IOException ie) {
		    ie.printStackTrace();
			return false;
		}
    }
    public void storeEntry( EntryDescriptor descriptor )
    	throws IOException
    {
        Entry entry;
        if( descriptor.getLength() == 0 )
        {
            entry = new Entry( numberBytes, 0 );
            entry.descriptor = descriptor;
            entries[numberEntries] = entry;
            numberEntries++;
        } else {
            storeEntry( descriptor.getInputStream(), descriptor.getLength(), descriptor );
        }
        
    }
}
