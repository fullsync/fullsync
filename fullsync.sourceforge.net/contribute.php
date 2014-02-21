<?php
	include("html.php");
	HtmlHeader("Contribute to FullSync", 'help-banner');
?>
	FullSync is looking for:<br />
	<ul>
		<li>Translators<br />
			The translations of FullSync are quite outdated.
			<!-- translatewiki? other translation services? -->
		</li>
		<li>Java developers<br />
			A few things that might happen:
			<ul>
				<li>porting to OSGi/Equinox</li>
				<li>or maybe adopting maven</li>
				<li>splitting the GUI and the core into separate processes (SWT is too unstable for my taste)</li>
				<li>your idea here</li>
			</ul>
		</li>
		<li>Mac testers and developers<br />
			somebody that knows how to package Java applications for Mac users.
		</li>
		<li>lots of new icons<br />
			basically every icon other than the logo needs a new SVG version...
		</li>
	</ul>
	Get in touch in the <a href="https://sourceforge.net/p/fullsync/discussion/">Fullsync discussion forum</a>.
<?php
	HtmlFooter();
