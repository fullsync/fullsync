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

include( "html.php" );
	HtmlHeader( "Welcome to FullSync" );
?>
	<p>
	FullSync is a universal file synchronization and backup tool which is highly customizable and expandable.
	It is especially for developers, but the basic functionality is easy enough for everyone.
	</p>
	<h2>Release History</h2>
	<ol>
<?php
		$versions = getVersions(3);
		$first = true;
		foreach ($versions as $v) {
			echo "\t\t<li>Version ${v['version']} (${v['releaseDate']}):<br />${v['changes']}";
			if ($first) {
				echo "<a href=\"https://sourceforge.net/projects/fullsync/files/FullSync%20${v['version']}/\">Download Now</a> or";
				$first = false;
			}
			if (isset($v['manual'])) {
				echo " view an online version of the <a href=\"docs/manual-${v['version']}/${v['manual']}\">documentation</a>.";
			}
			echo "</li>\n";
		}
?>
	</ol>
	View <a href="changelog.php">all releases</a>.
	<h2>Features:</h2>
	<ul>
		<li>Publication and update of websites, synchronization of local directories and making backups of your data.</li>
		<li>Configuration and rule-rewriting per directory possible.</li>
		<li>Flexible rules, allowing all kinds of exclusion/inclusion.</li>
		<li>Multiple protocols supported FTP, SFTP, SMB/CIFS(Windows Shares) and of course local files.</li>
		<li>Scheduled execution of synchronizations using intervals or a crontab like scheduling.</li>
		<li>Ability to remotely control a FullSync instance running on another computer.</li>
	</ul>
<?php
	HtmlFooter();
