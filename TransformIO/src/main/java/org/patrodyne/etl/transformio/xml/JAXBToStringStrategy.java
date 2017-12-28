// PatroDyne: Patron Supported Dynamic Executables, http://patrodyne.org
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio.xml;

import org.jvnet.jaxb2_commons.lang.ToStringStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;

/**
 * Customize the strategy that JAXB uses to generate string representations
 * of objects. In particular, simplify the Record.Field.toString() method to
 * use only the 'name' field for use in user-friendly drop down lists.
 * 
 * Excludes the class name.
 * 
 * @author Rick O'Sullivan
 */
public class JAXBToStringStrategy
	extends org.jvnet.jaxb2_commons.lang.JAXBToStringStrategy
{
	/** Whether to use the class name, the default is <code>true</code>. */
	private boolean useClassName = false;

	/** Whether to use short class names, the default is <code>false</code>. */
	private boolean useShortClassName = false;

	/**
	 * <p>Append to the <code>toString</code> the class name.</p>
	 * 
	 * @param buffer The <code>StringBuilder</code> to populate
	 * @param object The <code>Object</code> whose name to output
	 */
	protected void appendClassName(StringBuilder buffer, Object object)
	{
		if (useClassName && object != null)
		{
			if (useShortClassName)
				buffer.append(getShortClassName(object.getClass()));
			else 
				buffer.append(object.getClass().getName());
		}
	}

	/**
	 * Customization to not append a prefix for Record.Field, etc.
	 * @see org.jvnet.jaxb2_commons.lang.DefaultToStringStrategy#appendStart(org.jvnet.jaxb2_commons.locator.ObjectLocator, java.lang.Object, java.lang.StringBuilder)
	 */
	@Override
	public StringBuilder appendStart(ObjectLocator parentLocator, Object object, 
		StringBuilder buffer)
	{
		return ( object instanceof Record.Field || object instanceof Locator ) ?  buffer : 
			super.appendStart(parentLocator, object, buffer);
	}
	
	/**
	 * Customization to not append a suffix for Record.Field, etc.
	 * @see org.jvnet.jaxb2_commons.lang.DefaultToStringStrategy#appendEnd(org.jvnet.jaxb2_commons.locator.ObjectLocator, java.lang.Object, java.lang.StringBuilder)
	 */
	@Override
	public StringBuilder appendEnd(ObjectLocator parentLocator, Object object,
		StringBuilder buffer)
	{
		return ( object instanceof Record.Field || object instanceof Locator ) ?  buffer : 
			super.appendEnd(parentLocator, object, buffer);
	}

	/**
	 * Custom logic to simplify the Record.Field.toString() method to only show
	 * the 'name' field value by appending the value without a field name or separator, etc.
	 * 
	 * @see org.jvnet.jaxb2_commons.lang.DefaultToStringStrategy#appendField(org.jvnet.jaxb2_commons.locator.ObjectLocator, java.lang.Object, java.lang.String, java.lang.StringBuilder, java.lang.Object)
	 */
	@Override
	public StringBuilder appendField(ObjectLocator parentLocator, Object parent, String fieldName,
		StringBuilder buffer, Object value)
	{
		if ( parent instanceof Record.Field )
		{
			if ( "name".equals(fieldName) )
				buffer.append(value);
			return buffer;
		}
		else if ( parent instanceof Locator )
		{
			if ( "url".equals(fieldName) )
				buffer.append(value);
			return buffer;
		}
		else
			return super.appendField(parentLocator, parent, fieldName, buffer, value);
	}

	public static final ToStringStrategy INSTANCE = new JAXBToStringStrategy();
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
