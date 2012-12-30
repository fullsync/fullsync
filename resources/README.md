# Upgrade from FulSync 0.9.1
to upgrade from the older FullSync version follow the steps below:

 1. Backup your __profiles.xml__ from the old FullSync installation (located inside the installation directory)
 2. Install the new FullSync version downloaded above
 3. Use __Edit__ » __Import Profiles__ to import the __profiles.xml__ into the new FullSync version
 4. Uninstall the old FullSync version

# Changes since the last version
 * updated all bundled libraries to their latest version
 * added 64-bit support
 * initial attempt at mac support (needs testers!)
 * don't ask for exit confirmation if there are no tasks scheduled
 * added FullSync Launcher, since FullSync now uses the current SWT implementation it needs to have a launcher to select the correct SWT implementation at runtime
 * added support for public key authentication for SFTP connections
 * moved the profiles, preferences and log file to ~/.config/fullsync/ ($XDG_CONFIG_HOME) or C:\Documents and Settings\<username>\.config\fullsync\ for windows.
 * new commons-vfs source and destination browser should work now, certainly not fine, but it works 
 * added a file filter for simplyfied ruleset. The filter is a replacement for the ignore/accept patterns.
   Old profiles with regexp patterns are automatically converted to this new file filter.
 * added wildcards as ignore/accept patterns (regexp was default till now) in simplyfied syncrules.
 * changed the ignore/accept pattern behaviour. Now if the ignore pattern is empty but the accept is not empty, than
   everything is ignored but what matches the accept pattern
