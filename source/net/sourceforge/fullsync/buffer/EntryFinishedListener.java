/*
 * Created on 18.09.2004
 */
package net.sourceforge.fullsync.buffer;

import java.util.EventListener;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface EntryFinishedListener extends EventListener 
{
	public void entryFinished( EntryDescriptor entry );	

}
