package net.sourceforge.fullsync.ui;

import java.io.File;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
* This code was generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a
* for-profit company or business) then you should purchase
* a license - please visit www.cloudgarden.com for details.
*/
public class ProfileDetails extends org.eclipse.swt.widgets.Composite {

	private Button buttonDestinationBuffered;
	private Button buttonSourceBuffered;
	private Label label12;
	private Text textDestinationPassword;
	private Label label11;
	private Text textDestinationUsername;
	private Label label10;
	private Label label9;
	private Label label8;
	private Label label7;
	private Text textSourcePassword;
	private Label label6;
	private Text textSourceUsername;
	private Label label5;
	private Text textRuleSet;
	private Button buttonBrowseDst;
	private Button buttonBrowseSrc;
	private Button buttonCancel;
	private Button buttonOk;
	private Label label4;
	private Text textDestination;
	private Label label3;
	private Text textSource;
	private Label label2;
	private Text textName;
	private Label label1;
	
	private ProfileManager profileManager;
	private String profileName;
	
	public ProfileDetails(Composite parent, int style) {
		super(parent, style);
		initGUI();
	}

	/**
	* Initializes the GUI.
	* Auto-generated code - any changes you make will disappear.
	*/
	public void initGUI(){
		try {
			preInitGUI();
	
			label1 = new Label(this,SWT.NULL);
			textName = new Text(this,SWT.BORDER);
			label2 = new Label(this,SWT.NULL);
			textSource = new Text(this,SWT.BORDER);
			buttonBrowseSrc = new Button(this,SWT.PUSH| SWT.CENTER);
			label7 = new Label(this,SWT.NULL);
			buttonSourceBuffered = new Button(this,SWT.CHECK| SWT.LEFT);
			label5 = new Label(this,SWT.NULL);
			textSourceUsername = new Text(this,SWT.NULL);
			label6 = new Label(this,SWT.NULL);
			textSourcePassword = new Text(this,SWT.NULL);
			label8 = new Label(this,SWT.NULL);
			label3 = new Label(this,SWT.NULL);
			textDestination = new Text(this,SWT.BORDER);
			buttonBrowseDst = new Button(this,SWT.PUSH| SWT.CENTER);
			label9 = new Label(this,SWT.NULL);
			buttonDestinationBuffered = new Button(this,SWT.CHECK| SWT.LEFT);
			label10 = new Label(this,SWT.NULL);
			textDestinationUsername = new Text(this,SWT.NULL);
			label11 = new Label(this,SWT.NULL);
			textDestinationPassword = new Text(this,SWT.NULL);
			label12 = new Label(this,SWT.NULL);
			label4 = new Label(this,SWT.NULL);
			textRuleSet = new Text(this,SWT.BORDER);
			buttonOk = new Button(this,SWT.PUSH| SWT.CENTER);
			buttonCancel = new Button(this,SWT.PUSH| SWT.CENTER);
	
			this.setSize(new org.eclipse.swt.graphics.Point(472,195));
	
			GridData label1LData = new GridData();
			label1LData.verticalAlignment = GridData.CENTER;
			label1LData.horizontalAlignment = GridData.BEGINNING;
			label1LData.widthHint = -1;
			label1LData.heightHint = -1;
			label1LData.horizontalIndent = 0;
			label1LData.horizontalSpan = 1;
			label1LData.verticalSpan = 1;
			label1LData.grabExcessHorizontalSpace = false;
			label1LData.grabExcessVerticalSpace = false;
			label1.setLayoutData(label1LData);
			label1.setText("Name:");
	
			GridData textNameLData = new GridData();
			textNameLData.verticalAlignment = GridData.CENTER;
			textNameLData.horizontalAlignment = GridData.FILL;
			textNameLData.widthHint = -1;
			textNameLData.heightHint = -1;
			textNameLData.horizontalIndent = 0;
			textNameLData.horizontalSpan = 6;
			textNameLData.verticalSpan = 1;
			textNameLData.grabExcessHorizontalSpace = false;
			textNameLData.grabExcessVerticalSpace = false;
			textName.setLayoutData(textNameLData);
	
			GridData label2LData = new GridData();
			label2LData.verticalAlignment = GridData.CENTER;
			label2LData.horizontalAlignment = GridData.BEGINNING;
			label2LData.widthHint = -1;
			label2LData.heightHint = -1;
			label2LData.horizontalIndent = 0;
			label2LData.horizontalSpan = 1;
			label2LData.verticalSpan = 1;
			label2LData.grabExcessHorizontalSpace = false;
			label2LData.grabExcessVerticalSpace = false;
			label2.setLayoutData(label2LData);
			label2.setText("Source:");
	
			GridData textSourceLData = new GridData();
			textSourceLData.verticalAlignment = GridData.CENTER;
			textSourceLData.horizontalAlignment = GridData.FILL;
			textSourceLData.widthHint = -1;
			textSourceLData.heightHint = -1;
			textSourceLData.horizontalIndent = 0;
			textSourceLData.horizontalSpan = 5;
			textSourceLData.verticalSpan = 1;
			textSourceLData.grabExcessHorizontalSpace = true;
			textSourceLData.grabExcessVerticalSpace = false;
			textSource.setLayoutData(textSourceLData);
	
			GridData buttonBrowseSrcLData = new GridData();
			buttonBrowseSrcLData.verticalAlignment = GridData.CENTER;
			buttonBrowseSrcLData.horizontalAlignment = GridData.BEGINNING;
			buttonBrowseSrcLData.widthHint = -1;
			buttonBrowseSrcLData.heightHint = -1;
			buttonBrowseSrcLData.horizontalIndent = 0;
			buttonBrowseSrcLData.horizontalSpan = 1;
			buttonBrowseSrcLData.verticalSpan = 1;
			buttonBrowseSrcLData.grabExcessHorizontalSpace = false;
			buttonBrowseSrcLData.grabExcessVerticalSpace = false;
			buttonBrowseSrc.setLayoutData(buttonBrowseSrcLData);
			buttonBrowseSrc.setText("...");
			buttonBrowseSrc.addSelectionListener( new SelectionAdapter() {
				public void widgetSelected(SelectionEvent evt) {
					buttonBrowseSrcWidgetSelected(evt);
				}
			});
	
	
			buttonSourceBuffered.setText("buffered");
	
			label5.setText("Username:");
	
	
			label6.setText("Password:");
	
	
	
			GridData label3LData = new GridData();
			label3LData.verticalAlignment = GridData.CENTER;
			label3LData.horizontalAlignment = GridData.BEGINNING;
			label3LData.widthHint = -1;
			label3LData.heightHint = -1;
			label3LData.horizontalIndent = 0;
			label3LData.horizontalSpan = 1;
			label3LData.verticalSpan = 1;
			label3LData.grabExcessHorizontalSpace = false;
			label3LData.grabExcessVerticalSpace = false;
			label3.setLayoutData(label3LData);
			label3.setText("Destination:");
	
			GridData textDestinationLData = new GridData();
			textDestinationLData.verticalAlignment = GridData.CENTER;
			textDestinationLData.horizontalAlignment = GridData.FILL;
			textDestinationLData.widthHint = -1;
			textDestinationLData.heightHint = -1;
			textDestinationLData.horizontalIndent = 0;
			textDestinationLData.horizontalSpan = 5;
			textDestinationLData.verticalSpan = 1;
			textDestinationLData.grabExcessHorizontalSpace = true;
			textDestinationLData.grabExcessVerticalSpace = false;
			textDestination.setLayoutData(textDestinationLData);
	
			GridData buttonBrowseDstLData = new GridData();
			buttonBrowseDstLData.verticalAlignment = GridData.CENTER;
			buttonBrowseDstLData.horizontalAlignment = GridData.BEGINNING;
			buttonBrowseDstLData.widthHint = -1;
			buttonBrowseDstLData.heightHint = -1;
			buttonBrowseDstLData.horizontalIndent = 0;
			buttonBrowseDstLData.horizontalSpan = 1;
			buttonBrowseDstLData.verticalSpan = 1;
			buttonBrowseDstLData.grabExcessHorizontalSpace = false;
			buttonBrowseDstLData.grabExcessVerticalSpace = false;
			buttonBrowseDst.setLayoutData(buttonBrowseDstLData);
			buttonBrowseDst.setText("...");
			buttonBrowseDst.addSelectionListener( new SelectionAdapter() {
				public void widgetSelected(SelectionEvent evt) {
					buttonBrowseDstWidgetSelected(evt);
				}
			});
	
	
			buttonDestinationBuffered.setText("buffered");
	
			label10.setText("Username:");
	
	
			label11.setText("Password:");
	
	
	
			GridData label4LData = new GridData();
			label4LData.verticalAlignment = GridData.CENTER;
			label4LData.horizontalAlignment = GridData.BEGINNING;
			label4LData.widthHint = -1;
			label4LData.heightHint = -1;
			label4LData.horizontalIndent = 0;
			label4LData.horizontalSpan = 1;
			label4LData.verticalSpan = 1;
			label4LData.grabExcessHorizontalSpace = false;
			label4LData.grabExcessVerticalSpace = false;
			label4.setLayoutData(label4LData);
			label4.setText("RuleSet:");
	
			GridData textRuleSetLData = new GridData();
			textRuleSetLData.verticalAlignment = GridData.CENTER;
			textRuleSetLData.horizontalAlignment = GridData.BEGINNING;
			textRuleSetLData.widthHint = 100;
			textRuleSetLData.heightHint = -1;
			textRuleSetLData.horizontalIndent = 0;
			textRuleSetLData.horizontalSpan = 5;
			textRuleSetLData.verticalSpan = 1;
			textRuleSetLData.grabExcessHorizontalSpace = false;
			textRuleSetLData.grabExcessVerticalSpace = false;
			textRuleSet.setLayoutData(textRuleSetLData);
	
			GridData buttonOkLData = new GridData();
			buttonOkLData.verticalAlignment = GridData.CENTER;
			buttonOkLData.horizontalAlignment = GridData.END;
			buttonOkLData.widthHint = -1;
			buttonOkLData.heightHint = -1;
			buttonOkLData.horizontalIndent = 0;
			buttonOkLData.horizontalSpan = 6;
			buttonOkLData.verticalSpan = 1;
			buttonOkLData.grabExcessHorizontalSpace = false;
			buttonOkLData.grabExcessVerticalSpace = false;
			buttonOk.setLayoutData(buttonOkLData);
			buttonOk.setText("Ok");
			buttonOk.addSelectionListener( new SelectionAdapter() {
				public void widgetSelected(SelectionEvent evt) {
					buttonOkWidgetSelected(evt);
				}
			});
	
			GridData buttonCancelLData = new GridData();
			buttonCancelLData.verticalAlignment = GridData.CENTER;
			buttonCancelLData.horizontalAlignment = GridData.BEGINNING;
			buttonCancelLData.widthHint = -1;
			buttonCancelLData.heightHint = -1;
			buttonCancelLData.horizontalIndent = 0;
			buttonCancelLData.horizontalSpan = 1;
			buttonCancelLData.verticalSpan = 1;
			buttonCancelLData.grabExcessHorizontalSpace = false;
			buttonCancelLData.grabExcessVerticalSpace = false;
			buttonCancel.setLayoutData(buttonCancelLData);
			buttonCancel.setText("Cancel");
			buttonCancel.addSelectionListener( new SelectionAdapter() {
				public void widgetSelected(SelectionEvent evt) {
					buttonCancelWidgetSelected(evt);
				}
			});
			GridLayout thisLayout = new GridLayout(7, true);
			this.setLayout(thisLayout);
			thisLayout.marginWidth = 5;
			thisLayout.marginHeight = 5;
			thisLayout.numColumns = 7;
			thisLayout.makeColumnsEqualWidth = false;
			thisLayout.horizontalSpacing = 5;
			thisLayout.verticalSpacing = 5;
			this.layout();
	
			postInitGUI();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/** Add your pre-init code in here 	*/
	public void preInitGUI(){
	}

	/** Add your post-init code in here 	*/
	public void postInitGUI()
	{
	    textSourcePassword.setEchoChar( '*' );
	    textDestinationPassword.setEchoChar( '*' );
	}

	public void setProfileManager( ProfileManager manager )
	{
	    this.profileManager = manager;
	}
	public void setProfileName( String name )
	{
	    this.profileName = name;
	    
	    if( profileName != null )
	    {
	        Profile p = profileManager.getProfile( profileName );
	        
	        textName.setText( p.getName() );
	        textSource.setText( p.getSource().getUri().toString() );
	        buttonSourceBuffered.setSelection( "syncfiles".equals( p.getSource().getBufferStrategy() ) );
	        if( p.getSource().getUsername() != null )
	            textSourceUsername.setText( p.getSource().getUsername() );
	        if( p.getSource().getPassword() != null )
	            textSourcePassword.setText( p.getSource().getPassword() );
	        textDestination.setText( p.getDestination().getUri().toString() );
	        buttonDestinationBuffered.setSelection( "syncfiles".equals( p.getDestination().getBufferStrategy() ) );
	        if( p.getDestination().getUsername() != null )
	            textDestinationUsername.setText( p.getDestination().getUsername() );
	        if( p.getDestination().getPassword() != null )
	            textDestinationPassword.setText( p.getDestination().getPassword() );
	        textRuleSet.setText( p.getRuleSet() );
	    }
	}
	
	public static void showProfile( ProfileManager manager, String name ){
		try {
			Display display = Display.getDefault();
			Shell shell = new Shell(display);
			shell.setText( "Profile Details" );
			ProfileDetails inst = new ProfileDetails(shell, SWT.NULL);
			inst.setProfileManager( manager );
			inst.setProfileName( name );
			shell.setImage( new Image( display, "images/Button_Edit.gif" ) );
			shell.setLayout(new org.eclipse.swt.layout.FillLayout());
			shell.setSize( shell.computeSize( inst.getSize().x, inst.getSize().y ) );
			shell.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/** Auto-generated event handler method */
	protected void buttonOkWidgetSelected(SelectionEvent evt)
	{
	    ConnectionDescription src, dst;
	    try {
	        src = new ConnectionDescription( textSource.getText(), "" );
	        if( buttonSourceBuffered.getSelection() )
	            src.setBufferStrategy( "syncfiles" );
	        if( textSourceUsername.getText().length() > 0 )
	        {
	            src.setUsername( textSourceUsername.getText() );
	            src.setPassword( textSourcePassword.getText() );
	        }
	        dst = new ConnectionDescription( textDestination.getText(), "" );
	        if( buttonDestinationBuffered.getSelection() )
	            dst.setBufferStrategy( "syncfiles" );
	        if( textDestinationUsername.getText().length() > 0 )
	        {
	            dst.setUsername( textDestinationUsername.getText() );
	            dst.setPassword( textDestinationPassword.getText() );
	        }
	    } catch( Exception e ) {
	        MessageBox mb = new MessageBox( this.getShell(), SWT.ICON_ERROR );
	        mb.setText( "Exception" );
            mb.setMessage( "The following error occured: "+e.toString() );
            mb.open();
            return;
	    }

	    if( profileName == null || !textName.getText().equals( profileName ) )
	    {
	        Profile pr = profileManager.getProfile( textName.getText() );
	        if( pr != null )
	        {
	            MessageBox mb = new MessageBox( this.getShell(), SWT.ICON_ERROR );
	            mb.setText( "Duplicate Entry" );
	            mb.setMessage( "A Profile with the same name already exists." );
	            mb.open();
	            return;
	        }
	    }
	    Profile p;
        if( profileName == null )
        {
            p = new Profile( textName.getText(), src, dst, textRuleSet.getText() );
        } else {
            p = profileManager.getProfile( profileName );
            p.setName( textName.getText() );
            p.setSource( src );
            p.setDestination( dst );
            p.setRuleSet( textRuleSet.getText() );
            profileManager.removeProfile( profileName );
        }
        profileManager.addProfile( p );
        profileManager.save();
		getShell().dispose();
	}

	/** Auto-generated event handler method */
	protected void buttonCancelWidgetSelected(SelectionEvent evt){
		getShell().dispose();
	}

	/** Auto-generated event handler method */
	protected void buttonBrowseSrcWidgetSelected(SelectionEvent evt){
		DirectoryDialog dd = new DirectoryDialog( getShell() );
		dd.setMessage( "Choose local source directory." );
		String str = dd.open();
		if( str != null )
		    textSource.setText( new File( str ).toURI().toString() );
	}

	/** Auto-generated event handler method */
	protected void buttonBrowseDstWidgetSelected(SelectionEvent evt){
		DirectoryDialog dd = new DirectoryDialog( getShell() );
		dd.setMessage( "Choose local source directory." );
		String str = dd.open();
		if( str != null )
		    textDestination.setText( new File( str ).toURI().toString() );

	}
}
