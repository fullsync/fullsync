package net.sourceforge.fullsync.update;

import java.io.File;
import java.io.IOException;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface UpdateChecker
{
    public Version getLatestVersion( UpdatableModule module ) throws IOException;
    public File downloadUpdate( UpdatableModule module ) throws IOException;
	public boolean installUpdate( File file ) throws IOException;
}
