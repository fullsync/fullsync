<?php
	include( "html.php" );
	HtmlHeader( "Home" );
?>
	<h1>Welcome to FullSync</h1>
	<p>
	FullSync is a universal file synchronization and backup tool which is highly customizable and expandable. 
	It is especially for developers, but the basic functionality is easy enough for everyone.
	</p>
	<h2>Release History</h2>
	<ol>
		<li>
			Version 0.10.0 (3. December 2012):<br />
			<ul>
				<li>updated all bundled libraries to their latest version.</li>
				<li>added 64-bit support.</li>
				<li>initial attempt at mac support (needs testers!).</li>
				<li>added support for public key authentication for SFTP connections.</li>
				<li>moved the profiles, preferences and log file to ~/.config/fullsync/ ($XDG_CONFIG_HOME) or C:\Documents and Settings\&lt;username&gt;\.config\fullsync\ for Windows.</li>
				<li>new commons-vfs source and destination browser should work now, certainly not fine, but it works.</li>
				<li>added a file filter for simplyfied ruleset. The filter is a replacement for the ignore/accept patterns.</li>
				<li>added wildcards as ignore/accept patterns (regexp was default till now) in simplyfied syncrules.</li>
				<li>changed the ignore/accept pattern behaviour. Now if the ignore pattern is empty but the accept is not empty, then everything is ignored but what matches the accept pattern</li>
			</ul>
			<a href="https://sourceforge.net/projects/fullsync/files/FullSync%200.10.0/">Download Now</a>
			or view an online version of the <a href="docs/manual-0.10.0/manual.html">documentation</a>.
		</li>
		<li>
			Version 0.9.1 (8 March 2005):<br />
			<ul>
				<li>added compatible and passive url-query options to ftp urls</li>
				<li>compatible=true should solve the problem with - and spaces in paths (bug #1090385) this really needs gui support !</li>
				<li>dded FullSync internal logging (say loggers per class,...)</li>
				<li>added French localization</li>
				<li>added Spanish localization</li>
			</ul>
			view an online version of the <a href="docs/manual-0.9.1/index.html">documentation</a>.
		</li>
	</ol>
	<h2>Features:</h2>
	<ul>
		<li>Publication and update of websites, synchronization of local directories and making backups of your data.</li>
		<!--<li>multiple modes: update destination, synchronize source with destination</li>-->
		<li>configuration and rule-rewriting per directory possible</li>
		<li>flexible rules, allowing all kinds of exclusion/inclusion</li>
		<li>buffered filesystems (so the tool only sees files it created itself)</li>
		<li>multiple protocols supported (FTP,SFTP,SMB...)</li>
		<li>read/write access bufferable and multithreadable</li>
	</ul>
<?php
	HtmlFooter();
