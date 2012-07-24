/*
 * Copyright (c) 2002-2012, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.archive.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * ZIP archive creator
 * 
 */
public final class ZipGenerateUtil
{
	public static final String ARCHIVE_TYPE_ZIP = "ZIP";
	public static final String ARCHIVE_MIME_TYPE_ZIP = "application/zip";
	public static final int CONSTANTE_FILE_BUFFER = 4096;

	/**
	 * The archive methods
	 * @param strFolderToArchive path to the folder to archive
	 * @param strArchiveDestination path to the destination folder which will store the archive
	 * @param strArchiveName the name of the archive
	 * @throws IOException {@link IOException}
	 * @throws FileNotFoundException {@link FileNotFoundException}
	 */
	public static void archiveDirectory( String strFolderToArchive, String strArchiveDestination, String strArchiveName ) throws IOException, FileNotFoundException
	{
		BufferedOutputStream bos = null;
		File folderToZip;
		ZipOutputStream zos = null;
		StringBuilder strDestinationFile = new StringBuilder( );
		strDestinationFile.append( strArchiveDestination );
		strDestinationFile.append( File.separator );
		strDestinationFile.append( strArchiveName );

		try
		{
			folderToZip = new File( strFolderToArchive );
			bos = new BufferedOutputStream( new FileOutputStream( strDestinationFile.toString( ) ) );
			zos = new ZipOutputStream( bos );
			zipDirectory( folderToZip, zos, "" );
		}
		finally
		{
			zos.close( );
		}
	}

	/**
	 * This Method zip a directory and all files contained in it.
	 * @param dir : directory to zip
	 * @param zos : zip object
	 * @param path : Current path in the zip
	 * @throws IOException exception if there is an error during the operation
	 * @throws FileNotFoundException exception if the file is not found
	 */
	private static void zipDirectory( File dir, ZipOutputStream zos, String path ) throws IOException, FileNotFoundException
	{
		if ( ( dir != null ) && dir.isDirectory( ) )
		{
			zipFileInDirectory( dir, zos, path );
		}
	}

	/**
	 * Zip a given directory ( Recursive function )
	 * @param dir : Current directory to zip
	 * @param zos : Zip object
	 * @param path : Current path in the zip object
	 * @throws IOException exception if there is an error during the operation
	 * @throws FileNotFoundException exception if the file is not found
	 */
	private static void zipFileInDirectory( File dir, ZipOutputStream zos, String path ) throws IOException, FileNotFoundException
	{
		StringBuilder strEntry = null;
		String strDirectory = null;

		if ( ( dir != null ) && dir.isDirectory( ) )
		{
			File[] entries = dir.listFiles( );
			int sz = entries.length;

			for ( int j = 0; j < sz; j++ )
			{
				// directory
				if ( entries[j].isDirectory( ) )
				{
					// add a new directory in zip
					strEntry = new StringBuilder( );
					strEntry.append( path );
					strEntry.append( replaceAccent( entries[j].getName( ) ) );
					strEntry.append( File.separator );
					strDirectory = strEntry.toString( );

					// ZipEntry ze = new ZipEntry( strDirectory );
					// zos.putNextEntry( ze );

					// call zipDirectory
					strEntry = new StringBuilder( );
					strEntry.append( dir.getAbsolutePath( ) );
					strEntry.append( File.separator );
					strEntry.append( entries[j].getName( ) );

					File newDir = new File( strEntry.toString( ) );
					zipDirectory( newDir, zos, strDirectory );
				}

				// file
				else
				{
					// read the file to zip
					FileInputStream bis = null;

					try
					{
						bis = new FileInputStream( entries[j].getAbsolutePath( ) );

						// create a new entry in zip
						ZipEntry ze = new ZipEntry( path + replaceAccent( entries[j].getName( ) ) );
						zos.putNextEntry( ze );

						byte[] tab = new byte[CONSTANTE_FILE_BUFFER];
						int read = -1;

						do
						{
							read = bis.read( tab );

							if ( read > 0 )
							{
								zos.write( tab, 0, read );
							}
						} while ( read > 0 );

						zos.closeEntry( );
					}

					finally
					{
						bis.close( );
					}
				}
			}
		}
	}

	/**
	 * the key of generate archive service
	 * @return the key of the genarate service
	 */
	public static String getKey( )
	{
		return ARCHIVE_TYPE_ZIP;
	}

	/**
	 * 
	 * @return the mime type of the archive
	 */
	public static String getMimeType( )
	{
		return ARCHIVE_MIME_TYPE_ZIP;
	}

	private static String replaceAccent( String strString )
	{
		return Normalizer.normalize( strString, Normalizer.Form.NFC ).replaceAll( "[^\\p{ASCII}]", "" );
	}
}
