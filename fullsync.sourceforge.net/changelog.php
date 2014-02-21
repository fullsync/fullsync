<?php
	include( "html.php" );
	HtmlHeader( "FullSync Changelog" );
?>
	<ol>
<?php
		$versions = getVersions(0);
		foreach ($versions as $v) {
			echo "\t\t<li>Version ${v['version']} (${v['releaseDate']}):<br />${v['changes']}</li>\n";
		}
?>
	</ol>
<?php
	HtmlFooter();
