<?php
include("html.php");
HtmlHeader("Screenshots");
define('SCREENSHOTS_DIR', "screenshots");

function getScreesnhots($major, $minor, $patch) {
	$screenshots = array();
	$dir = SCREENSHOTS_DIR . '/' . "$major.$minor.$patch/";
	$d = dir($dir);
	if (false !== $d) {
		while (false !== ($entry = $d->read())) {
			if (preg_match('/\.(png|jpg|jpeg|gif)$/i', $entry)) {
				$content = '';
				if (file_exists($dir . $entry . '.html')) {
					$content = file_get_contents($dir . $entry . '.html');
				}
				$screenshots[$dir . $entry] = $content;
			}
		}
		$d->close();
	}
	return $screenshots;
}


$screenshots = array();

$d = dir(SCREENSHOTS_DIR);
if (false !== $d) {
	while (false !== ($entry = $d->read())) {
		if (preg_match('/^(\d+)\.(\d+)\.(\d+)$/', $entry, $version)) {
			$screenshots[] = array(
				'major' => $version[1],
				'minor' => $version[2],
				'patch' => $version[3],
				'screenshots' => getScreesnhots($version[1], $version[2], $version[3])
			);
		}
	}
	$d->close();
}
usort($screenshots, 'versionComparator');

foreach($screenshots as $version) {
	if (empty($version['screenshots'])) {
		continue;
	}
	echo "<h3>Version ${version['major']}.${version['minor']}.${version['patch']}</h3>";
	foreach($version['screenshots'] as $s => $desc) {
		echo '<img src="' . $s . '" /><br />';
		if ($desc) {
			echo $desc . "<br />";
		}
		echo "<br />";
	}
}

HtmlFooter();
