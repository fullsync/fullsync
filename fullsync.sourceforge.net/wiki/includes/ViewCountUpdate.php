<?php
# $Id: ViewCountUpdate.php,v 1.4.4.5 2004/07/10 02:23:03 timstarling Exp $
# See deferred.doc
class ViewCountUpdate {

	var $mPageID;

	function ViewCountUpdate( $pageid )
	{
		$this->mPageID = $pageid;
	}

	function doUpdate()
	{
		global $wgDisableCounters, $wgIsMySQL;
		if ( $wgDisableCounters ) { return; }
		$lowpri=$wgIsMySQL?"LOW_PRIORITY":"";
		$sql = "UPDATE $lowpri cur SET cur_counter=(1+cur_counter)," .
		  "cur_timestamp=cur_timestamp WHERE cur_id={$this->mPageID}";
		$res = wfQuery( $sql, DB_WRITE, "ViewCountUpdate::doUpdate" );
	}
}
?>
