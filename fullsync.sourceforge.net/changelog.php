<?php
	include( "html.php" );
	HtmlHeader( "Press Releases" );
?>
	<h1>Press Releases</h1>

<?php 
	$f = fopen( "http://cvs.sourceforge.net/viewcvs.py/fullsync/FullSync/CHANGELOG?view=markup", "r" );
	$doPrint = 0;
	while( $line = fgets( $f, 10000 ) )
	{
		if( substr( $line, 0, 6 ) == "</pre>" && $doPrint == 2 ) {
			$doPrint = 0;
		} else if( $doPrint == 2 ) {
			print $line;
		} else if( substr( $line, 0, 5 ) == "<pre>" ) {
			$doPrint++;
			if( $doPrint == 2 )
			print substr( $line, 5 );
		}
	}
	fclose( $f );
?>

<?php
	HtmlFooter();
