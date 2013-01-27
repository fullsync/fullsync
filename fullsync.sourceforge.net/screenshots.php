<?php
	include( "html.php" );
	HtmlHeader( "Screenshots" );
?>
	<h1>Screenshots</h1>
	<img src="pictures/2004-12-12-MainWindow.png"><br>
	The main window with the new profile list. You can still switch back<br>
	to the table if you wish, but it has a bit less features.<br><br><br>
	<img src="pictures/2004-11-25-TaskDecision.png"><br>
	If you select an interactive synchronization process<br>
	you will get this screen to see and change the actions<br>
	that will be performed.<br/><br/><br/>
	<img src="pictures/2004-11-25-Profile_Details.png"><br>
	This dialog is used to configure the profiles. Here you specify<br>
	what, how and when should be synchronized.<br><br><br>
	<img src="pictures/2004-11-25-Profile_Scheduler.png"><br>
	Here you can specify when a profile is executed automatically.<br>
	You can either choose an interval, a crontab-like scheduling<br>
	(you can see in the picture) or none.<br><br><br>
<?php
	HtmlFooter();
