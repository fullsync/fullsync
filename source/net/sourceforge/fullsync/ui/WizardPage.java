package net.sourceforge.fullsync.ui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface WizardPage
{
    public String getTitle();
    public String getCaption();
    public String getDescription();
    public Image getIcon();
    public Image getImage();
    
    public void createContent( Composite content );
    public void createBottom( Composite bottom );
}
