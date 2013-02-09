<?php
header( "Content-Type: text/html; charset=UTF-8" );

function readVersion($major, $minor, $patch, $file) {
	$content = file_get_contents($file);
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
		if (preg_match('/<!--\s*Date:\s*(.*?)\s*-->/', $content, $releaseDate)) {
			$entry['releaseDate'] = $releaseDate[1];
		}
		if (preg_match('/<!--\s*Manual:\s*(.*?)\s*-->/', $content, $manual)) {
			$entry['manual'] = $manual[1];
		}
		$entry['changes'] = preg_replace('/(<!--.*?-->)/', '', $entry['changes']);
		return $entry;
	}
	return null;
}

// sort version numbers, newest first
function versionComparator($a, $b) {
	if ($a['major'] === $b['major']) {
		if ($a['minor'] === $b['minor']) {
			if ($a['patch'] === $b['patch']) {
				return 0;
			}
			else {
				return $b['patch'] - $a['patch'];
			}
		}
		else {
			return $b['minor'] - $a['minor'];
		}
	}
	else {
		return $b['major'] - $a['major'];
	}
}

function getVersions($count) {
	$versions = array();
	$d = dir("versions");
	if (false !== $d) {
		while (false !== ($entry = $d->read())) {
			if (preg_match('/^(\d+)\.(\d+)\.(\d+)\.html$/', $entry, $version)) {
				$versions[] = readVersion($version[1], $version[2], $version[3], 'versions/' . $entry);
			}
		}
		$d->close();
	}
	usort($versions, 'versionComparator');
	if ($count > 0) {
		$versions = array_splice($versions, 0, $count);
	}
	return $versions;
}

function HtmlHeader($caption, $skip) {
?>
<!doctype html>
<html>
	<head>
		<title>FullSync - <?php echo $caption; ?></title>
		<meta http-equiv="X-UA-Compatible" content="IE=edge" />
		<link href="style.css" rel="stylesheet" type="text/css" />
		<link rel="shortcut icon" href="favicon.ico" />
	</head>
	<body>
		<div id="header">
			<img alt="" src="img/fullsync72.png" />
			<h1>FullSync</h1>
			<span>Publishing, Backup, Synchronization</span>
		</div>
		<div id="menu">
			<a href="index.php">Home</a>
			<a href="features.php">Features</a>
			<a href="screenshots.php">Screenshots</a>
			<!-- <a href="donate.php">Donate</a> -->
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
<?php
}

function HtmlFooter() {
?>
			<a href="download.php" id="global-download-button">Get it now!</a>
			<div id="footer-copyright">
				<span style="font-size: 10pt; color: #999999;">Copyright &copy; 2004-2013 The FullSync Authors. All Rights Reserved.</span>
				<a href="http://sourceforge.net" style="float: right;"><img src="http://sourceforge.net/sflogo.php?group_id=115436&amp;type=1" alt="SourceForge Logo"/></a>
			</div>
		</div>
		<div id="footer"> </div>
	</body>
</html>
<?php
}
