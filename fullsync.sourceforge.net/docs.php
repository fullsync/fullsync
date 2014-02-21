<?php
include( "html.php" );
HtmlHeader( "Documentation" );

$versions = getVersions(3);
foreach ($versions as $v) {
	echo "\t<p>for <a href=\"docs/manual-${v['version']}/${v['manual']}\">FullSync ${v['version']}</a></p>\n";
}
HtmlFooter();
