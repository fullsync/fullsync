package net.full.fs.ui;

import java.net.URISyntaxException;

import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.VFS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FillLayout;

public class ConnectionConfiguration extends Composite
{
    private Label labelProtocol = null;
    private Combo comboProtocol = null;
    private Composite compositeProtocolSpecific = null;
    private ProtocolSpecificComposite compositeSpecific;
    
    public ConnectionConfiguration( Composite parent, int style )
    {
        super( parent, style );
		initialize();
        updateComponent();
    }
    
    /**
     * This method initializes this
     * @throws FileSystemException 
     * 
     */
    private void initialize() {
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        labelProtocol = new Label(this, SWT.NONE);
        labelProtocol.setText("Protocol:");
        createComboProtocol();
        this.setLayout(gridLayout);
        createCompositeProtocolSpecific();
    }
    
    /**
     * This method initializes comboProtocol	
     * @throws FileSystemException 
     *
     */
    private void createComboProtocol()
    {
        comboProtocol = new Combo(this, SWT.READ_ONLY);
        comboProtocol.addModifyListener( new org.eclipse.swt.events.ModifyListener() {
            public void modifyText( org.eclipse.swt.events.ModifyEvent e )
            {
                if( compositeSpecific != null ) 
                    compositeSpecific.dispose();

                compositeSpecific = 
                    FileSystemUiManager.getInstance()
                        .createProtocolSpecificComposite( compositeProtocolSpecific, SWT.NULL, comboProtocol.getText() );
                
                compositeProtocolSpecific.layout();
                setSize( computeSize( getSize().x, SWT.DEFAULT ) );
            }
        } );
    }
    
    /**
     * This method initializes compositeProtocolSpecific    
     *
     */
    private void createCompositeProtocolSpecific()
    {
        GridData gridData1 = new org.eclipse.swt.layout.GridData();
        gridData1.horizontalSpan = 2;
        gridData1.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData1.grabExcessHorizontalSpace = true;
        gridData1.grabExcessVerticalSpace = true;
        gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        compositeProtocolSpecific = new Composite( this, SWT.NONE );
        compositeProtocolSpecific.setLayout(new FillLayout());
        compositeProtocolSpecific.setLayoutData(gridData1);
    }

    public void updateComponent()
    {
        comboProtocol.removeAll();
        String[] schemes = FileSystemUiManager.getInstance().getSchemes();
        for( int i = 0; i < schemes.length; i++ )
            comboProtocol.add( schemes[i] );
        comboProtocol.select(0);
    }

    public void setLocationDescription( LocationDescription location )
    {
        comboProtocol.setText( location.getUri().getScheme() );
        compositeSpecific.setLocationDescription( location );
    }

    public LocationDescription getLocationDescription() throws URISyntaxException
    {
        return compositeSpecific.getLocationDescription();
    }


}  //  @jve:decl-index=0:visual-constraint="10,10"
