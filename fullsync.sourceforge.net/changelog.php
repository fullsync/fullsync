<?php
	include( "html.php" );
	HtmlHeader( "Press Releases" );
?>
	<h2>FullSync Changelog</h2>
	<ol>
<?php
		$versions = getVersions(0);
		foreach ($versions as $v) {
			echo "\t\t<li>Version ${v[version]} (${v[releaseDate]}):<br />${v[changes]}</li>\n";
		}
?>
	</ol>
<?php
	HtmlFooter();
