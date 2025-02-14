/** (C) Copyright 1998-2008 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

*/

package org.smartfrog.vast.archive;

import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;

public class ZipArchive extends BaseArchive {
	ZipOutputStream out;

	public ZipArchive(String inPath) {
		ArchivePath = inPath;
	}

	public void close() throws IOException {
		out.close();
	}

	public void create() throws IOException {
		// open the zip output stream
		out = new ZipOutputStream(new FileOutputStream(ArchivePath));
	}

	public void putNextEntry(String inPath, String inRelPath) throws IOException {
		File file = new File(inPath);

		// create an entry in the zip file
		ZipEntry entry = new ZipEntry(inRelPath);
		entry.setSize(file.length());
		out.putNextEntry(entry);

		if (file.isFile()) {
			// copy the file's content
			FileInputStream reader = new FileInputStream(file);
			byte buffer[] = new byte[BUFFER_SIZE];
			int in;
			while ((in = reader.read(buffer, 0, buffer.length)) > 0) {
				out.write(buffer, 0, in);
			}
			reader.close();
		}

		// close entry
		out.closeEntry();
	}

	public void extract(String inDestination) throws IOException {
		File destFolder = new File(inDestination);
		if (!destFolder.isDirectory())
			throw new IOException("Destination is not a folder.");

		// open the archive
		ZipInputStream in = new ZipInputStream(new FileInputStream(ArchivePath));
		ZipEntry entry;
		File dest;
		byte [] buffer = new byte[BUFFER_SIZE];
		int read;

		// parse the entries
		while ((entry = in.getNextEntry()) != null) {
			dest = new File(destFolder.getPath() + "/" + entry.getName());
			if (entry.isDirectory())
				// create the directory
				dest.mkdir();
			else {
				// create the file
				FileOutputStream out = new FileOutputStream(dest);

				// extract the data
				while ((read = in.read(buffer, 0, BUFFER_SIZE)) > 0) {
					out.write(buffer, 0, read);
				}

				// close the file
				out.close();
			}
		}

		// close the archive
		in.close();
	}
}
