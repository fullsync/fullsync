package net.full.fs.ui;

import java.net.URISyntaxException;

import org.eclipse.swt.widgets.Composite;

public abstract class ProtocolSpecificComposite extends Composite
{
    public ProtocolSpecificComposite( Composite parent, int style )
    {
        super( parent, style );
    }

    public abstract void reset( String scheme );
    public abstract void setLocationDescription( LocationDescription location );
    public abstract LocationDescription getLocationDescription() throws URISyntaxException;
}
