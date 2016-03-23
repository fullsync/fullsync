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
package net.sourceforge.fullsync.connection;

import org.junit.Test;

public class CommonsVfsConnectionTest {
	@Test
	public void testLocal() throws Exception {
		// FIXME: throws InvalidURLException...
		/*
		 *  * /
		 * String testSample = "Hello,\nthis is some nice text sample!";
		 *
		 * File testingDir = new File( "testing" );
		 * testingDir.mkdirs();
		 * File sample = new File( testingDir, "sample" );
		 * FileWriter writer = new FileWriter( sample );
		 * writer.write( testSample );
		 * writer.close();
		 *
		 *
		 * ConnectionDescription connectionDescription = new ConnectionDescription(testingDir.toURI().toString(),"");
		 * CommonsVfsConnection conn = new CommonsVfsConnection( connectionDescription );
		 * net.sourceforge.fullsync.fs.File root = conn.getRoot();
		 * assertTrue( root.exists() );
		 *
		 * Collection children = root.getChildren();
		 * assertEquals( 1, children.size() );
		 *
		 * net.sourceforge.fullsync.fs.File sampleFile = (net.sourceforge.fullsync.fs.File)children.iterator().next();
		 * assertNotNull( sampleFile );
		 *
		 * assertTrue( sampleFile.exists() );
		 *
		 * assertEquals( "sample", sampleFile.getName() );
		 *
		 * InputStream in = sampleFile.getInputStream();
		 * BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
		 *
		 * assertEquals( "Hello,", reader.readLine() );
		 * assertEquals( "this is some nice text sample!", reader.readLine() );
		 *
		 * reader.close();
		 *
		 * sample.delete();
		 * testingDir.delete();
		 * / *
		 */
	}
}
