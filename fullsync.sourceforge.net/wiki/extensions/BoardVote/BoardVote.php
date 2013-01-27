<?php

# Wikimedia Foundation Board of Trustees Election

# Register extension
$wgExtensionFunctions[] = "wfBoardvoteSetup";

# This extension act as a special page
require_once( '../../includes/SpecialPage.php' );

# Default settings
if ( !isset( $wgBoardVoteDB ) ) $wgBoardVoteDB = "boardvote";
if ( !isset( $wgContributingCandidates ) ) $wgContributingCandidates = array();
if ( !isset( $wgContributingCandidates ) ) $wgVolunteerCandidates = array();
if ( !isset( $wgGPGCommand ) ) $wgGPGCommand = "gpg";
if ( !isset( $wgGPGRecipient ) ) $wgGPGRecipient = "boardvote";
if ( !isset( $wgGPGHomedir ) ) $wgGPGHomedir = false;
if ( !isset( $wgGPGPubKey ) ) $wgGPGPubKey = "C:\\Program Files\\gpg\\pub.txt";

function wfBoardvoteSetup()
{
# Look out, freaky indenting
# The class definition is inside the function because it has to be performed after SpecialPage is defined

class BoardVotePage extends SpecialPage {
	var $mPosted, $mContributing, $mVolunteer, $mDBname, $mUserDays, $mUserEdits;
	var $mHasVoted, $mAction, $mUserKey, $mId, $mFinished;

	function BoardVotePage() {
		SpecialPage::SpecialPage( "Boardvote" );
	}

	function execute( $par ) {
		global $wgUser, $wgDBname, $wgInputEncoding, $wgRequest, $wgBoardVoteDB;

		$this->mUserKey = iconv( $wgInputEncoding, "UTF-8", $wgUser->getName() ) . "@$wgDBname";
		$this->mPosted = $wgRequest->wasPosted();
		$this->mContributing = $wgRequest->getVal( "contributing", array() );
		$this->mVolunteer = $wgRequest->getVal( "volunteer", array() );
		$this->mId = $wgRequest->getInt( "id", 0 );
		
		$this->mDBname = $wgBoardVoteDB;
		$this->mHasVoted = $this->hasVoted( $wgUser );
		
		if ( $par ) {
			$this->mAction = $par;
		} else {
			$this->mAction = $wgRequest->getText( "action" );
		}

		$this->setHeaders();

		if ( time() > 1087084800 ) {
			$this->mFinished = true; 
		} else {
			$this->mFinished = false;
		}
		if ( $this->mAction == "list" ) {
			$this->displayList();
		} elseif ( $this->mAction == "dump" ) {
			$this->dump();
		} elseif ( $this->mAction == "strike" ) {
			$this->strike( $this->mId, false );
		} elseif ( $this->mAction == "unstrike" ) {
			$this->strike( $this->mId, true );
		} elseif( $this->mAction == "vote" && !$this->mFinished ) {
			if ( !$wgUser->getID() ) {
				$this->notLoggedIn();
			} else {
				$this->getQualifications( $wgUser );
				if ( $this->mUserDays < 90 ) {
					$this->notQualified();
				} elseif ( $this->mPosted ) {
					$this->logVote();
				} else {
					$this->displayVote();
				}
			}
		} else {
			$this->displayEntry();
		}
	}
	
	function displayEntry() {
		global $wgOut;
		$wgOut->addWikiText( wfMsg( "boardvote_entry" ) );
	}

	function hasVoted( &$user ) {
		global $wgDBname;
		$row = wfGetArray( $this->mDBname . ".log", array( "1" ), 
		  array( "log_user_key" => $this->mUserKey ), "BoardVotePage::getUserVote" );
		if ( $row === false ) {
			return false;
		} else {
			return true;
		}
	}

	function logVote() {
		global $wgUser, $wgDBname, $wgIP, $wgOut, $wgGPGPubKey;
		$fname = "BoardVotePage::logVote";
		
		$now = wfTimestampNow();
		$record = $this->getRecord();
		$encrypted = $this->encrypt( $record );
		$gpgKey = file_get_contents( $wgGPGPubKey );
		$db = $this->mDBname;
		
		# Mark previous votes as old
		$encKey = wfStrencode( $this->mUserKey );
		$sql = "UPDATE $db.log SET log_current=0 WHERE log_user_key='$encKey'";
		wfQuery( $sql, DB_WRITE, $fname );
		
		# Add vote to log
		wfInsertArray( "$db.log", array(
			"log_user" => $wgUser->getID(),
			"log_user_text" => $wgUser->getName(),
			"log_user_key" => $this->mUserKey,
			"log_wiki" => $wgDBname,
			"log_edits" => $this->mUserEdits,
			"log_days" => $this->mUserDays,
			"log_record" => $encrypted,
			"log_ip" => $wgIP,
			"log_xff" => @$_SERVER['HTTP_X_FORWARDED_FOR'],
			"log_ua" => $_SERVER['HTTP_USER_AGENT'],
			"log_timestamp" => $now,
			"log_current" => 1
		), $fname );

		$wgOut->addWikiText( wfMsg( "boardvote_entered", $record, $gpgKey, $encrypted ) );
	}
	
	function displayVote() {
		global $wgContributingCandidates, $wgVolunteerCandidates, $wgOut;
		
		$thisTitle = Title::makeTitle( NS_SPECIAL, "Boardvote" );
		$action = $thisTitle->getLocalURL( "action=vote" );
		if ( $this->mHasVoted ) {
			$intro = wfMsg( "boardvote_intro_change" );
		} else {
			$intro = wfMsg( "boardvote_intro" );
		}
		$contributing = wfMsg( "boardvote_contributing" );
		$volunteer = wfMsg( "boardvote_volunteer" );
		$ok = wfMsg( "ok" );
		
		$candidatesV = $candidatesC = array();
		foreach( $wgContributingCandidates as $i => $candidate ) {
			$candidatesC[] = array( $i, $candidate );
		}
		foreach ( $wgVolunteerCandidates as $i => $candidate ) {
			$candidatesV[] = array( $i, $candidate );
		}

		srand ((float)microtime()*1000000);
		shuffle( $candidatesC );
		shuffle( $candidatesV );

		$text = "
		  $intro
		  <form name=\"boardvote\" id=\"boardvote\" method=\"post\" action=\"$action\">
		  <table border='0'><tr><td colspan=2>
		  <h2>$contributing</h2>
		  </td></tr>";
		foreach ( $candidatesC as $candidate ) {
			$text .= $this->voteEntry( $candidate[0], $candidate[1], "contributing" );
		}
		$text .= "
		  <tr><td colspan=2>
		  <h2>$volunteer</h2></td></tr>";
		foreach ( $candidatesV as $candidate ) {
			$text .= $this->voteEntry( $candidate[0], $candidate[1], "volunteer" );
		}
		
		$text .= "<tr><td>&nbsp;</td><td>
		  <input name=\"submit\" type=\"submit\" value=\"$ok\">
		  </td></tr></table></form>";
		$text .= wfMsg( "boardvote_footer" );
		$wgOut->addHTML( $text );
	}

	function voteEntry( $index, $candidate, $name ) {
		return "
		<tr><td align=\"right\">
		  <input type=\"checkbox\" name=\"{$name}[{$index}]\" value=\"1\">
		</td><td align=\"left\">
		  $candidate
		</td></tr>";
	}

	function notLoggedIn() {
		global $wgOut;
		$wgOut->addWikiText( wfMsg( "boardvote_notloggedin" ) );
	}
	
	function notQualified() {
		global $wgOut;
		$wgOut->addWikiText( wfMsg( "boardvote_notqualified", $this->mUserDays ) );
	}
	
	function getRecord() {
		global $wgContributingCandidates, $wgVolunteerCandidates;
		
		$file = @fopen( "/dev/urandom", "r" );
		if ( $file ) {
			$salt = implode( "", unpack( "H*", fread( $file, 64 ) ));
			fclose( $file );
		} else {
			$salt = Parser::getRandomString() . Parser::getRandomString();
		}
		
		$record = 
		  "Contributing: " . implode( ", ", wfArrayLookup( $wgContributingCandidates, $this->mContributing ) ). "\n" .
		  "Volunteer: " . implode( ", ", wfArrayLookup( $wgVolunteerCandidates , $this->mVolunteer ) ). "\n" .
		  "Salt: $salt\n";
		return $record;
	}

	function encrypt( $record ) {
		global $wgGPGCommand, $wgGPGRecipient, $wgGPGHomedir;
		# Get file names
		$input = tempnam( "/tmp", "gpg_" );
		$output = tempnam( "/tmp", "gpg_" );

		# Write unencrypted record
		$file = fopen( $input, "w" );
		fwrite( $file, $record );
		fclose( $file );

		# Call GPG
		$command = wfEscapeShellArg( $wgGPGCommand ) . " --batch --yes -ear " . 
		  wfEscapeShellArg( $wgGPGRecipient ) . " -o " . wfEscapeShellArg( $output );
		if ( $wgGPGHomedir ) {
			$command .= " --homedir " . wfEscapeShellArg( $wgGPGHomedir );
		} 
		$command .= " " . wfEscapeShellArg( $input );

		shell_exec( $command );

		# Read result
		$result = file_get_contents( $output );

		# Delete temporary files
		unlink( $input );
		unlink( $output );
		
		return $result;
	}

	function getQualifications( &$user ) {
		$id = $user->getID();
		if ( !$id ) {
			$this->mUserDays = 0;
			$this->mUserEdits = 0;
			return;
		}

		# Count contributions and find earliest edit
		# First cur
		$sql = "SELECT COUNT(*) as n, MIN(cur_timestamp) as t FROM cur WHERE cur_user=$id";
		$res = wfQuery( $sql, DB_READ, "BoardVotePage::getQualifications" );
		$cur = wfFetchObject( $res );
		wfFreeResult( $res );

		# If the user has stacks of contributions, don't check old as well
		$now = time();
		if ( is_null( $cur->t ) ) {
			$signup = $now;
		} else {
			$signup = wfTimestamp2Unix( $cur->t );
		}
		
		$days = ($now - $signup) / 86400;
		if ( $cur->n > 400 && $days > 180 ) {
			$this->mUserDays = 0x7fffffff;
			$this->mUserEdits = 0x7fffffff;
			return;
		}

		# Now check old
		$sql = "SELECT COUNT(*) as n, MIN(old_timestamp) as t FROM old WHERE old_user=$id";
		$res = wfQuery( $sql, DB_READ, "BoardVotePage::getQualifications" );
		$old = wfFetchObject( $res );
		wfFreeResult( $res );
		
		if ( !is_null( $old->t ) ) {
			$signup = min( wfTimestamp2Unix( $old->t ), $signup );
		}
		$this->mUserDays = (int)(($now - $signup) / 86400);
		$this->mUserEdits = $cur->n + $old->n;
	}
	
	function displayList() {
		global $wgOut, $wgOutputEncoding, $wgLang, $wgUser;

		$userRights = $wgUser->getRights();
		$admin = $this->isAdmin();

		$sql = "SELECT * FROM {$this->mDBname}.log ORDER BY log_user_key";
		$res = wfQuery( $sql, DB_READ, "BoardVotePage::list" );
		if ( wfNumRows( $res ) == 0 ) {
			$wgOut->addWikiText( wfMsg( "boardvote_novotes" ) );
			return;
		}
		$thisTitle = Title::makeTitle( NS_SPECIAL, "Boardvote" );
		$sk = $wgUser->getSkin();
		$dumpLink = $sk->makeKnownLinkObj( $thisTitle, wfMsg( "boardvote_dumplink" ), "action=dump" );
		
		$intro = wfMsg( "boardvote_listintro", $dumpLink );
		$hTime = wfMsg( "boardvote_time" );
		$hUser = wfMsg( "boardvote_user" );
		$hEdits = wfMsg( "boardvote_edits" );
		$hDays = wfMsg( "boardvote_days" );
		$hIp = wfMsg( "boardvote_ip" );
		$hUa = wfMsg( "boardvote_ua" );

		$s = "$intro <table border=1><tr><th>
			$hUser
		  </th><th>
			$hTime
		  </th><th>
			$hEdits
		  </th><th>
			$hDays
		  </th>";

		if ( $admin ) {
			$s .= "<th>
			    $hIp
			  </th><th>
			    $hUa
			  </th><th>&nbsp;</th>";
		}
		$s .= "</tr>";

		while ( $row = wfFetchObject( $res ) ) {
			if ( $wgOutputEncoding != "utf-8" ) {
				$user = wfUtf8ToHTML( $row->log_user_key );
			} else {
				$user = $row->log_user_key;
			}
			$time = $wgLang->timeanddate( $row->log_timestamp );
			$cellOpen = "<td>";
			$cellClose = "</td>";
			if ( !$row->log_current ) {
				$cellOpen .= "<font color=\"#666666\">";
				$cellClose = "</font>$cellClose";
			}
			if ( $row->log_strike ) {
				$cellOpen .= "<del>";
				$cellClose = "</del>$cellClose";
			}
			$edits = $row->log_edits == 0x7fffffff ? "many" : $row->log_edits;
			$days = $row->log_days == 0x7fffffff ? "many" : $row->log_days;
			$s .= "<tr>$cellOpen
				  $user
				{$cellClose}{$cellOpen}
				  $time
				{$cellClose}{$cellOpen}
				  $edits
				{$cellClose}{$cellOpen}
				  $days
				{$cellClose}";

			if ( $admin ) {
				if ( $row->log_strike ) {
					$strikeLink = $sk->makeKnownLinkObj( $thisTitle, wfMsg( "boardvote_unstrike" ), 
					  "action=unstrike&id={$row->log_id}" );
				} else {
					$strikeLink = $sk->makeKnownLinkObj( $thisTitle, wfMsg( "boardvote_strike" ),
					  "action=strike&id={$row->log_id}" );
				}

				$s .= "{$cellOpen}
				  {$row->log_ip}
				{$cellClose}{$cellOpen}
				  {$row->log_ua}
				{$cellClose}<td>
				  {$strikeLink}
				</td></tr>";
			} else {
				$s .= "</tr>";
			}
		}
		$s .= "</table>";
		$wgOut->addHTML( $s );
	}

	function dump() {
		global $wgOut, $wgOutputEncoding, $wgLang, $wgUser;

		$sql = "SELECT log_record FROM {$this->mDBname}.log WHERE log_current=1 AND log_strike=0";
		$res = wfQuery( $sql, DB_READ, "BoardVotePage::list" );
		if ( wfNumRows( $res ) == 0 ) {
			$wgOut->addWikiText( wfMsg( "boardvote_novotes" ) );
			return;
		}

		$s = "<pre>";
		while ( $row = wfFetchObject( $res ) ) {
			$s .= $row->log_record . "\n\n";
		}
		$s .= "</pre>";
		$wgOut->addHTML( $s );
	}

	function isAdmin() {	
		global $wgUser;
		$userRights = $wgUser->getRights();
		if ( in_array( "boardvote", $userRights ) ) {
			return true;
		} else {
			return false;
		}
	}

	function strike( $id, $unstrike ) {
		global $wgOut;

		if ( !$this->isAdmin() ) {
			$wgOut->addWikiText( wfMsg( "boardvote_needadmin" ) );
			return;
		}
		$value = $unstrike ? 0 : 1;
		$sql = "UPDATE {$this->mDBname}.log SET log_strike=$value WHERE log_id=$id";
		wfQuery( $sql, DB_WRITE, "BoardVotePage::strike" );

		$title = Title::makeTitle( NS_SPECIAL, "Boardvote" );
		$wgOut->redirect( $title->getFullURL( "action=list" ) );
	}
}

SpecialPage::addPage( new BoardVotePage );

global $wgMessageCache;
$wgMessageCache->addMessages( array(

"boardvote"               => "Wikimedia Board of Trustees election",
"boardvote_entry"         => 
"* [[Special:Boardvote/vote|Vote]]
* [[Special:Boardvote/list|List votes to date]]
* [[Special:Boardvote/dump|Dump encrypted election record]]",
"boardvote_intro"         => "
<p>Welcome to the first elections for the Wikimedia Board of Trustees. We are
voting for two people to represent the community of users on the various
Wikimedia projects: the <strong>Contributing Active User Representative</strong> and the
<strong>Volunteer User Representative</strong>. They will help to determine the future direction
that the Wikimedia projects will take, individually and as a group, and
represent <em>your</em> interests and concerns to the Board of Trustees. They will
decide on ways to generate income and the allocation of moneys raised.</p>

<p>Please read the candidates' statements and responses to queries carefully
before voting. Each of the candidates is a respected user, who has contributed
considerable time and effort to making these projects a welcoming environment
committed to the pursuit and free distribution of human knowledge.</p>

<p>You may vote for as many candidates in each category as you want. The
candidate with the most votes in each position will be declared the winner of that
position. In the event of a tie, a run-off election will be held.</p>

<p>For more information, see:</p>
<ul><li><a href=\"http://meta.wikipedia.org/wiki/Election_FAQ\" class=\"external\">Election FAQ</a></li>
<li><a href=\"http://meta.wikipedia.org/wiki/Election_Candidates\" class=\"external\">Candidates</a></li></ul>
",
"boardvote_intro_change"  => "<p>You have voted before. However you may change 
your vote using the form below. Please check the boxes next to each candidate whom 
you approve of.</p>",
"boardvote_footer"        => "&nbsp;",
"boardvote_entered"       => "Thank you, your vote has been recorded.

If you wish, you may record the following details. Your voting record is:

<pre>$1</pre>

It has been encrypted with the public key of the Election Administrators:

<pre>$2</pre>

The resulting encrypted version follows. It will be displayed publicly on [[Special:Boardvote/dump]]. 

<pre>$3</pre>

[[Special:Boardvote/entry|Back]]",
"boardvote_notloggedin"   => "You are not logged in. To vote, you must use an account
which has existed for at least 90 days.",
"boardvote_notqualified"  => "Sorry, your first contribution was only $1 days ago. 
You need to have been contributing for at least 90 days to vote in this election.",
"boardvote_novotes"       => "Nobody has voted yet.",
"boardvote_contributing"  => "Contributing candidate",
"boardvote_volunteer"     => "Volunteer candidate",
"boardvote_time"          => "Time",
"boardvote_user"          => "User",
"boardvote_edits"         => "Edits",
"boardvote_days"          => "Days",
"boardvote_ip"            => "IP",
"boardvote_ua"            => "User agent",
"boardvote_listintro"     => "<p>This is a list of all votes which have been recorded 
to date. $1 for the encrypted data.</p>",
"boardvote_dumplink"      => "Click here",
"boardvote_strike"        => "Strike",
"boardvote_unstrike"      => "Unstrike",
"boardvote_needadmin"     => "Only election administrators can perform this operation.",
"boardvote_sitenotice"    => "<a href=\"{{localurle:Special:Boardvote/vote}}\">Wikimedia Board Elections</a>:  Vote open until June 12",
));
/*
global $wgSiteNotice, $wgUser;

$title = Title::makeTitle( NS_SPECIAL, "Boardvote/vote" );

if ( $wgUser->getID() != 0 ) {
	$wgSiteNotice = wfMsg( "boardvote_sitenotice" );
}
*/

} # End of extension function

?>
