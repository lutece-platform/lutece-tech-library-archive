/*
 * Copyright (c) 2002-2026, City of Paris
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests of {@link ZipGenerateUtil}.
 */
public class ZipGenerateUtilTest
{
    @TempDir
    Path _tempDir;

    /**
     * Archives a folder with a sub folder and checks every file is in the zip with its content.
     *
     * @throws IOException
     *             if a file cannot be written or read
     */
    @Test
    public void testArchiveDirectory( ) throws IOException
    {
        Path source = Files.createDirectories( _tempDir.resolve( "source" ) );
        Files.writeString( source.resolve( "root.txt" ), "root content" );
        Path sub = Files.createDirectories( source.resolve( "sub" ) );
        Files.writeString( sub.resolve( "child.txt" ), "child content" );
        Path destination = _tempDir.resolve( "out/archives" );

        ZipGenerateUtil.archiveDirectory( source.toString( ), destination.toString( ), "test.zip" );

        Map<String, String> entries = readEntries( destination.resolve( "test.zip" ).toFile( ) );
        assertEquals( 2, entries.size( ), "two files expected in the archive" );
        assertEquals( "root content", entries.get( "root.txt" ), "root file content" );
        assertEquals( "child content", entries.get( "sub/child.txt" ), "sub folder file content" );
    }

    /**
     * Checks that the accents are removed from the entry names written in the archive.
     *
     * @throws IOException
     *             if a file cannot be written or read
     */
    @Test
    public void testArchiveEntryNamesWithoutAccent( ) throws IOException
    {
        Path source = Files.createDirectories( _tempDir.resolve( "accents" ) );
        Files.writeString( source.resolve( "résumé.txt" ), "accented" );

        ZipGenerateUtil.archiveDirectory( source.toString( ), _tempDir.toString( ), "accents.zip" );

        Map<String, String> entries = readEntries( _tempDir.resolve( "accents.zip" ).toFile( ) );
        assertEquals( 1, entries.size( ), "one file expected in the archive" );
        assertEquals( "accented", entries.get( "resume.txt" ), "accents removed from the entry name" );
    }

    /**
     * Checks the archive key and mime type.
     */
    @Test
    public void testKeyAndMimeType( )
    {
        assertEquals( "ZIP", ZipGenerateUtil.getKey( ), "archive key" );
        assertEquals( "application/zip", ZipGenerateUtil.getMimeType( ), "archive mime type" );
    }

    /**
     * Reads every entry of a zip file.
     *
     * @param file
     *            the zip file
     * @return the content of each entry, by entry name
     * @throws IOException
     *             if the zip cannot be read
     */
    private static Map<String, String> readEntries( File file ) throws IOException
    {
        Map<String, String> entries = new HashMap<>( );
        try ( ZipFile zip = new ZipFile( file ) )
        {
            for ( ZipEntry entry : Collections.list( zip.entries( ) ) )
            {
                try ( InputStream in = zip.getInputStream( entry ) )
                {
                    entries.put( entry.getName( ), new String( in.readAllBytes( ), StandardCharsets.UTF_8 ) );
                }
            }
        }
        return entries;
    }
}
