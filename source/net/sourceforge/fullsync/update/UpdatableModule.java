package net.sourceforge.fullsync.update;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface UpdatableModule
{
    // jars/files
    // dependencies
    public String getName();
    //public String[] getFiles();
    //public String[] getDependentModules();
    public Version getVersion();
}
