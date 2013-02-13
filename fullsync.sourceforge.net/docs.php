<?php
include( "html.php" );
HtmlHeader( "Download" );
echo '<h1>Documentation</h1>';

$versions = getVersions(3);
foreach ($versions as $v) {
	echo "\t<p>for <a href=\"docs/manual-${v['version']}/${v['manual']}\">FullSync ${v['version']}</a></p>\n";
}
HtmlFooter();
