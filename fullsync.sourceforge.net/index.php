<?php
/*
	if( !file_exists( "counter" ) )
		 $count = array( 0 );
	else $count = file( "counter" );
	$count[0]++;
	$f = fopen( "counter", "w" );
	fputs( $f, $count[0] );
	fclose( $f );
*/
	include( "html.php" );
	HtmlHeader( "Home" );
?>
	<p style="font-size: 20"><b>Welcome to FullSync</b></p>
	FullSync is a universal file synchronization and backup tool which is highly customizable and expandable. 
	It is especially for developers, but the basic functionality is easy enough for everyone.
	<br><br>
	current Version: 0.9.1 [2005-03-08] (<a href="http://cvs.sourceforge.net/viewcvs.py/fullsync/FullSync/CHANGELOG?view=markup">ChangeLog</a>)<br>
	<a href="http://sourceforge.net/projects/fullsync/">get it at sourceforge.net</a><br><br>
	<a href="donate.php">If you use and like FullSync and want to support it, please consider donating.</a><br><br>
	<b>Features:</b>
	<ul>
		<li>Publication and update of websites, synchronization of local directories and making backups of your data.</li>
		<!--<li>multiple modes: update destination, synchronize source with destination</li>-->
		<li>configuration and rule-rewriting per directory possible</li>
		<li>flexible rules, allowing all kinds of exclusion/inclusion</li>
		<li>buffered filesystems (so the tool only sees files it created itself)</li>
		<li>multiple protocols supported (FTP,SFTP,SMB...)</li>
		<li>read/write access bufferable and multithreadable</li>
	</ul>
	<br><br>
	<a href="screenshots.php">Screenshots</a><br><br>
	<a href="features.php">Features explained</a><br><br>
<?php
	HtmlFooter();
?>
