<?php
	include( "html.php" );
	HtmlHeader( "Donate" );
?>
	<h1>Donate</h1>
	<span class="donateIcon"><input type="image" src="https://www.paypal.com/en_US/i/btn/x-click-but04.gif" name="submit"></span>

	<p>
		We have spent and will spend many, many hours of our free time to
		build and extend this program, so if you like and use FullSync,
		please consider donating. This will keep us interested in extending
		the program.
	</p>
	
	<form action="https://www.paypal.com/cgi-bin/webscr" method="post">
		<input type="hidden" name="cmd" value="_xclick">
		<input type="hidden" name="business" value="codewright@gmx.net">
		<input type="hidden" name="item_name" value="FullSync Donation">
		<input type="hidden" name="no_shipping" value="1">
		<input type="hidden" name="return" value="http://fullsync.sourceforge.net/thanks.php">
		<input type="hidden" name="cancel_return" value="http://fullsync.sourceforge.net/">
		<!--<input type="hidden" name="no_note" value="1">-->
		<input type="hidden" name="currency_code" value="EUR">
		<input type="hidden" name="tax" value="0">

		<ul class="donationlist">
			<li><label><input type="radio" name="amount" value="5.00" /> <span class="amount">5&euro;</span> <span class="description">Your program is nice, but thats all i can spend :-/</span></label></li>
			<li><label><input type="radio" name="amount" value="10.00" checked="checked" /> <span class="amount">10&euro;</span> <span class="description">Thanks for this good program.</span></label></li>
			<li><label><input type="radio" name="amount" value="20.00"/> <span class="amount">20&euro;</span> <span class="description">Your application is just wonderful, i love it.</span></label></li>
			<li><label><input type="radio" name="amount" value="30.00"/> <span class="amount">30&euro;</span> <span class="description">I'm using FullSync at work and it makes a good job there.</span></label></li>
			<li><label><input type="radio" name="amount" value="100.00"/> <span class="amount">100&euro;</span> <span class="description">I need nothing more than FullSync, so... here is all my money ;) </span></label></li>
			<li><label><input type="radio" name="amount" value=""/> <span class="amount">Other</span> <span class="description">I had some other amount in mind... let me choose. </span></label></li>
		</ul>
		<input id="donate-button" type="submit" value="Donate" />
	</form>
<?php
	HtmlFooter();
