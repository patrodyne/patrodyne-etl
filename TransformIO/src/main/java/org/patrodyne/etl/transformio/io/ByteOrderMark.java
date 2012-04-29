// PatroDyne: Patron Supported Dynamic Executables, http://patrodyne.org
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio.io;

import java.util.HashMap;

/**
 * An enumeration of ByteOrderMark (BOM).
 * 
 * The byte order mark (BOM) is a Unicode character used to signal the 
 * endianness (byte order) of a text file or stream. BOM use is optional, 
 * and, if used, should appear at the start of the text stream. Beyond 
 * its specific use as a byte-order indicator, the BOM character may also 
 * indicate which of the several Unicode representations the text is 
 * encoded in.
 * 
 * @author Rick O'Sullivan
 */
public enum ByteOrderMark
{
	UTF8("UTF-8", new byte[] {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF}, false),
	UTF16BE("UTF-16BE", new byte[] {(byte) 0xFE, (byte) 0xFF}, true),
	UTF16LE("UTF-16LE", new byte[] {(byte) 0xFF, (byte) 0xFE}, true),
	UTF32BE("UTF-32BE", new byte[] {(byte) 0x00, (byte) 0x00, (byte) 0xFE, (byte) 0xFF}, true),
	UTF32LE("UTF-32LE", new byte[] {(byte) 0xFF, (byte) 0xFE, (byte) 0x00, (byte) 0x00}, true);
	
	public static int MIN_BYTES = 2;
	public static int MAX_BYTES = 4;
	
	private static HashMap<String, ByteOrderMark> aliases = 
		new HashMap<String, ByteOrderMark>();
	static
	{
		aliases.put("ASCII", UTF8);
		aliases.put("UTF8", UTF8);
		aliases.put("UTF16BE", UTF16BE);
		aliases.put("UTF16LE", UTF16LE);
		aliases.put("UTF32BE", UTF32BE);
		aliases.put("UTF32LE", UTF32LE);
		aliases.put("UNICODEBIG", UTF16BE);
		aliases.put("UNICODELITTLE", UTF16LE);
		aliases.put("UNICODEBIGUNMARKED", UTF16BE);
		aliases.put("UNICODELITTLEUNMARKED", UTF16LE);
	}
	private static HashMap<String, ByteOrderMark> getAliases()
	{
		return aliases;
	}
	
	/**
	 * Resolve a BOM for the given encoding name.
	 * 
	 * @param encoding The name of a character encoding.
	 * 
	 * @return A BOM for the given encoding name or null.
	 */
	public static ByteOrderMark resolve(String encoding)
	{
		ByteOrderMark resolve = null;
		if ( encoding != null )
		{
			String alias = encoding.toUpperCase()
				.replace("-", "")
				.replace("_", "");
			if ( getAliases().containsKey(alias) )
				resolve = ( getAliases().get(alias));
		}
		return resolve;
	}
	
	/**
	 * Resolve a BOM for the given byte signature.
	 * 
	 * @param bytes The byte signature of a character encoding.
	 * @param len The length of the signature in bytes.
	 * 
	 * @return A BOM for the given character encoding or null.
	 */
	public static ByteOrderMark resolve(byte[] bytes, int len)
	{
		ByteOrderMark resolve = null;
		if ( bytes != null )
		{
			if ( match(bytes, len, UTF32BE))
				resolve = UTF32BE;
			else if ( match(bytes, len, UTF32LE))
				resolve = UTF32LE;
			else if ( match(bytes, len, UTF8))
				resolve = UTF8;
			else if ( match(bytes, len, UTF16BE))
				resolve = UTF16BE;
			else if ( match(bytes, len, UTF16LE))
				resolve = UTF16LE;
		}
		return resolve;
	}

	/**
	 * Literal representation of the Byte Order Mark character.
	 * @return Byte Order Mark character.
	 */
	public static char BOM()
	{
		return '\uFEFF';
	}
	
	private static boolean match(byte[] bytes, int bytesLen, ByteOrderMark bom)
	{
		boolean match = false;
		byte[] bomBytes = bom.getBytes();
		if ( bytesLen >= bomBytes.length )
		{
			match = true;
			for (int bomIndex=0; match && (bomIndex < bomBytes.length); ++bomIndex)
				match = (bytes[bomIndex] == bomBytes[bomIndex]);
		}
		return match;
	}
	
	private String encoding;
	/**
	 * Get the encoding name.
	 * @return The encoding name. 
	 */
	public String getEncoding() { return encoding; }
	private void setEncoding(String encoding) { this.encoding = encoding; }
	
	private byte[] bytes;
	/**
	 * Get signature bytes.
	 * @return The signature bytes.
	 */
	public byte[] getBytes() { return bytes; }
	private void setBytes(byte[] bytes)	{ this.bytes = bytes; }
	
	private boolean recommended;
	/**
	 * Is BOM recommended for this encoding.
	 * @return True, when a BOM should be used as a signature.
	 */
	public boolean isRecommended()
	{
		return recommended;
	}
	private void setRecommended(boolean recommended)
	{
		this.recommended = recommended;
	}

	private ByteOrderMark(String encoding, byte[] bytes, boolean recommended)
	{
		setEncoding(encoding);
		setBytes(bytes);
		setRecommended(recommended);
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
