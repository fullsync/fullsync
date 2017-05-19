<?php
/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301, USA.
 *
 * For information about the authors of this project Have a look
 * at the AUTHORS file in the root of this project.
 */

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
