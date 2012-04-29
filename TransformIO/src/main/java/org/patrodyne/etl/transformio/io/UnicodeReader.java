// PatroDyne: Patron Supported Dynamic Executables, http://patrodyne.org
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;

/**
 * Generic Unicode text reader, using BOM mark to identify the encoding.
 * If BOM is not found then use a specified default or system encoding.
 * 
 * @author Rick O'Sullivan
 */
public class UnicodeReader
	extends Reader
{
	private static final int MIN_BOM_BYTES = ByteOrderMark.MIN_BYTES;
	private static final int MAX_BOM_BYTES = ByteOrderMark.MAX_BYTES;

	private String defaultEncoding;
	/**
	 * Get the default encoding name.
	 * @return The default encoding name, may be null.
	 */
	public String getDefaultEncoding()
	{
		return defaultEncoding;
	}
	private void setDefaultEncoding(String defaultEncoding)
	{
		this.defaultEncoding = defaultEncoding;
	}

	/**
	 * Get stream encoding or NULL. 
	 */
	public String getEncoding()
		throws IOException
	{
		return getInputStreamReader().getEncoding();
	}

	/**
	 * Construct with a File and default encoding.
	 * 
	 * @param file File to read;
	 * 
	 * @throws FileNotFoundException When the file does not exist.
	 */
	public UnicodeReader(File file)
		throws FileNotFoundException
	{
		this(file, null);
	}

	/**
	 * Construct with a File and default encoding.
	 * 
	 * @param file File to read;
	 * @param defaultEncoding default encoding, if null use system default.
	 * 
	 * @throws FileNotFoundException When the file does not exist.
	 */
	public UnicodeReader(File file, String defaultEncoding)
		throws FileNotFoundException
	{
		this(new FileInputStream(file), defaultEncoding);
	}

	/**
	 * Construct with an input stream and default system encoding;
	 * 
	 * @param inputStream input stream to be read.
	 */
	public UnicodeReader(InputStream inputStream)
	{
		this(inputStream, null);
	}

	/**
	 * Construct with an input stream and default encoding;
	 * 
	 * @param inputStream input stream to be read
	 * @param defaultEncoding default encoding, if null use system default.
	 */
	public UnicodeReader(InputStream inputStream, String defaultEncoding)
	{
		super(inputStream);
		setInputStream(inputStream);
		setDefaultEncoding(defaultEncoding);
	}

	/** @see java.io.Reader#read(char[], int, int) */
	public int read(char[] cbuf, int off, int len)
		throws IOException
	{
		return getInputStreamReader().read(cbuf, off, len);
	}

	/** @see java.io.Reader#close() */
	public void close()
		throws IOException
	{
		getInputStreamReader().close();
	}

	private InputStream inputStream;
	private InputStream getInputStream()
	{
		return inputStream;
	}
	private void setInputStream(InputStream inputStream)
	{
		this.inputStream = inputStream;
	}

	private PushbackInputStream pushBackInputStream;
	private PushbackInputStream getPushBackInputStream()
	{
		if ( pushBackInputStream == null )
			setPushBackInputStream(new PushbackInputStream(getInputStream(), MAX_BOM_BYTES));
		return pushBackInputStream;
	}
	private void setPushBackInputStream(PushbackInputStream pushBackInputStream)
	{
		this.pushBackInputStream = pushBackInputStream;
	}
	
	private InputStreamReader inputStreamReader;
	private InputStreamReader getInputStreamReader()
		throws IOException
	{
		if ( inputStreamReader == null )
		{
			String encoding = getDefaultEncoding();
			byte bytes[] = new byte[MAX_BOM_BYTES];
			int nBytesRead = getPushBackInputStream().read(bytes, 0, bytes.length);
			if ( nBytesRead > 0 )
			{
				int nBytesPushBack = nBytesRead;
				if ( nBytesRead >= MIN_BOM_BYTES )
				{
					ByteOrderMark bom = ByteOrderMark.resolve(bytes, nBytesRead);
					if ( bom != null )
					{
						// BOM found, push back any non-BOM bytes.
						nBytesPushBack = nBytesRead - bom.getBytes().length;
						encoding = bom.getEncoding();
					}
				}
				// Push back any unused bytes.
				if (nBytesPushBack > 0)
				{
					int start = nBytesRead - nBytesPushBack;
					getPushBackInputStream().unread(bytes, start, nBytesPushBack);
				}
			}

			// Use specified encoding, if present; otherwise, system encoding.
			if (encoding != null)
				setInputStreamReader(new InputStreamReader(getPushBackInputStream(), encoding));
			else
				setInputStreamReader(new InputStreamReader(getPushBackInputStream()));
		}
		return inputStreamReader;
	}
	private void setInputStreamReader(InputStreamReader unicodeReader)
	{
		this.inputStreamReader = unicodeReader;
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:

