/**
 *	@license
 *	This program is free software; you can redistribute it and/or
 *	modify it under the terms of the GNU General Public License
 *	as published by the Free Software Foundation; either version 2
 *	of the License, or (at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program; if not, write to the Free Software
 *	Foundation, Inc., 51 Franklin Street, Fifth Floor,
 *	Boston, MA  02110-1301, USA.
 *
 *	---
 *	@copyright Copyright (C) 2005, Jan Kopcsek <codewright@gmx.net>
 *	@copyright Copyright (C) 2011, Obexer Christoph <cobexer@gmail.com>
 */
package net.full.fs.ui;

import java.net.URI;
import java.net.URISyntaxException;

import net.sourceforge.fullsync.ExceptionHandler;

import org.apache.commons.vfs2.FileObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class UserPasswordSpecificComposite extends ProtocolSpecificComposite
{
    private String scheme;

    private Label labelPath = null;
    private Text textPath = null;
    private Button buttonBrowse = null;
    private Label labelHost = null;
    private Text textHost = null;
    private Label labelUsername = null;
    private Text textUsername = null;
    private Label labelPassword = null;
    private Text textPassword = null;
    public UserPasswordSpecificComposite( Composite parent, int style )
    {
        super( parent, style );
        initialize();
    }

    public void initialize()
    {
        GridData gridData3 = new org.eclipse.swt.layout.GridData();
        gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData3.horizontalSpan = 2;
        gridData3.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        GridData gridData2 = new org.eclipse.swt.layout.GridData();
        gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData2.horizontalSpan = 2;
        gridData2.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        GridData gridData1 = new org.eclipse.swt.layout.GridData();
        gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData1.horizontalSpan = 2;
        gridData1.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        labelHost = new Label(this, SWT.NONE);
        labelHost.setText("Host:");
        GridData gridData = new org.eclipse.swt.layout.GridData();
        gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        textHost = new Text(this, SWT.BORDER);
        textHost.setLayoutData(gridData3);
        labelUsername = new Label(this, SWT.NONE);
        labelUsername.setText("Username:");
        textUsername = new Text(this, SWT.BORDER);
        textUsername.setLayoutData(gridData2);
        labelPassword = new Label(this, SWT.NONE);
        labelPassword.setText("Password:");
        textPassword = new Text(this, SWT.BORDER);
        textPassword.setLayoutData(gridData1);
        textPassword.setEchoChar( '*' );
        labelPath = new Label(this, SWT.NONE);
        labelPath.setText("Path:");
        textPath = new Text(this, SWT.BORDER);
        textPath.setLayoutData(gridData);
        buttonBrowse = new Button(this, SWT.NONE);
        buttonBrowse.setText("...");
        buttonBrowse
                .addSelectionListener( new org.eclipse.swt.events.SelectionAdapter() {
                    @Override
					public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
                    {
                        try {
                            LocationDescription desc = getLocationDescription();

                            FileObject base = FileSystemUiManager.getInstance().resolveFile( desc );
                            FileObjectChooser foc = new FileObjectChooser( getShell(), SWT.NULL );
                            foc.setBaseFileObject( base );
                            foc.setSelectedFileObject( base );
                            if( foc.open() == 1 )
                            {
                                URI uri;
                                uri = new URI( foc.getActiveFileObject().getName().getURI() );
                                textPath.setText( uri.getPath() );
                            }
                        } catch( Exception e1 ) {
                            ExceptionHandler.reportException( e1 );
                        }
                    }
                } );
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        this.setLayout(gridLayout);

    }

    @Override
	public LocationDescription getLocationDescription() throws URISyntaxException
    {
        URI uri = new URI( scheme, textHost.getText(), textPath.getText(), null );
        LocationDescription loc = new LocationDescription( uri );
        loc.setProperty( "username", textUsername.getText() );
        loc.setProperty( "password", textPassword.getText() );
        return loc;
    }

    @Override
	public void setLocationDescription( LocationDescription location )
    {
        URI uri = location.getUri();
        textHost.setText( uri.getHost() );
        textPath.setText( uri.getPath() );
        textUsername.setText( location.getProperty( "username" ) );
        textPassword.setText( location.getProperty( "password" ) );
    }

    @Override
	public void reset( String scheme )
    {
        this.scheme = scheme;
        textHost.setText( "" );
        textPath.setText( "" );
        textUsername.setText( "" );
        textPassword.setText( "" );
    }
}
