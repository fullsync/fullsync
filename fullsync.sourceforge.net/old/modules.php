<?php

	class Module
	{
		var $name;
		var $version;
		var $mirrors;

		function Module( $name, $version, $mirrors )
		{
			$this->name = $name;
			$this->version = $version;
			$this->mirrors =& $mirrors;
		}
	}

	$modules = array(
		new Module( "FullSync", "0.7.1", array(
			"http://mesh.dl.sourceforge.net/sourceforge/fullsync/FullSync-0.7.1.zip"
		) )
	);

	

	if( isset( $_REQUEST['module'] ) )
	{
		$name = $_REQUEST['module'];
		foreach( $modules as $m )
		{
			if( $m->name == $name )
			{
				print implode( "\n", $m->mirrors );
				break;
			}
		}
	} else {
		foreach( $modules as $m )
			print "{$m->name}={$m->version}\n";
	}

?>
