<?php

if( php_sapi_name() != 'cli' ) {
	die("");
}

$end = "-----END PGP MESSAGE-----";
$contents = file_get_contents( $argv[1] );
$entries = explode( $end, $contents );

$ctally = array();
$vtally = array();
$infile = tempnam( "/tmp", "gpg" );
$outfile = tempnam( "/tmp", "gpg" );

foreach ( $entries as $entry ) {
	$entry = trim( $entry.$end );
	
	if ( $entry == $end ) {
		continue;
	}
#	print "{{{$entry}}}\n\n";
	$file = fopen( $infile, "w" );
	fwrite( $file, trim( $entry ) . "\n" );
	fclose( $file );
	`gpg --batch --yes -do $outfile $infile`;
	$lines = file( $outfile );
	$cset = process_line( $lines[0] );
	$vset = process_line( $lines[1] );
	foreach ( $cset as $c ) {
		if  ( !array_key_exists( $c, $ctally ) ) {
			$ctally[$c] = 0;
		}
		$ctally[$c]++;
	}
	foreach ( $vset as $v ) {
		if  ( !array_key_exists( $v, $vtally ) ) {
			$vtally[$v] = 0;
		}

		$vtally[$v]++;
	}
}

unlink( $infile );
unlink( $outfile );

arsort( $ctally );
arsort( $vtally );

print "Contributing representative\n";
foreach ( $ctally as $candidate => $tally ) {
	printf( "%-30s%d\n", $candidate, $tally );
}
	
print "\nVolunteer representative\n";
foreach ( $vtally as $candidate => $tally ) {
	printf( "%-30s%d\n", $candidate, $tally );
}

#-----------------------------------------------------------

function process_line( $line )
{
	$importantBit = substr( $line, strpos( $line, ":" ) + 1 );
	$set = array_map( "trim", explode( ",", $importantBit ) );
	if ( $set[0] == "" ) {
		$set = array();
	}
	return $set;
}


