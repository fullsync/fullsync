<?php
/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301, USA.
 *
 * For information about the authors of this project Have a look
 * at the AUTHORS file in the root of this project.
 */

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
