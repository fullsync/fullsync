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

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;


/**
* This code was generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* *************************************
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED
* for this machine, so Jigloo or this code cannot be used legally
* for any corporate or commercial purpose.
* *************************************
*/
public class FileObjectChooser extends org.eclipse.swt.widgets.Dialog {

	private Shell dialogShell;
	private Text textUrlExtension;
	private Label labelFileFilter;
	private Text textFilename;
	private Label labelFilename;
	private ToolItem toolItemNewFolder;
	private Composite compositeBottom;
	private TableColumn tableColumnName;
	private TableColumn tableColumnDateModified;
	private TableColumn tableColumnType;
	private TableColumn tableColumnSize;
	private ToolItem toolItemParent;
	private Button buttonCancel;
	private Button buttonOk;
	private Combo comboFileFilter;
	private ToolItem toolItemView;
	private ToolBar toolBarActions;
	private Composite compositeTop;
	private Label labelBaseUrl;
	private Label label1;
	private Table tableItems;

    private Image imageFile;
    private Image imageFolder;

    private int result;
    private FileObject baseFileObject;
    private FileObject activeFileObject;
    private FileObject selectedFileObject;

	/**
	* Auto-generated main method to display this
	* org.eclipse.swt.widgets.Dialog inside a new Shell.
	*/
	public static void main(String[] args) {
		try {
			Display display = Display.getDefault();
			Shell shell = new Shell(display);
            shell.setLayout( new FillLayout() );
            ConnectionConfiguration inst = new ConnectionConfiguration(shell, SWT.NULL);
            shell.open();
            while( !shell.isDisposed() )
                if( !display.readAndDispatch() )
                    display.sleep();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FileObjectChooser(Shell parent, int style) {
		super(parent, style);
	}

	public int open() {
		try {
            result = 0;
			Shell parent = getParent();
			dialogShell = new Shell(parent, SWT.BORDER | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
            dialogShell.setText( "Choose File..." );
            imageFile = new Image( parent.getDisplay(), "images/FS_File_text_plain.gif" );
            imageFolder = new Image( parent.getDisplay(), "images/FS_Folder_Collapsed.gif" );

			dialogShell.setLayout(new GridLayout());
			dialogShell.layout();
			dialogShell.pack();
			dialogShell.setSize(546, 396);
            {
                compositeTop = new Composite(dialogShell, SWT.NONE);
                GridLayout composite1Layout = new GridLayout();
                composite1Layout.numColumns = 4;
                GridData compositeTopLData = new GridData();
                compositeTopLData.horizontalAlignment = GridData.FILL;
                compositeTop.setLayoutData(compositeTopLData);
                compositeTop.setLayout(composite1Layout);
                {
                    label1 = new Label(compositeTop, SWT.NONE);
                    label1.setText("Url:");
                }
                {
                    labelBaseUrl = new Label(compositeTop, SWT.NONE);
                    labelBaseUrl.setText("<base url>");
                }
                {
                    textUrlExtension = new Text(compositeTop, SWT.BORDER);
                    GridData textUrlExtensionLData = new GridData();
                    textUrlExtensionLData.grabExcessHorizontalSpace = true;
                    textUrlExtensionLData.horizontalAlignment = GridData.FILL;
                    textUrlExtension.setLayoutData(textUrlExtensionLData);
                    textUrlExtension.setText("<url extension>");
                }
                {
                    toolBarActions = new ToolBar(compositeTop, SWT.NONE);
                    {
                        toolItemParent = new ToolItem(toolBarActions, SWT.NONE);
                        toolItemParent.setImage( new Image( parent.getDisplay(), "images/FS_LevelUp.gif" ) );
                        toolItemParent
                            .addSelectionListener(new SelectionAdapter() {
                            @Override
							public void widgetSelected(SelectionEvent evt) {
                                toolItemParentWidgetSelected(evt);
                            }
                            });
                    }
                    {
                        toolItemNewFolder = new ToolItem(
                            toolBarActions,
                            SWT.NONE);
                        toolItemNewFolder.setImage( new Image( parent.getDisplay(), "images/FS_Folder_New.gif" ) );
                        toolItemNewFolder.setDisabledImage( new Image( parent.getDisplay(), "images/FS_Folder_New_disabled.gif" ) );
                        toolItemNewFolder.addSelectionListener(new SelectionAdapter() {
                            @Override
							public void widgetSelected(SelectionEvent evt) {
                                toolItemNewFolderWidgetSelected(evt);
                            }
                        });
                        toolItemNewFolder.setEnabled( false );
                    }
                    /*
                    {
                        toolItemView = new ToolItem(toolBarActions, SWT.DROP_DOWN);
                        toolItemView.setText("View");
                    }
                    */
                }
            }
            {
            	GridData tableItemsLData = new GridData();
            	tableItemsLData.grabExcessHorizontalSpace = true;
            	tableItemsLData.grabExcessVerticalSpace = true;
            	tableItemsLData.horizontalAlignment = GridData.FILL;
            	tableItemsLData.verticalAlignment = GridData.FILL;
                tableItems = new Table(dialogShell, SWT.SINGLE | SWT.BORDER);
                tableItems.setHeaderVisible( true );
                tableItems.setLayoutData(tableItemsLData);
                tableItems.addMouseListener(new MouseAdapter() {
                    @Override
					public void mouseDown(MouseEvent evt) {
                        tableItemsMouseDown(evt);
                    }
                    @Override
					public void mouseDoubleClick(MouseEvent evt) {
                        tableItemsMouseDoubleClick(evt);
                    }
                });
                {
                    tableColumnName = new TableColumn(tableItems, SWT.NONE);
                    tableColumnName.setText("File name");
                    tableColumnName.setWidth(200);
                }
                {
                    tableColumnSize = new TableColumn(tableItems, SWT.NONE);
                    tableColumnSize.setText("Size");
                    tableColumnSize.setWidth(60);
                }
                {
                    tableColumnType = new TableColumn(tableItems, SWT.NONE);
                    tableColumnType.setText("Type");
                    tableColumnType.setWidth(100);
                }
                {
                    tableColumnDateModified = new TableColumn(
                        tableItems,
                        SWT.NONE);
                    tableColumnDateModified.setText("Date Modified");
                    tableColumnDateModified.setWidth(145);
                }
            }
            {
                compositeBottom = new Composite(dialogShell, SWT.NONE);
                GridLayout compositeBottomLayout = new GridLayout();
                compositeBottomLayout.numColumns = 3;
                compositeBottomLayout.horizontalSpacing = 15;
                GridData compositeBottomLData = new GridData();
                compositeBottomLData.horizontalAlignment = GridData.FILL;
                compositeBottom.setLayoutData(compositeBottomLData);
                compositeBottom.setLayout(compositeBottomLayout);
                {
                    labelFilename = new Label(compositeBottom, SWT.NONE);
                    labelFilename.setText("File name:");
                }
                {
                	GridData textFilenameLData = new GridData();
                	textFilenameLData.horizontalAlignment = GridData.FILL;
                	textFilenameLData.grabExcessHorizontalSpace = true;
                    textFilename = new Text(compositeBottom, SWT.BORDER);
                    textFilename.setLayoutData(textFilenameLData);
                }
                {
                    buttonOk = new Button(compositeBottom, SWT.PUSH
                        | SWT.CENTER);
                    GridData buttonOkLData = new GridData();
                    buttonOkLData.horizontalAlignment = GridData.CENTER;
                    buttonOkLData.widthHint = 80;
                    buttonOk.setLayoutData(buttonOkLData);
                    buttonOk.setText("Open");
                    buttonOk.addSelectionListener(new SelectionAdapter() {
                        @Override
						public void widgetSelected(SelectionEvent evt) {
                            buttonOkWidgetSelected(evt);
                        }
                    });
                }
                {
                    labelFileFilter = new Label(compositeBottom, SWT.NONE);
                    labelFileFilter.setText("Files of type:");
                }
                {
                    comboFileFilter = new Combo(compositeBottom, SWT.NONE);
                    GridData comboFileFilterLData = new GridData();
                    comboFileFilterLData.horizontalAlignment = GridData.FILL;
                    comboFileFilter.setLayoutData(comboFileFilterLData);
                    comboFileFilter.setText("all files");
                    comboFileFilter.setEnabled( false );
                }
                {
                    buttonCancel = new Button(compositeBottom, SWT.PUSH
                        | SWT.CENTER);
                    GridData buttonCancelLData = new GridData();
                    buttonCancelLData.horizontalAlignment = GridData.FILL;
                    buttonCancel.setLayoutData(buttonCancelLData);
                    buttonCancel.setText("Cancel");
                    buttonCancel.addSelectionListener(new SelectionAdapter() {
                        @Override
						public void widgetSelected(SelectionEvent evt) {
                            buttonCancelWidgetSelected(evt);
                        }
                    });
                }
            }
            updateBaseFileObject();
            updateActiveFileObject();
            updateSelectedFileObject();
			dialogShell.open();
			Display display = dialogShell.getDisplay();
			while (!dialogShell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
            return result;
		} catch (Exception e) {
			e.printStackTrace();
            return 0;
		}
	}

    protected void populateList() throws FileSystemException
    {
        FileObject[] children = activeFileObject.getChildren();
        Arrays.sort( children, new Comparator() {
            @Override
			public int compare(Object arg0,Object arg1)
            {
                FileObject o1 = (FileObject)arg0;
                FileObject o2 = (FileObject)arg1;
                try {
                    if( o1.getType() == FileType.FOLDER && o2.getType() == FileType.FILE )
                    {
                        return -1;
                    } else if( o1.getType() == FileType.FILE && o2.getType() == FileType.FOLDER ) {
                        return 1;
                    } else {
                        return o1.getName().getBaseName().compareTo( o2.getName().getBaseName() );
                    }
                } catch( FileSystemException fse ) {
                    fse.printStackTrace();
                    return 0;
                }
            }
        } );

        DateFormat df = DateFormat.getDateTimeInstance();

        for( int i = 0; i < children.length; i++ )
        {
            FileObject data = children[i];

            TableItem item;
            if( tableItems.getItemCount() <= i )
                 item = new TableItem( tableItems, SWT.NULL );
            else item = tableItems.getItem( i );

            item.setText( 0, data.getName().getBaseName() );
            String type = data.getType().getName();

            if( data.getType().hasContent() )
            {
                FileContent content = data.getContent();
                String contentType = content.getContentInfo().getContentType();
                if( contentType != null )
                    type += " ("+contentType+")";
                item.setText( 1, String.valueOf( content.getSize() ) );
                item.setText( 3, df.format( new Date(content.getLastModifiedTime()) ) );
            } else {
                item.setText( 1, "" );
                item.setText( 3, "" );
            }
            item.setText( 2, type );

            if( data.getType() == FileType.FOLDER )
                 item.setImage( imageFolder );
            else item.setImage( imageFile );

            item.setData( data );
        }
        tableItems.setItemCount( children.length );
    }

    protected void updateBaseFileObject()
    {
        labelBaseUrl.setText( baseFileObject.getName().toString() );
    }

    protected void updateActiveFileObject() throws FileSystemException
    {
        tableItems.deselectAll();
        populateList();
        textUrlExtension.setText( baseFileObject.getName().getRelativeName( activeFileObject.getName() ) );
    }

    protected void updateSelectedFileObject() throws FileSystemException
    {
        if( selectedFileObject != null )
        {
            textFilename.setText( selectedFileObject.getName().getBaseName() );
        } else {
            textFilename.setText( "" );
        }
    }

    public void setBaseFileObject( FileObject baseFileObject )
    {
        this.baseFileObject = baseFileObject;
        if( activeFileObject == null )
            activeFileObject = baseFileObject;
    }
    public FileObject getBaseFileObject()
    {
        return baseFileObject;
    }

    public void setActiveFileObject( FileObject active ) throws FileSystemException
    {
        this.activeFileObject = active;
        if( dialogShell != null )
            updateActiveFileObject();
    }
    public FileObject getActiveFileObject()
    {
        return activeFileObject;
    }

    public void setSelectedFileObject( FileObject selected ) throws FileSystemException
    {
        this.selectedFileObject = selected;
        if( dialogShell != null )
            updateSelectedFileObject();
    }
    public FileObject getSelectedFileObject()
    {
        return selectedFileObject;
    }

    private void toolItemParentWidgetSelected(SelectionEvent evt)
    {
        try {
            FileObject parent = activeFileObject.getParent();
            if( parent != null )
                setActiveFileObject( parent );
        } catch( FileSystemException e ) {
            e.printStackTrace();
        }
	}

	private void toolItemNewFolderWidgetSelected(SelectionEvent evt) {
	    System.out.println("toolItemNewFolder.widgetSelected, event="+evt);
	    //TODO add your code for toolItemNewFolder.widgetSelected
	}

	private void tableItemsMouseDoubleClick(MouseEvent evt) {
        TableItem[] items = tableItems.getSelection();
        if( items.length == 0 )
            return;

        try {
            TableItem item = items[0];
            FileObject file = (FileObject)item.getData();
            if( file.getType().hasChildren() )
            {
                setActiveFileObject( file );
            }
        } catch( FileSystemException fse ) {
            fse.printStackTrace();
        }
	}

	private void tableItemsMouseDown(MouseEvent evt) {
        TableItem[] items = tableItems.getSelection();
        if( items.length == 0 )
            return;

        try {
            TableItem item = items[0];
            FileObject file = (FileObject)item.getData();

            // TODO if we are looking for files, just take files, otherwise just take dirs
            setSelectedFileObject( file );
        } catch( FileSystemException fse ) {
            fse.printStackTrace();
        }
	}

	private void buttonOkWidgetSelected(SelectionEvent evt) {
        result = 1;
        dialogShell.dispose();
	}

	private void buttonCancelWidgetSelected(SelectionEvent evt) {
        result = 0;
        dialogShell.dispose();
	}
}
