<?php
header( "Content-Type: text/html; charset=UTF-8" );
function HtmlHeader( $caption ) {
?>
<!doctype html>
<html>
	<head>
		<title>FullSync - <?php echo $caption; ?></title>
		<meta http-equiv="X-UA-Compatible" content="IE=edge" />
		<link href="style.css" rel="Stylesheet" type="text/css">
	</head>
	<body>
		<div id="wrapper">
			<img id="header-logo" width="640" height="91" src="img/head.gif"/>
			<div id="menu">
				<a href="index.php">Home</a>
				<a href="features.php">Features</a>
				<a href="screenshots.php">Screenshots</a>
				<!-- <a href="donate.php">Donate</a> -->
				<a href="docs.php">Documentation</a>
				<a href="download.php">Download</a>
				<a href="press.php">In The Press</a>
			</div>
			<div id="content">
<?php
}

function HtmlFooter() {
?>
				<div id="global-download-button">
					<!-- TODO: add a fancy download button here -->
					<a href="download.php">Get it now!</a>
				</div>
				<div id="footer-copyright">
					<span style="font-size: 10pt; color: #999999;">Copyright &copy; 2004-2012 The FullSync Authors. All Rights Reserved.</span>
					<a href="http://sourceforge.net" style="float: right;"><img src="http://sourceforge.net/sflogo.php?group_id=115436&amp;type=1" alt="SourceForge Logo"/></a>
				</div>
			</div>
			<img id="footer" src="img/foot.gif" height="22" border="0" />
		</div>
	</body>
</html>
<?php
}
