package net.sourceforge.fullsync.fs.sftp;

import java.util.Collection;

import net.sourceforge.fullsync.fs.Directory;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.Node;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class SftpDirectory implements Directory
{
    /*
    private SshClient  sshClient;
    private SftpClient sftpClient;
    */
    public SftpDirectory( String host, String username, String password )
    {
        /*
        sshClient = new SshClient();
        sshClient.connect( host );
        
        PasswordAuthenticationClient pwd = new PasswordAuthenticationClient();
        pwd.setUsername( username );
        pwd.setPassword( password );
        
        int result = sshClient.authenticate( pwd );
        if( result == AuthenticationProtocolState.COMPLETE )
        {
            sftpClient = sshClient.openSftpClient();
        }
        */
        /* 
         * sftpClient.quit();
         * sshClient.disconnect();
         */
    }

    public Directory getParent()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection getChildren()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Node getChild( String name )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Directory createDirectory( String name )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public File createFile( String name )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void makeDirectory()
    {
        // TODO Auto-generated method stub

    }

    public String getName()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getPath()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isDirectory()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean exists()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isBuffered()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public Node getUnbuffered()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean delete()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public void refresh()
    {
        // TODO Auto-generated method stub

    }

}
