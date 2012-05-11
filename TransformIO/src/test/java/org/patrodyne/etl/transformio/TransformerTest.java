// PatroDyne: Patron Supported Dynamic Executables, http://patrodyne.org
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio;

import static org.junit.Assert.*;

import org.junit.Test;
import org.patrodyne.etl.transformio.xml.Batch;
import org.patrodyne.etl.transformio.xml.Record;
import org.patrodyne.etl.transformio.xml.Record.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit tests for Transformer.
 * 
 * @author Rick O'Sullivan
 */
public class TransformerTest
{
	/** Represents a SLF4J logger interface. */
	private static Logger log = LoggerFactory.getLogger(TransformerTest.class);
	
	/** Represents a sample batch for a new transformation. */
	protected static final String NEW_BATCH =
		"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"+
		"<batch xmlns=\"http://patrodyne.org/etl/TransformIO\">\n"+
		"\t<source charset=\"UTF-8\" bufferSize=\"256\">\n"+
		"\t\t<locator url=\"file:input.txt\"/>\n"+
		"\t\t<record>\n"+
		"\t\t\t<field name=\"F01\" get=\"(.*)\\n\" set=\"$1\"/>\n"+
		"\t\t</record>\n"+
		"\t</source>\n"+
		"\t<target charset=\"UTF-8\" byteOrderMark=\"false\">\n"+
		"\t\t<locator url=\"file:output.txt\"/>\n"+
		"\t\t<record>\n"+
		"\t\t\t<field name=\"F01\" get=\"(.*)\" set=\"$1\\n\"/>\n"+
		"\t\t</record>\n"+
		"\t</target>\n"+
		"\t<transform>\n"+
		"\t\t<script engine=\"JavaScript\">\n"+
		"main();\n"+
		"function main()\n"+
		"{\n"+
		"\ttarget.put('F01',source.get('F01'));\n"+
		"\treturn target;\n"+
		"}\n"+
		"\t\t</script>\n"+
		"\t</transform>\n"+
		"</batch>\n"+
		"<!-- vi:set tabstop=4 hardtabs=4 shiftwidth=4: -->\n";
	
	/**
	 * Test method for {@link org.patrodyne.etl.transformio.Transformer#marshalNewBatch()}.
	 */
	@Test
	public void testMarshalNewBatch()
	{
		String marshalledBatch = Transformer.marshalNewBatch();
		assertNotNull(marshalledBatch);
		assertEquals(NEW_BATCH, marshalledBatch);
	}
	
	/**
	 * Test method for {@link org.patrodyne.etl.transformio.Transformer#unmarshalBatch(String)}.
	 */
	@Test
	public void testUnmarshalBatchString()
	{
		try
		{
			Batch batch1 = Transformer.newBatch();
			String batch1xml = Transformer.marshal(batch1);
			Batch batch2 = Transformer.unmarshalBatch(batch1xml);
			assertNotNull(batch2);
			assertEquals(batch1, batch2);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.toString());
		}
	}

	/**
	 * Test method for {@link org.patrodyne.etl.transformio.Transformer#transform(Batch, String)
	 */
	@Test
	public void testTransformBatchString()
	{
		Transformer transformer = new Transformer()
		{
			@Override
			protected Logger log() { return log; }
		};
		Batch batch = Transformer.newBatch();
		try
		{
			log.debug("\n\n"+Transformer.marshal(batch));
			// Empty source, empty target
			assertEquals("", transformer.transform(batch, ""));

			// Example: VCard Data Fields:
			// XGroup|NSurname|NGiven|NAdditional|NPrefix|NSuffix|TelValue|TelType\n
			String source = 
				"Friends|Smith|John||Mr|Jr|311-555-1010|WORK;VOICE\n"+
				"Friends|Brown|Jane||Ms|II|311-555-1011|HOME;VOICE\n"+
				"Friends|Green|Mary||||311-555-1012|HOME;CELL\n";
			// Transform #1
			assertEquals(source, transformer.transform(batch, source));
			
			// Parse piped delimited source records...
			Record sourceRecord = new Record();
			sourceRecord.getFields().add(new XField("XGroup",      "(.*)\\|", "$1"));
			sourceRecord.getFields().add(new XField("NSurname",    "(.*)\\|", "$1"));
			sourceRecord.getFields().add(new XField("NGiven",      "(.*)\\|", "$1"));
			sourceRecord.getFields().add(new XField("NAdditional", "(.*)\\|", "$1"));
			sourceRecord.getFields().add(new XField("NPrefix",     "(.*)\\|", "$1"));
			sourceRecord.getFields().add(new XField("NSuffix",     "(.*)\\|", "$1"));
			sourceRecord.getFields().add(new XField("TelValue",    "(.*)\\|", "$1"));
			sourceRecord.getFields().add(new XField("TelType",     "(.*)\n", "$1"));
			batch.getSource().setRecord(sourceRecord );
			// ... into tab delimited target records.
			Record targetRecord = new Record();
			targetRecord.getFields().add(new XField("XGroup",      "(.*)", "$1\t"));
			targetRecord.getFields().add(new XField("NSurname",    "(.*)", "$1\t"));
			targetRecord.getFields().add(new XField("NGiven",      "(.*)", "$1\t"));
			targetRecord.getFields().add(new XField("NAdditional", "(.*)", "$1\t"));
			targetRecord.getFields().add(new XField("NPrefix",     "(.*)", "$1\t"));
			targetRecord.getFields().add(new XField("NSuffix",     "(.*)", "$1\t"));
			targetRecord.getFields().add(new XField("TelValue",    "(.*)", "$1\t"));
			targetRecord.getFields().add(new XField("TelType",     "(.*)", "$1\n"));
			batch.getTarget().setRecord(targetRecord );
			// Transform #2
			assertEquals(source.replace('|', '\t'), transformer.transform(batch, source));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.toString());
		}
	}

	private class XField extends Field
	{
		protected XField(String name, String get, String set)
		{
			setName(name);
			setGet(get);
			setSet(set);
		}
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
