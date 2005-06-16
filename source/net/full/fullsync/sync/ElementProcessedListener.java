package net.full.fullsync.sync;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface ElementProcessedListener
{
    public void elementProcessingStarted();
    public void elementProcessed( Element element );
    public void elementProcessingFinished();
}
