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

 define('SOURCEFORGE_PROJECTS_URL', 'https://sourceforge.net/projects/fullsync');

function readVersion($major, $minor, $patch) {
	$content = file_get_contents("versions/$major.$minor.$patch.html");
	if ($content) {
		$entry = array(
			'version' => "$major.$minor.$patch",
			'major' => $major,
			'minor' => $minor,
			'patch' => $patch,
			'changes' => $content,
			'releaseDate' => '',
			'manual' => 'manual.html'
		);
		if (preg_match('/<ul.*data-date="([^"]*)"/', $content, $releaseDate)) {
			$d = DateTime::createFromFormat("!Ymd", $releaseDate[1], new DateTimeZone('UTC'));
			$entry['releaseDate'] = $d->format("j F Y");
		}
		if (preg_match('/<ul.*data-manual="([^"]*)"/', $content, $manual)) {
			$entry['manual'] = $manual[1];
		}
		return $entry;
	}
	return null;
}

function compareVersionComponent($a, $b) {
	$a = intval($a, 10);
	$b = intval($b, 10);
	if ($a > $b) {
		return 1;
	}
	if ($a < $b) {
		return -1;
	}
	return 0;
}

// sort version numbers, newest first
function versionComparator($a, $b) {
	$components = array('major', 'minor', 'patch');
	foreach ($components as $component) {
		$result = compareVersionComponent($b[$component], $a[$component]);
		if (0 !== $result) {
			return $result;
		}
	}
	return 0;
}

function getVersions($countOrCallback) {
	$versions = array();
	$d = dir("versions");
	if (false !== $d) {
		while (false !== ($entry = $d->read())) {
			if (preg_match('/^(\d+)\.(\d+)\.(\d+)\.html$/', $entry, $version)) {
				$versions[] = array(
					'major' => $version[1],
					'minor' => $version[2],
					'patch' => $version[3],
				);
			}
		}
		$d->close();
	}
	usort($versions, 'versionComparator');
	if (is_numeric($countOrCallback)) {
		if ($countOrCallback > 0) {
			$versions = array_splice($versions, 0, $countOrCallback);
		}
	}
	else {
		$allVersions = $versions;
		$versions = array();
		foreach ($allVersions as $v) {
			if (true === $countOrCallback($v)) {
				$versions[] = $v;
			}
		}
	}
	foreach ($versions as $idx => $allVersions) {
		$versions[$idx] = readVersion($allVersions['major'], $allVersions['minor'], $allVersions['patch']);
	}
	return $versions;
}

function getScreesnhots($screenshotsDir, $major, $minor, $patch) {
	$screenshots = array();
	$dir = $screenshotsDir . '/' . "$major.$minor.$patch/";
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

function getAllScreenshots() {
	$screenshots = array();
	$screenshotsDir = "screenshots";
	$d = dir($screenshotsDir);
	if (false !== $d) {
		while (false !== ($entry = $d->read())) {
			if (preg_match('/^(\d+)\.(\d+)\.(\d+)$/', $entry, $version)) {
				$screenshots[] = array(
					'major' => $version[1],
					'minor' => $version[2],
					'patch' => $version[3],
					'screenshots' => getScreesnhots($screenshotsDir, $version[1], $version[2], $version[3])
				);
			}
		}
		$d->close();
	}
	usort($screenshots, 'versionComparator');
	return $screenshots;
}

function HtmlHeader($caption, $skip = '') {
	header("Content-Type: text/html; charset=UTF-8");
	$script = explode('/', $_SERVER['PHP_SELF']);
	$script = end($script);
	?><!doctype html>
<html>
	<head>
		<title>FullSync - <?php echo $caption; ?></title>
		<meta http-equiv="X-UA-Compatible" content="IE=edge" />
		<meta name="viewport" content="width=device-width" />
		<link href="style.css" rel="stylesheet" type="text/css" />
		<link rel="shortcut icon" href="favicon.ico" />
		<style type="text/css">
		#menu a[href="<?php echo $script; ?>"] {
			color: #4477AA;
		}
		@media only screen and (max-width: 900px) {
			#menu a[href="<?php echo $script; ?>"] {
				display: block !important;
			}
		}
		</style>
	</head>
	<body>
		<div id="header">
			<a href="index.php"><img alt="" src="img/fullsync72.png" /></a>
			<h1><a href="index.php">FullSync</a></h1>
			<span>Publishing, Backup, Synchronization</span>
		</div>
		<div id="menu">
			<a href="#menu" id="mobile-menu-accessor">Menu</a>
			<a href="index.php">Welcome</a>
			<a href="features.php">Features</a>
			<a href="screenshots.php">Screenshots</a>
			<a href="docs.php">Documentation</a>
			<a href="download.php">Download</a>
			<a href="contribute.php">Contribute</a>
			<a href="press.php">In The Press</a>
		</div>
	<?php if ('help-banner' !== $skip) { ?>
		<div id="help-banner">
			<div>
				FullSync is looking for you! Check out the <a href="contribute.php">Contribute</a> page for more infos!
			</div>
		</div>
	<?php } ?>
		<div id="content">
			<h1><?php echo $caption; ?></h1>
	<?php
}

function HtmlFooter($skip = '') {
	if ('download-button' !== $skip) {
		echo "<a href=\"download.php\" id=\"global-download-button\">Get FullSync now for free &rarr;</a>";
	}
	?>
			<a href="https://twitter.com/FullSyncNews" class="twitter-follow-button" data-size="large" data-show-count="false">Follow @FullSyncNews</a>
			<script async src="https://platform.twitter.com/widgets.js" charset="utf-8"></script>
			<div id="footer-copyright">
				<span>Copyright &copy; 2004-2020 The FullSync Authors. All Rights Reserved.</span>
				<a href="https://sourceforge.net">
					<img src="https://sourceforge.net/sflogo.php?group_id=115436&amp;type=1" alt="SourceForge Logo"/>
				</a>
			</div>
		</div>
		<div id="footer"> </div>
	</body>
</html>
	<?php
}
