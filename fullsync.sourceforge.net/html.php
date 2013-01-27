<?php

	function HtmlHeader( $caption )
	{
?>
<html>
	<head>
		<title>FullSync - <?=$caption?></title>
		<link href="style.css" rel="Stylesheet" type="text/css">
	</head>
	<body bgcolor="#f0f0f0" background="img/bg2.gif">
		<br/>
		<table width="600" cellpadding="0" cellspacing="0" border="0" align="center">
			<tr height="91"><td><img src="img/head.gif" border="0"></td></tr>
			<tr height="22" bgcolor="#B7C1CA">
				<td style="font-size: 11px" align="center">
					<b>
						<a href="index.php">Home</a> - 
						<a href="features.php">Features</a> -
						<a href="screenshots.php">Screenshots</a> - 
						<a href="donate.php">Donate</a> - 
						<a href="docs/manual/index.html">Documentation</a> - 
						<a href="http://minoku.de/fswiki/www/">Wiki</a> - 
						<a href="http://sourceforge.net/project/showfiles.php?group_id=115436">Download</a> - 
						<a href="press.php">In The Press</a>
					</b>
				</td>
			</tr>
			<tr height="400" bgcolor="#ffffff">
				<td>
					<br><br>
					<table cellpadding="0" cellspacing="0" border="0" align="center" width="90%">
						<tr>
							<td>
<?php
	}

	function HtmlFooter()
	{
?>
								<br/>
								<table border="0" width="100%">
								<tr>
									<td valign="bottom" style="font-size: 10px; color: #999999;">Copyright (C) 2004 Jan Kopcsek &lt;codewright [AT] gmx [.] net&gt;</td>
									<td align="right"><A href="http://sourceforge.net"><IMG src="http://sourceforge.net/sflogo.php?group_id=115436&amp;type=1" border="0" alt="SourceForge Logo"></A></td>
								</tr>
								</table>
								<br/>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr height="22"><td><img src="img/foot.gif" border="0"></td></tr>
		</table>
		<br><br>
	</body>
</html>
<?php
	}
?>
