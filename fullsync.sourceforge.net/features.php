<?php
	include( "html.php" );
	HtmlHeader( "Features" );
?>
	<h1>Features</h1>
	<dl>
		<dt>Publication and update of websites, synchronization of local directories and making backups of your data.</dt>
		<dd>Because of the large amount of options provided, you can use FullSync to publish
			and update websites, synchronize files between your pc and your notebook or even 
			backup files to a storage device every few hours.<br/><br/></dd>
		<dt>multiple modes: update destination, synchronize source with destination</dt>
		<dd>FullSync can be configured to look for changes in different ways. you can
			decide to make it look only on source changes, or both, source and destination
			or only on the destination. Additionally you can decide whether deletions should
			be performed or just reported.<br/><br/></dd>
		<dt>flexible rules, allowing all kinds of exclusion/inclusion</dt>
		<dd>The rules that can be defined are very powerful. For example pattern matches
			can be regular expressions and you can first tell FullSync which files to take
			and can then define exceptions. Additionally you can configure criterias
			which decide whether a file has changed or not. This is good when using 
			filesystems which can not set modification times or where the size changes
			while transferring from the source to destination (ftp).<br/><br/></dd>
		<dt>configuration and rule-rewriting per directory possible</dt>
		<dd>For advanced configuration you can deploy xml files in the synchronized
			paths which can overwrite the rules for this branch. So you could for example
			change the synchronization direction for a single directory, or you could
			change filters or even disallow rule rewriting.<br/><br/></dd>
		<dt>buffered filesystems (so the tool only sees files it created itself)</dt>
		<dd>To allow updating just the important parts or a directory tree, FullSync 
			can buffer the view of the destination filesystem and keeps track which
			files it transferred. This way additional files on the destination are ignored and
			changes on destination files can be reported. Reporting or finding changes
			will be implemented for the source later.<br/><br/></dd>
		<dt>multiple protocols (FTP,SFTP,SMB...)</dt>
		<dd>The structure of FullSync makes it easy for a developer to add new types of
			filesystems, which can be used as source or destination in the synchronization process.
			Currently local files, ftp, sftp and samba are supported. Zip is planned.<br/><br/></dd>
		<dt>read/write access bufferable and multithreadable</dt>
		<dd>The way the file data is copied is very customizable. This allows to use a
			buffer which loads large masses of source data into the memory and writes
			it all in one run if the buffer is full. That reduces harddisk-jumps and is especially
			good for making backups or synchronizations on one harddisk. For updating webpages
			multithreaded writing is good for using full bandwidth.<br/><br/></dd>
	</dl>
<?php
	HtmlFooter();
