package net.sourceforge.fullsync;

import java.util.EventListener;


/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface ActionCreatedListener extends EventListener 
{
    public void actionCreated( Action a );
}
