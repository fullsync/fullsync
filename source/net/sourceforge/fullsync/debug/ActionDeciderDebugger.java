package net.sourceforge.fullsync.debug;

import java.io.IOException;

import net.sourceforge.fullsync.ActionDecider;
import net.sourceforge.fullsync.BufferStateDecider;
import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.FileComparer;
import net.sourceforge.fullsync.StateDecider;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.buffering.debug.BufferedDebugNode;
import net.sourceforge.fullsync.fs.debug.DebugNode;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class ActionDeciderDebugger
{
    public void debugTask( ActionDecider ad, File src, File dst, StateDecider sd, BufferStateDecider bsd )
    	throws DataParseException, IOException
    {
        Task task = ad.getTask( src, dst, sd, bsd );
        System.out.println( src+" <-> "+dst+" : "+task.toString() );
    }
    
    public BufferedDebugNode[] generatePossibleBufferedNodes()
    {
        BufferedDebugNode[] node = new BufferedDebugNode[6];
        int i = 0;
        
        node[i++] = null;
        node[i++] = new BufferedDebugNode( false, true, 0, 0 );
        //node[i++] = new BufferedDebugNode( false, false, 0, 0 );

        node[i++] = new BufferedDebugNode( true, true, 0, 0 );
        
        node[i++] = new BufferedDebugNode( true, false, 100, 100 );
        node[i++] = new BufferedDebugNode( true, false, 100, 200 );
        node[i++] = new BufferedDebugNode( true, false, 200, 100 );
        return node;
    }
    
    public DebugNode[] generatePossibleNodes()
    {
        DebugNode[] node = new DebugNode[5];
        int i = 0;
        
        node[i++] = new DebugNode( false, true, 0, 0 );
        //node[i++] = new DebugNode( false, false, 0, 0 );

        node[i++] = new DebugNode( true, true, 0, 0 );
        
        node[i++] = new DebugNode( true, false, 100, 100 );
        node[i++] = new DebugNode( true, false, 100, 200 );
        node[i++] = new DebugNode( true, false, 200, 100 );
        return node;
    }
    public void debugActionDecider( ActionDecider ac, boolean srcBuffering, boolean dstBuffering )
    	throws DataParseException, IOException
    {
        FileComparer fc = new DebugFileComparer();
        StateDecider sd = new net.sourceforge.fullsync.impl.StateDecider( fc );
        BufferStateDecider bsd = new net.sourceforge.fullsync.impl.BufferStateDecider( fc );
        
        DebugNode[] srcFileSystem = generatePossibleNodes();
        DebugNode[] dstFileSystem = generatePossibleNodes();
        BufferedDebugNode[] srcBuffer = srcBuffering?generatePossibleBufferedNodes():new BufferedDebugNode[] { null };
        BufferedDebugNode[] dstBuffer = dstBuffering?generatePossibleBufferedNodes():new BufferedDebugNode[] { null };
        
        int srcFs, srcB, dstB, dstFs;
        
        DebugNode src, dst;
        
        for( srcFs = 0; srcFs < srcFileSystem.length; srcFs++ )
        {
            for( srcB = 0; srcB < srcBuffer.length; srcB++ )
            {
                src = srcFileSystem[srcFs];
                if( srcBuffer[srcB] != null )
                {
                    srcBuffer[srcB].setUnbuffered( src );
                    src = srcBuffer[srcB];
                }
                
                for( dstFs = 0; dstFs < dstFileSystem.length; dstFs++ )
                {
                    for( dstB = 0; dstB < dstBuffer.length; dstB++ )
                    {
                        dst = dstFileSystem[dstFs];
                        if( dstBuffer[dstB] != null )
                        {
                            dstBuffer[dstB].setUnbuffered( dst );
                            dst = dstBuffer[dstB];
                        }
                        
                        debugTask( ac, src, dst, sd, bsd );
                    }
                }

            }
        }
    }
}
