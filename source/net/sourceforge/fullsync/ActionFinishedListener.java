/*
 * Created on 18.09.2004
 */
package net.sourceforge.fullsync;

import java.util.EventListener;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface ActionFinishedListener extends EventListener 
{
	// TODO better use some action class given to the actionqueue
	public void actionFinished();
}
