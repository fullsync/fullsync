<?php
	include( "html.php" );
	HtmlHeader( "Download FullSync" );
	$version = getVersions(1);
	$version = $version[0]['version'];
?>
	<h2>for Windows</h2>
	<p>
		Tested with Windows XP, and Windows 7. <a href="http://sourceforge.net/projects/fullsync/files/FullSync%20<?php echo $version; ?>/FullSync-<?php echo $version; ?>-Windows.msi/download">Download FullSync for Windows</a><br />
		Java must be installed, you can get it at <a href="http://www.java.com/">www.java.com</a>
	</p>

	<h2>for Linux</h2>
	<iframe id="obs-download-frame" src="http://software.opensuse.org/download/package.iframe?project=home%3Acobexer%3AFullSync&amp;package=FullSync"></iframe>

	<h2>FullSync without Installer</h2>
	<p>
		Theese downloads contain both 32bit and 64bit versions of FullSync.
	</p>
	<ul>
		<li>for <a href="http://sourceforge.net/projects/fullsync/files/FullSync%20<?php echo $version; ?>/FullSync-<?php echo $version; ?>-win.zip/download">Windows</a></li>
		<li>for <a href="http://sourceforge.net/projects/fullsync/files/FullSync%20<?php echo $version; ?>/FullSync-<?php echo $version; ?>-linux.tar.gz/download">Linux</a></li>
		<li>for <a href="http://sourceforge.net/projects/fullsync/files/FullSync%20<?php echo $version; ?>/FullSync-<?php echo $version; ?>-mac.tar.gz/download">Mac</a> please test this download and <a href="https://sourceforge.net/p/fullsync/discussion/">tell me if it works for you</a>.</li>
	</ul>

	<h2>Source Code</h2>
	<p>
		The source code of FullSync Version <?php echo $version; ?> can be obtained here:
		<a href="http://sourceforge.net/projects/fullsync/files/FullSync%20<?php echo $version; ?>/FullSync-<?php echo $version; ?>-src.tar.gz/download">FullSync-<?php echo $version; ?>-src.tar.gz</a><br />
		To access the FullSync Source repository head over to <a href="http://sourceforge.net/p/fullsync/git/">the git repository</a>.
	</p>

	<h2>Older versions</h2>
	<p>
		You can get older versions of FullSync from <a href="http://sourceforge.net/projects/fullsync/files/">the download archive</a>.
	</p>

<?php
	HtmlFooter('download-button');
