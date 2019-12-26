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

include "html.php";
HtmlHeader("Download FullSync");
$version = getVersions(1);
$version = $version[0]['version'];
$downloadBase = SOURCEFORGE_PROJECTS_URL . "/files/FullSync%20$version/FullSync-$version";
?>
	<h2>for Windows</h2>
	<p>
		Tested with Windows 7 AMD64, and Windows 10 AMD64.
		<a href="<?php echo $downloadBase; ?>-Windows.msi/download">Download FullSync for Windows</a><br />
		Java must be installed, you can get it at <a href="http://www.java.com/">www.java.com</a>
	</p>

	<h2>for Linux</h2>
	<iframe id="obs-download-frame" src="https://software.opensuse.org/download/package.iframe?project=home%3Acobexer%3AFullSync&amp;package=FullSync"></iframe>

	<h2>FullSync without Installer</h2>
	<p>
		Theese downloads contain both 32bit and 64bit versions of FullSync.
	</p>
	<ul>
		<li>for <a href="<?php echo $downloadBase; ?>-win.zip/download">Windows</a></li>
		<li>for <a href="<?php echo $downloadBase; ?>-linux.tar.gz/download">Linux</a></li>
		<li>for <a href="<?php echo $downloadBase; ?>-mac.tar.gz/download">Mac</a> please test this download
		and <a href="https://sourceforge.net/p/fullsync/discussion/">tell me if it works for you</a>.</li>
	</ul>

	<h2>Source Code</h2>
	<p>
		The source code of FullSync Version <?php echo $version; ?> can be obtained here:
		<a href="<?php echo $downloadBase; ?>-src.tar.gz/download">FullSync-<?php echo $version; ?>-src.tar.gz</a><br />
		To access the FullSync Source repository head over to
		<a href="https://sourceforge.net/p/fullsync/git/">the git repository</a>.
	</p>

	<h2>Older versions</h2>
	<p>
		You can get older versions of FullSync from
		<a href="<?php echo SOURCEFORGE_PROJECTS_URL; ?>/files/">the download archive</a>.
	</p>

<?php
HtmlFooter('download-button');
