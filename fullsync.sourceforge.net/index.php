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
<?php
		$versions = getVersions(3);
		$first = true;
		foreach ($versions as $v) {
			echo "\t\t<li>Version ${v[version]} (${v[releaseDate]}):<br />${v[changes]}";
			if ($first) {
				echo "<a href=\"https://sourceforge.net/projects/fullsync/files/FullSync%20${v[version]}/\">Download Now</a> or";
				$first = false;
			}
			if (isset($v['manual'])) {
				echo " view an online version of the <a href=\"docs/manual-${v[version]}/${v[manual]}\">documentation</a>.";
			}
			echo "</li>\n";
		}
?>
	</ol>
	View <a href="changelog.php">all releases</a>.
	<h2>Features:</h2>
	<ul>
		<li>Publication and update of websites, synchronization of local directories and making backups of your data.</li>
		<li>Configuration and rule-rewriting per directory possible.</li>
		<li>Flexible rules, allowing all kinds of exclusion/inclusion.</li>
		<li>Multiple protocols supported FTP, SFTP, SMB/CIFS(Windows Shares) and of course local files.</li>
		<li>Scheduled execution of synchronizations using intervals or a crontab like scheduling.</li>
		<li>Ability to remotely control a FullSync instance running on another computer.</li>
	</ul>
<?php
	HtmlFooter();
