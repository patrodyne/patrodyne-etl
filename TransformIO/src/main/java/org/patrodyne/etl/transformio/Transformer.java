// PatroDyne: Patron Supported Dynamic Executables
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.patrodyne.etl.transformio.cli.CommandLI;
import org.patrodyne.etl.transformio.gui.GraphicalUI;
import org.patrodyne.etl.transformio.io.ByteOrderMark;
import org.patrodyne.etl.transformio.io.UnicodeReader;
import org.patrodyne.etl.transformio.tui.TextualUI;
import org.patrodyne.etl.transformio.xml.Batch;
import org.patrodyne.etl.transformio.xml.Batch.Source;
import org.patrodyne.etl.transformio.xml.Batch.Target;
import org.patrodyne.etl.transformio.xml.Batch.Transform;
import org.patrodyne.etl.transformio.xml.Batch.Transform.Script;
import org.patrodyne.etl.transformio.xml.Locator;
import org.patrodyne.etl.transformio.xml.Record;
import org.patrodyne.etl.transformio.xml.Record.Field;
import org.slf4j.Logger;
import org.xml.sax.SAXException;

/**
 * <p>An abstract class for core transformation methods.</p>
 * 
 * <p>This is the transformation engine used by all user interface modes.
 * It contains methods for <em>Java Scripting</em> for transformations,
 * unmarshalling/marshalling/executing {@link Batch} objects, opening/saving
 * files, field pattern matching, notifications and extending the class path.
 * </p>
 * 
 * @author Rick O'Sullivan
 */
abstract public class Transformer
{
	// Represents the program name 
	protected static final String PROGRAM_NAME = System.getProperty("program.name", Transformer.class.getName());
	
	/** Represents font points per inch. */
	protected static final int POINTS_PER_INCH = 72;
	/** Represents the default tab size for editor display.*/
	protected static final int DEFAULT_TAB_SIZE = 4;
	/** Represents the input/output buffer size. */
	protected static final int IO_BUFFER_SIZE = 8192;
	/** Represents the user's home directory. */
	protected File userHomeDir = new File(System.getProperty("user.home"));
	/* Represents the Java Scripting Engine directory. */
	private static final String ENGINE_DIR = "lib/engine";
	// End Of Stream character.
	private static final char EOS = (char) -1;
	private static final String DEFAULT_SCRIPT_ENGINE = "JavaScript";
	/** Represents this project's home page. */
	protected static final String LINK_HOME = "http://patrodyne.org/sites/etl-TransformIO";
	/** Represents this project's donation page. */
	protected static final String LINK_DONATE = "http://flattr.com/thing/657653/TransformIO";

	/**
	 * An abstract logger to serve as a place holder for log messages.
	 * It is override by the concrete subclass for greater control.
	 *
	 * @return the logger
	 */
	abstract protected Logger log();
	
	// Represents the active {@link Batch} file.
	private File currentBatchFile = null;
	/**
	 * Get the active {@link Batch} file.
	 * @return The current batch file.
	 */
	protected File getCurrentBatchFile()
	{
		return currentBatchFile;
	}
	/**
	 * Set the active {@link Batch} file.
	 * @param currentBatchFile The current batch file.
	 */
	protected void setCurrentBatchFile(File currentBatchFile)
	{
		this.currentBatchFile = currentBatchFile;
		if ( currentBatchFile != null )
			setCurrentWorkingDirectory(currentBatchFile);
	}

	// Represents the active {@link Batch} hash.
	private int currentBatchHash;
	/**
	 * Get the current batch hash code.
	 * @return The current batch hash code.
	 */
	protected int getCurrentBatchHash()
	{
		return currentBatchHash;
	}
	/**
	 * Set current batch hash code.
	 * @param currentBatchHash The current batch hash code.
	 */
	protected void setCurrentBatchHash(int currentBatchHash)
	{
		this.currentBatchHash = currentBatchHash;
	}

	// Represents the active {@link Batch.Source} hash.
	private int currentSourceHash;
	/**
	 * Get the current source hash code.
	 * @return The current source hash code.
	 */
	protected int getCurrentSourceHash()
	{
		return currentSourceHash;
	}
	/**
	 * Set current source hash code.
	 * @param currentSourceHash The current source hash code.
	 */
	protected void setCurrentSourceHash(int currentSourceHash)
	{
		this.currentSourceHash = currentSourceHash;
	}

	// Represents the Java Scripting Engine Manager
	private ScriptEngineManager scriptEngineManager = null;
	/*
	 * <p>Get the Java Scripting Engine Manager.</p>
	 * 
	 * <p>On initial access, a {@link ScriptEngineManager} is
	 * created with a classloader for libraries in {@link #ENGINE_DIR}.</p>
	 * 
	 * <p>If a {@link ScriptEngineManager} cannot be created using {@link #ENGINE_DIR}
	 * then a {@link ScriptEngineManager} is created using the default classpath and
	 * a warning is logged.</p>
	 * 
	 * @return The Java Scripting Engine Manager.
	 */
	private ScriptEngineManager getScriptEngineManager()
	{
		if ( scriptEngineManager == null )
		{
			try
			{
				ClassLoader newLoader = new URLClassLoader(scriptEngineLocators());
				setScriptEngineManager(new ScriptEngineManager(newLoader));
			}
			catch ( MalformedURLException mue )
			{
				log().warn(mue.getMessage());
				setScriptEngineManager(new ScriptEngineManager());
			}
		}
		return scriptEngineManager;
	}
	/*
	 * Set a Java Scripting Engine Manager.
	 * @param scriptEngineManager The Java Scripting Engine Manager.
	 */
	private void setScriptEngineManager(ScriptEngineManager scriptEngineManager)
	{
		this.scriptEngineManager = scriptEngineManager;
	}

	@SuppressWarnings("unused")
	private static URL[] buildClassPath(String[] directories) throws MalformedURLException
	{
		final List<URL> classPath = new ArrayList<URL>();
		for (String directory : directories)
		{
			for ( URL url : buildClassPath(directory) )
				classPath.add(url);
		}
		return classPath.toArray(new URL[classPath.size()]);
	}

	private static URL[] scriptEngineLocators() throws MalformedURLException
	{
		return buildClassPath(ENGINE_DIR);
	}
	
	// Return an array of URLs for the JARs found in the specified directory.
	private static URL[] buildClassPath(String directory) throws MalformedURLException
	{
		final List<URL> classPath = new ArrayList<URL>();
		File[] pathnames = new File(directory).listFiles();
		if ( pathnames != null )
		{
			for (File pathname : pathnames)
			{
				if (pathname.isFile() && pathname.toString().toLowerCase().endsWith(".jar"))
				{
					URL url = pathname.toURI().toURL();
					classPath.add(url);
				}
			}
		}
		return classPath.toArray(new URL[classPath.size()]);
	}
	
	private static Marshaller batchMarshaller;
	/**
	 * <p>On initial use, creates a {@link Batch} marshaller.</p> 
	 * 
	 * @return A marshaller to convert a {@link Batch} instance into an XML representation.
	 * 
	 * @throws JAXBException when the unmarshaller cannot be created.
	 */
	protected static Marshaller getBatchMarshaller() throws JAXBException
	{
		if ( batchMarshaller == null )
		{
			batchMarshaller = JAXBContext
			.newInstance(Batch.class)
			.createMarshaller();
			batchMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		}
		return batchMarshaller;
	}
	
	private static Unmarshaller batchUnmarshaller;
	/**
	 * <p>On initial use, creates a {@link Batch} unmarshaller.</p> 
	 * <p>
	 * Creates an unmarshaller from a {@link JAXBContext} for the {@link Batch}
	 * class the sets the event handler to handle each {@link ValidationEvent}.
	 * Events emit {@link #notification(MessageType, String)} messages.
	 * </p>
	 * 
	 * @return An unmarshaller to convert an XML representation into a {@link Batch} instance.
	 * 
	 * @throws JAXBException when the unmarshaller cannot be created.
	 * @throws SAXException when the XML Schema cannot be accessed or found. 
	 */
	protected static Unmarshaller getBatchUnmarshaller() throws JAXBException, SAXException
	{
		if ( batchUnmarshaller == null )
		{
			batchUnmarshaller = JAXBContext
				.newInstance(Batch.class)
				.createUnmarshaller();
			batchUnmarshaller.setSchema(getSchema());
			batchUnmarshaller.setEventHandler
			(
				new ValidationEventHandler()
				{
					// allow unmarshalling to continue even if there are errors
					public boolean handleEvent(ValidationEvent ve)
					{
						ValidationEventLocator vel = ve.getLocator();
						String invalid = "[line=" + vel.getLineNumber() + 
						", col=" + vel.getColumnNumber() + "]; " + ve.getMessage();
						if (ve.getSeverity() == ValidationEvent.WARNING)
						{
							notification(MessageType.WARN, invalid);
							return true;
						}
						else if (ve.getSeverity() == ValidationEvent.ERROR)
						{
							notification(MessageType.ERROR, invalid);
							return true;
						}
						else if (ve.getSeverity() == ValidationEvent.FATAL_ERROR)
						{
							notification(MessageType.FATAL, invalid);
						}
						return false;
					}
				}
			);
		}
		return batchUnmarshaller;
	}
	
	/**
	 * <p>Marshal a {@link Batch} instance into an XML string.</p>
	 * 
	 * @param batch The instance object to marshal.
	 * 
	 * @return A string containing an XML representation of the batch instance.
	 * 
	 * @throws JAXBException When the batch object cannot be marshalled.
	 */
	protected static String marshal(Batch batch) throws JAXBException
	{
		StringWriter writer = new StringWriter();
		getBatchMarshaller().marshal(batch, writer);
		return writer.toString().replaceAll("    ", "\t")+
			"<!-- vi:set tabstop=4 hardtabs=4 shiftwidth=4: -->\n";
	}
	
	/**
	 * <p>Converts a text representation of a batch into a {@link Batch} object.<p>
	 * <p>Emits a notification when the text is empty.</p>
	 * 
	 * @param batchText A string containing an XML representation of a {@link Batch}.
	 * 
	 * @return A {@link Batch} instance.
	 * 
	 * @see Batch
	 */
	protected static Batch unmarshalBatch(String batchText)
	{
		Batch batch = null;
		if ( !isEmpty(batchText) )
			batch = unmarshalBatch(new StringReader(batchText));
		else
			notification(MessageType.WARN, "Batch is empty.");
		return batch;
	}
	
	/**
	 * <p>Reads a batch stream and instantiates a {@link Batch} object.</p>
	 * <p>Assumes the validator will emit a notification for issues.</p>
	 * 
	 * @param batchReader A reader for a batch stream.
	 * 
	 * @return A {@link Batch} instance.
	 */
	protected static Batch unmarshalBatch(Reader batchReader)
	{
		Batch batch = null;
		try
		{
			batch = (Batch) getBatchUnmarshaller().unmarshal(batchReader);
		}
		catch (Exception ex)
		{
			// Validator will emit the notification.
		}
		return batch;
	}

	/**
	 * Create a new batch with a simple source, target and transform.
	 * 
	 * @return a new batch with a simple source, target and transform.
	 */
	protected static Batch newBatch()
	{
		Batch batch = new Batch();

		Source source = new Source();
		source.setCharset("UTF-8");
		source.setBufferSize(new BigInteger("256"));
		Locator sourceLocator = new Locator();
		sourceLocator.setUrl("file:input.txt");
		source.setLocator(sourceLocator);
		Record sourceRecord = new Record();
		sourceRecord.setExclude("\\s*\\n");
		Field sourceField01 = new Field();
		sourceField01.setName("F01");
		sourceField01.setGet("(.*)\\n");
		sourceField01.setSet("$1");
		sourceRecord.getFields().add(sourceField01);
		source.setRecord(sourceRecord);

		Target target = new Target();
		target.setCharset("UTF-8");
		target.setByteOrderMark(false);
		Locator targetLocator = new Locator();
		targetLocator.setUrl("file:output.txt");
		target.setLocator(targetLocator);
		Record targetRecord = new Record();
		Field targetField01 = new Field();
		targetField01.setName("F01");
		targetField01.setGet("(.*)");
		targetField01.setSet("$1\\n");
		targetRecord.getFields().add(targetField01);
		target.setRecord(targetRecord);

		Transform transform = new Transform();
		Script script = new Script();
		script.setEngine("JavaScript");
		script.setValue("\nmain();\nfunction main()\n{\n\ttarget.put('F01',source.get('F01'));\n\treturn target;\n}\n\t\t");
		transform.setScript(script );
		
		batch.setSource(source);
		batch.setTarget(target);
		batch.setTransform(transform);
		
		return batch;
	}
	
	/**
	 * Marshal a new batch with a simple source, target and transform.
	 * 
	 * @return a new batch with a simple source, target and transform.
	 */
	protected static String marshalNewBatch()
	{
		try
		{
			return marshal(newBatch());
		}
		catch (JAXBException e)
		{
			return "";
		}
	}

	/**
	 * <p>Transform a source string into a target string and return the target.</p>
	 * <p>This is a convenience method for {@link #transform(Batch, Reader, Writer)}
	 * when the transformation is performed in memory.</p> 
	 * 
	 * @param batch The batch configuration.
	 * @param source The source to extract.
	 * 
	 * @return The target to load.
	 * 
	 * @throws IOException from {@link #transform(Batch, Reader, Writer)}
	 * @throws ScriptException from {@link #transform(Batch, Reader, Writer)}
	 */
	public String transform(Batch batch, String source) throws IOException, ScriptException
	{
		Reader reader = new StringReader(source);
		Writer writer = new StringWriter(source.length());
		transform(batch, reader, writer, true);
		return writer.toString();
	}

	/**
	 * <p>
	 * Transform a source {@link Reader} into a target {@link Writer} for the
	 * given {@link Batch}.
	 * </p>
	 * 
	 * <p>
	 * A transformation script is optional. When a script is defined, a script
	 * engine is created or retrieved for the given engine name (i.e.
	 * JavaScript) and bindings are created at the
	 * {@link ScriptContext#ENGINE_SCOPE} level. If the {@link ScriptEngine} is
	 * {@link Compilable} then a {@link CompiledScript} is produced.
	 * </p>
	 * 
	 * <p>
	 * The source is read and matched using the get patterns of its
	 * {@link Field} specifications. For each match, the set pattern is checked
	 * for {@link #literal(String)} substitutions before parsing the source
	 * field into its set format. The result is the field value prior to the
	 * transform script or target processing.
	 * </p>
	 * 
	 * <p>
	 * When there is a transform script, the source fields are bound to a script
	 * variable named <tt>source</tt> and the empty target fields are bound to a
	 * script variable named <tt>target</tt>. In the script, the <tt>source</tt>
	 * and <tt>target</tt> variables are maps where the key is the field name
	 * and the value is the field value. The script is evaluated. If the result
	 * is a {@link Map} then it is expected to be the target fields; otherwise,
	 * the result is retrieved from the bindings for the variable named
	 * <tt>target</tt>.
	 * </p>
	 * 
	 * <p>
	 * In other words, the script has access to two maps: <tt>source</tt> and
	 * <tt>target</tt>. The script populates the target map and, if the language
	 * allows, it should return the <tt>target</tt> map. If the language does
	 * not support returning the a map then the results are retrieved from the
	 * bindings for the variable named <tt>target</tt>.
	 * </p>
	 * 
	 * @param batch The batch configuration.
	 * @param reader To extract the source.
	 * @param writer To load the target.
	 * @param debug When true, increases the level for log messages.
	 * 
	 * @throws IOException when the source cannot be read or the target cannot be written.
	 * @throws ScriptException when the transform script cannot be compiled or evaluated.
	 */
	public void transform(Batch batch, Reader reader, Writer writer, boolean debug) 
		throws IOException, ScriptException
	{
		log().info("Transform: START");
		ScriptEngine scriptEngine = null;
		CompiledScript compiledScript = null;
		Bindings bindings = null;
		if ( batch.getTransform() != null )
		{
			if ( batch.getTransform().getScript() != null )
			{
				// XSD specifies the default engine name, "JavaScript".
				String engineName = batch.getTransform().getScript().getEngine();
				if ( engineName == null || engineName.isEmpty() )
					engineName = DEFAULT_SCRIPT_ENGINE;
		        scriptEngine = getScriptEngineManager().getEngineByName(engineName);
		        if ( scriptEngine != null )
		        {
					bindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
					if ( bindings == null )
					{
						bindings = scriptEngine.createBindings();
						scriptEngine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
					}
			        if ( scriptEngine instanceof Compilable)
			        {
						String script = batch.getTransform().getScript().getValue();
						if ( !isEmpty(script) )
						{
				            Compilable compilableScriptEngine = (Compilable) scriptEngine;
				            try
				            {
				            	compiledScript = compilableScriptEngine.compile(script);
					        	log().info("Script Execution Mode: COMPILED");
				            }
				            catch ( Error e)
				            {
					        	log().info("Script Execution Mode: INTERPRETED");
				            }
						}
			        }
			        else
			        	log().info("Script Execution Mode: INTERPRETED");
		        }
		        else
		        {
		        	notification(MessageType.ERROR, "Unknown script engine: "+engineName);
		        }
			}
		}
		int sourceBufferSize = batch.getSource().getBufferSize().intValue();
		String sourceExcludeSpec = batch.getSource().getRecord().getExclude();
		List<Field> sourceFieldSpecs = batch.getSource().getRecord().getFields();
		List<Field> targetFieldSpecs = batch.getTarget().getRecord().getFields();
		Map<String,Integer> sourceShuffle = new HashMap<String,Integer>();
		for( int index=0; index < sourceFieldSpecs.size(); ++index)
		{
			Field targetFieldSpec = sourceFieldSpecs.get(index);
			sourceShuffle.put(targetFieldSpec.getName(), index);
		}
		Map<String,Integer> targetShuffle = new HashMap<String,Integer>();
		for( int index=0; index < targetFieldSpecs.size(); ++index)
		{
			Field targetFieldSpec = targetFieldSpecs.get(index);
			targetShuffle.put(targetFieldSpec.getName(), index);
		}
		int sourceSize = sourceFieldSpecs.size();
		int targetSize = targetFieldSpecs.size();
		Field sourceFieldSpec = null;
		String[] sourceFields = new String[sourceSize];
		String[] targetFields = new String[targetSize];
		StringBuilder recordBuffer = new StringBuilder();
		StringBuilder fieldBuffer = new StringBuilder();
		Map<String, Object> common = new HashMap<String, Object>();
		int sourceIndex = 0;
		int errors = 0;
		int recno = 0;
		boolean bufferOverflow = false;
		boolean nextRecord = true;
		boolean omitRecord = false;
		char ch;
		long t1 = System.currentTimeMillis();
		while ( ((ch = (char) reader.read()) != EOS) && !bufferOverflow)
		{
			fieldBuffer.append(ch);
			if ( sourceExcludeSpec != null )
			{
				if ( nextRecord )
					recordBuffer = new StringBuilder(sourceBufferSize);
				recordBuffer.append(ch);
				if ( matches(sourceExcludeSpec, recordBuffer.toString()) )
				{
					fieldBuffer = new StringBuilder(sourceBufferSize);
					nextRecord = true;
					log().trace("Record: "+recno+" excluded.");
					continue;
				}
			}
			if ( nextRecord )
			{
				nextRecord = false;
				omitRecord = false;
				++recno;
				if ( debug ) 
					log().debug("Record: "+recno);
			}
			sourceFieldSpec = sourceFieldSpecs.get(sourceIndex);
			String sourceFieldSpecGet = sourceFieldSpec.getGet();
			String sourceFieldGet = fieldBuffer.toString();
			if ( matches(sourceFieldSpecGet, sourceFieldGet) )
			{
				String sourceFieldSpecSet = literal(sourceFieldSpec.getSet());
				String sourceFieldSet = replaceFirst(sourceFieldGet, sourceFieldSpecGet, sourceFieldSpecSet);
				sourceFields[sourceIndex] = sourceFieldSet;
				// Shuffle source into target
				if ( targetShuffle.containsKey(sourceFieldSpec.getName()) )
				{
					int targetIndex = targetShuffle.get(sourceFieldSpec.getName());
					targetFields[targetIndex] = sourceFieldSet;
				}
				fieldBuffer = new StringBuilder(sourceBufferSize);
				// Check for enough source fields to compose the current record.
				sourceIndex = ++sourceIndex % sourceSize;
				if ( sourceIndex == 0 )
				{
					// Optional Script Transformation
					if ( scriptEngine != null )
					{
						bindings.put("common", common);
						bindings.put("source", map(sourceFieldSpecs, sourceFields));
						bindings.put("target", map(targetFieldSpecs, targetFields));
						Object result = null;
						if ( compiledScript != null )
							result = compiledScript.eval(bindings);
						else
						{
							String script = batch.getTransform().getScript().getValue();
							if ( !isEmpty(script) )
								result = scriptEngine.eval(script, bindings);
						}
						@SuppressWarnings("unchecked")
						Map<String, Object> castCommon = (Map<String, Object>) bindings.get("common");
						common = castCommon;
						if ( !(result instanceof Map) )
							result = bindings.get("target");
						if ( result != null )
						{
							if (result instanceof Map)
							{
								try
								{
									@SuppressWarnings("unchecked")
									Map<String,String> resultMap = (Map<String,String>) result;
									if ( resultMap.size() > 0 )
									{
										for ( int index=0; index < targetFields.length; ++index)
										{
											String name = targetFieldSpecs.get(index).getName();
											if ( resultMap.containsKey(name) )
												targetFields[index] = resultMap.get(name);
										}
									}
									else
										omitRecord = true;
								}
								catch ( ClassCastException cce )
								{
									++errors;
									log().error(message(cce));
								}
							}
							else
							{
								++errors;
								log().error("Script does not return a map type for target. Type returned: "+result.getClass());
							}
						}
						else
						{
							++errors;
							log().error("Script does not return a result");
						}
					}
					if ( omitRecord )
						log().trace("Record: "+recno+" omitted.");
					else
					{
						// Target Transformation and Write
						for ( int targetIndex=0; targetIndex < targetFields.length; ++targetIndex)
						{
							String targetField = targetFields[targetIndex];
							Field targetFieldSpec = targetFieldSpecs.get(targetIndex);
							String targetFieldSpecGet = targetFieldSpec.getGet();
							String targetFieldSpecSet = literal(targetFieldSpec.getSet());
							String targetFieldSet = replaceFirst(targetField, targetFieldSpecGet, targetFieldSpecSet);
							writer.write(targetFieldSet);
							if ( debug && log().isTraceEnabled() )
							{
								Integer sourceIndexTmp = sourceShuffle.get(targetFieldSpec.getName());
								if ( sourceIndexTmp != null )
								{
									log().trace((sourceIndexTmp+1)+" => "+(targetIndex+1)+": "+
										targetFieldSpec.getName()+" = "+escape(targetFieldSet));
								}
								else
								{
									log().trace("? => "+(targetIndex+1)+": "+
										targetFieldSpec.getName()+" = "+escape(targetFieldSet));
								}
							}
						}
					}
					sourceFields = new String[sourceSize];
					targetFields = new String[targetSize];
					nextRecord = true;
				}
			}
			if ( fieldBuffer.length() >= sourceBufferSize )
				bufferOverflow = true;
		}
		if ( !nextRecord || bufferOverflow)
		{
			++errors;
			String msg = bufferOverflow ? "Buffer Overflow, " : "Incomplete ";
			String fieldName = sourceFieldSpecs.get(sourceIndex).getName();
			log().error(msg+"Record #"+recno+", Field #"+(sourceIndex+1)+" ("+fieldName+")");
		}
		long t2 = System.currentTimeMillis();
		if ( errors == 0 )
		{
			double duration = t2 - t1;
			double count = recno;
			double ms = (count > 0.0) ? duration / count : duration;
			log().info(String.format("Transform: SUCCESS (%d, %.3f ms)", recno, ms));
		}
		else
			notification(MessageType.ERROR, "Batch error count: "+errors);
	}

	private Map<String,String> map(List<Field> sourceFieldSpecs, String[] sourceFields)
	{
		Map<String,String> map = new TreeMap<String, String>();
		for (int index=0; index < sourceFields.length; ++index)
			map.put(sourceFieldSpecs.get(index).getName(), sourceFields[index]);
		return map;
	}

	private static Map<String,Pattern> patterns = new HashMap<String, Pattern>();
	private static Pattern pattern(String regex)
	{
		Pattern pattern = null;
		if ( patterns.containsKey(regex) )
			pattern = patterns.get(regex);
		else
			patterns.put(regex, pattern = Pattern.compile(regex));
		return pattern;
	}

	private static Matcher matcher(String regex, String text)
	{
		return pattern(regex).matcher(text);
	}

	private static boolean matches(String regex, String text)
	{
		if ( text != null )
			return matcher(regex, text).matches();
		else
			return (regex == null);
	}

	private static String replaceFirst(String text, String regex, String replacement)
	{
		try
		{
	    	return matcher(regex, text != null ? text : "").replaceFirst(replacement);
		}
		catch (Exception ex)
		{
			return text;
		}
	}

	private static Schema schema;
	private static Schema getSchema()
		throws SAXException
	{
		if ( schema == null )
		{
			SchemaFactory factory = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
			InputStream inputStream = Transformer.class.getResourceAsStream("/TransformIO.xsd");
			StreamSource ss = new StreamSource(inputStream);
			schema = factory.newSchema(ss);
		}
		return schema;
	}

	private static String literal(String str)
	{
		if ( str != null )
			return str
				.replace("\\b", "\b") // blank
				.replace("\\f", "\f") // form-feed
				.replace("\\n", "\n") // new-line
				.replace("\\r", "\r") // carriage-return
				.replace("\\t", "\t") // tab
			;
		else
			return null;
	}

	private static String escape(String str)
	{
		if ( str != null )
			return str
				.replace("\b", "\\b") // blank
				.replace("\f", "\\f") // form-feed
				.replace("\n", "\\n") // new-line
				.replace("\r", "\\r") // carriage-return
				.replace("\t", "\\t") // tab
			;
		else
			return null;
	}

	/** Represent a window size in character columns and rows. */
	protected class WindowSize
	{
		private int columns;
		/**
		 * Gets the columns.
		 *
		 * @return the columns
		 */
		public int getColumns()	{ return columns; }
		/**
		 * Sets the columns.
		 *
		 * @param columns the new columns
		 */
		public void setColumns(int columns)	{ this.columns = columns; }
		
		private int rows;
		/**
		 * Gets the rows.
		 *
		 * @return the rows
		 */
		public int getRows() { return rows;	}
		/**
		 * Sets the rows.
		 *
		 * @param rows the new rows
		 */
		public void setRows(int rows) {	this.rows = rows; }
		
		/**
		 * Instantiates a new window size.
		 *
		 * @param columns the columns
		 * @param rows the rows
		 */
		public WindowSize(int columns, int rows)
		{
			super();
			setColumns(columns);
			setRows(rows);
		}
	}

	/**
	 * Open (load) a URL for reading and read text into a string.
	 * 
	 * @param locator Represents a Uniform Resource Locator.
	 * @return A string containing the text from the URL.
	 * 
	 * @throws IOException When the locator cannot be opened for reading.
	 * @throws MalformedURLException When a URL cannot be composed.
	 */
	protected String openURL(Locator locator)
		throws IOException, MalformedURLException
	{
		Reader reader = null;
		try
		{
			reader = new UnicodeReader(new URL(resolve(locator)).openStream());
			return read(reader);
		}
		finally
		{
			if ( reader != null )
				reader.close();
		}
	}

	/**
	 * Save text to the location specified by a URL.
	 * 
	 * @param locator Represents a Uniform Resource Locator.
	 * @param text The text to be saved.
	 * 
	 * @throws MalformedURLException When a URL cannot be composed.
	 * @throws IOException When the location cannot be opened for writing.
	 */
	protected void saveURL(Locator locator, String text)
		throws MalformedURLException, IOException
	{
		URL url = new URL(resolve(locator));
		Writer writer = null;
		try
		{
			writer = "file".equals(url.getProtocol()) ? new FileWriter(url.getPath()) :
				new OutputStreamWriter(url.openConnection().getOutputStream());
			writer.write(text);
		}
		finally
		{
			if ( writer != null )
				writer.close();
		}
	}

	/**
	 * Read a file into a string.
	 * 
	 * @param file The file to read.
	 * 
	 * @return A string containing the text from the file.
	 * 
	 * @throws FileNotFoundException When the file cannot be located.
	 * @throws IOException When the file cannot be opened for reading.
	 */
	protected String read(File file)
		throws FileNotFoundException, IOException
	{
		FileReader reader = null;
		try
		{
			reader = new FileReader(file);
			return read(reader);
		}
		finally
		{
			if (reader != null)
				reader.close();
		}
	}

	private String read(Reader reader)
		throws IOException
	{
		StringBuilder text = new StringBuilder();
		char[] buffer = new char[IO_BUFFER_SIZE];
		int nchars = 0;
		while ( (nchars=reader.read(buffer)) >= 0 )
			text.append(buffer, 0, nchars);
		return text.toString();
	}

	/**
	 * Save text to the specified file.
	 * 
	 * @param text The text to save.
	 * @param file The file to be written.
	 * 
	 * @throws IOException When the file cannot be opened for writing.
	 */
	protected void save(String text, File file) throws IOException
	{
		FileWriter writer = null;
		try
		{
			writer = new FileWriter(file);
			writer.write(text);
		}
		finally
		{
			if (writer != null)
				writer.close();
		}
	}
	
	// KLUDGE: http://www.coderanch.com/t/499592/Streams/java/PipedOUtputStream-PipedInputStream
	private static PipedInputStream consolePipedInputStream;
	private static PipedInputStream getConsolePipedInputStream()
	{
		if ( consolePipedInputStream == null )
			consolePipedInputStream = new PipedInputStream();
		return consolePipedInputStream;
	}

	private static OutputStream consoleOutputStream;
	public static OutputStream getConsoleOutputStream(OutputStream defaultOutputStream)
	{
		if ( consoleOutputStream == null )
		{
			try
			{
				consoleOutputStream = new PipedOutputStream(getConsolePipedInputStream());
			}
			catch (IOException e)
			{
				consoleOutputStream = defaultOutputStream;
			}
		}
		return consoleOutputStream;
	}
	
	private static PrintStream consolePrintStream;
	/**
	 * Get a PrintStream for output to the console.
	 * @return A console print (output) stream.
	 */
	public static PrintStream getConsolePrintStream()
	{
		if ( consolePrintStream == null )
		{
			OutputStream cos = getConsoleOutputStream(System.out);
			consolePrintStream = new PrintStream(cos, true);
		}
		return consolePrintStream;
	}
	
	/**
	 * <p>Print text to the console. 
	 * 
	 * @param text The string to output.
	 */
	public static void console(String text)
	{
		getConsolePrintStream().println(text);
	}
	
	/**
	 * Bind a line number reader to the standard output stream.
	 * 
	 * @return A line number reader bound to System.out.
	 * @throws IOException When a reader cannot be bound to System.out.
	 */
	protected LineNumberReader consoleReader()
		throws IOException
	{
		System.setOut(getConsolePrintStream());
		System.setErr(getConsolePrintStream());
		InputStreamReader isrOut = new InputStreamReader(getConsolePipedInputStream());
		LineNumberReader lnrOut = new LineNumberReader(isrOut);
		return lnrOut;
	}

	/**
	 * Convert an exception into a simple message.
	 * @param ex Exception to convert into a message. 
	 */
	protected static String message(Throwable ex)
	{
		String error;
		if ( ex instanceof ScriptException )
		{
			ScriptException se = (ScriptException) ex;
			error = se.getClass().getSimpleName()
				+": "+se.getMessage();
			if ( se.getFileName() != null )
				error += ", "+se.getFileName();
			if ( se.getLineNumber() >= 0 )
				error += "@("+se.getLineNumber()
				+","+se.getColumnNumber()
				+")";
		}
		else
		{
			StackTraceElement st = ex.getStackTrace()[0];
			error = ex.getClass().getSimpleName()
				+": "+ex.getMessage()
				+", "+st.getFileName()
				+"@"+st.getLineNumber();
		}
		return error;
	}

	/**
	 * <p>Dispatch a notification for the given exception.</p>
	 * 
	 * <p>The exception is logged as an error and a message is
	 * dispatched as a notification.</p>
	 * 
	 * @param ex An exception for the content of the notification.
	 * 
	 * @see #notification(MessageType, String)
	 */
	public void notification(Exception ex)
	{
		String msg = message(ex);
		if ( ex instanceof FileNotFoundException )
		{
			log().warn(msg);
			notification(MessageType.WARN, msg);
		}
		else
		{	
			log().error(msg, ex);
			notification(MessageType.ERROR, msg);
		}
	}

	/**
	 * Notify user of a message for a given type.
	 * 
	 * @param type The message type.
	 * @param message The message text.
	 */
	public static void notification(MessageType type, String message)
	{
		switch ( Main.getMode() )
		{
			case GUI: GraphicalUI.guiMessageDialog(type, message); break;
			case TUI: TextualUI.tuiMessageDialog(type, message); break;
			default: CommandLI.show(type+" "+message); break;
		}
	}
	
	/**
	 * <p>Execute the extract, transform and load for the given batch.</p>
	 * <p>The batch is validated, a Reader is created for the source
	 * and a Writer is created for the target then the transform is invoked.</p>
	 * 
	 * @param batch The batch configuration.
	 * @param debug When true, increases the level for log messages.
	 * 
	 * @see #transform(Batch, Reader, Writer, boolean)
	 */
	public void execute(Batch batch, boolean debug)
	{
		log().info("Source: "+resolve(batch.getSource().getLocator()));
		log().info("Target: "+resolve(batch.getTarget().getLocator()));
		try
		{
			validate(batch);
			// Source
			UnicodeReader sourceReader = null;
			URL sourceURL = new URL(resolve(batch.getSource().getLocator()));
			String sourceCharset = batch.getSource().getCharset();
			URLConnection source = sourceURL.openConnection();
			source.setConnectTimeout(60000);
			source.setReadTimeout(300000);
			source.setDoInput(true);
			source.setDoOutput(false);
			// Target
			Writer targetWriter = null;
			URL targetURL = new URL(resolve(batch.getTarget().getLocator()));
			String targetCharset = batch.getTarget().getCharset();
			// Execute
			try
			{
				sourceReader = new UnicodeReader(source.getInputStream(), sourceCharset);
				if ( targetCharset == null )
					targetCharset = sourceReader.getEncoding();
				if ( "file".equals(targetURL.getProtocol()) )
				{
					targetWriter= new FileWriter(targetURL.getPath());
					OutputStream os = new FileOutputStream(targetURL.getPath());
					targetWriter = new OutputStreamWriter(os, targetCharset);
				}
				else
				{
					URLConnection target = targetURL.openConnection();
					target.setConnectTimeout(60000);
					target.setDoInput(false);
					target.setDoOutput(true);
					OutputStream os = target.getOutputStream();
					targetWriter = new OutputStreamWriter(os, targetCharset);
				}
				// Conditionally write a Byte Order Mark (BOM)
				ByteOrderMark targetBOM = ByteOrderMark.resolve(targetCharset);
				if ( targetBOM != null )
				{
					if ( targetBOM.isRecommended() || batch.getTarget().isByteOrderMark() )
						targetWriter.write(ByteOrderMark.BOM());
				}
				transform(batch, sourceReader, targetWriter, debug);
			}
			finally
			{
				if ( targetWriter != null )
					targetWriter.close();
				if ( sourceReader != null )
					sourceReader.close();
			}
		}
		catch (ScriptException se)
		{
			notification(MessageType.ERROR, message(se));
		}
		catch (Exception e)
		{
			notification(e);
		}
	}

	/**
	 * Resolve a locator to a simple URL representation.
	 * @param locator A locator containing URL components.
	 * @return A URL representation of the locator.
	 */
	protected static String resolve(Locator locator)
	{
		StringBuilder resolve = new StringBuilder();
		if ( !isEmpty(locator.getUrl()) )
			resolve.append(locator.getUrl());
		else
		{
			if ( !isEmpty(locator.getProtocol()) && !isEmpty(locator.getPath()))
			{
				resolve.append(locator.getProtocol());
				if ( !isEmpty(locator.getUsername()) )
					resolve.append(locator.getUsername());
				if ( !isEmpty(locator.getPassword()) )
					resolve.append(":"+locator.getPassword());
				if ( !isEmpty(locator.getHost()) )
					resolve.append(locator.getHost());
				if ( !isEmpty(locator.getPort().toString()) )
					resolve.append(":"+locator.getPort());
				resolve.append(locator.getPath());
				if ( !isEmpty(locator.getQuery()) )
					resolve.append("?"+locator.getQuery().replaceAll("\\s", ""));
				if ( !isEmpty(locator.getAnchor()) )
					resolve.append("#"+locator.getAnchor());
			}
		}
		return resolve.toString();
	}

	private static void validate(Batch batch)
	{
		if ( batch.getSource() == null )
			throw new IllegalArgumentException("Source is not defined.");
		if ( isEmpty(resolve(batch.getSource().getLocator())) )
			throw new IllegalArgumentException("Source locator is empty.");
		if ( batch.getTarget() == null )
			throw new IllegalArgumentException("Target is not defined.");
		if ( isEmpty(resolve(batch.getTarget().getLocator())) )
			throw new IllegalArgumentException("Target locator is empty.");
	}

	private static boolean isEmpty(String str)
	{
		return (str == null) || str.trim().isEmpty();
	}
	
	/**
	 * <p>Log list of supported languages.</p>
	 * <p>If the INFO level is enabled for the logger then the script engine manager
	 * is used to get a list of script engine factories. For each factory, the list
	 * of short names is logged.</p>
	 */
	protected void logScriptEngines()
	{
		if ( log().isInfoEnabled() )
		{
			List<ScriptEngineFactory> engineFactories = getScriptEngineManager().getEngineFactories();
			for (ScriptEngineFactory sef : engineFactories) 
			{
				StringBuilder sb = new StringBuilder("ScriptEngine = ");
				sb.append(sef.getEngineName()+" ");
				sb.append(sef.getEngineVersion());
				String punc = ": ";
				for ( String name : sef.getNames() )
				{
					sb.append(punc+name);
					punc = ", ";
				}
				log().info(sb.toString());
			}
		}
	}
	
	private File currentWorkingDirectory;
	/**
	 * Get the current working directory.
	 * 
	 * @return a File reference the current working directory.
	 */
	protected File getCurrentWorkingDirectory()
	{
		if ( currentWorkingDirectory == null )
		{
			String userDir = System.getProperty("user.dir");
			if ( userDir != null )
				setCurrentWorkingDirectory(new File(userDir));
		}
		return currentWorkingDirectory;
	}
	/**
	 * Set the current working directory.
	 * @param currentWorkingDirectory The current working directory. 
	 */
	protected void setCurrentWorkingDirectory(File cwd)
	{
		if ( cwd != null )
		{
			if ( cwd.isDirectory() )
				this.currentWorkingDirectory = cwd;
			else
				this.currentWorkingDirectory = cwd.getParentFile();
		}
		else
			this.currentWorkingDirectory = null;
	}

	private static String about;
	/**
	 * About this product.
	 * @return the product name and version. 
	 */
	protected static String about()
	{
		if ( about == null )
		{
			about =
				getPomProperties().getProperty("project.name") + " " +
				getPomProperties().getProperty("project.version");
		}
		return about;
	}
	
	private static Properties pomProperties;
	/**
	 * Selected Project Object Model (POM) properties.
	 * @return some POM properties.
	 */
	protected static Properties getPomProperties()
	{
		if ( pomProperties == null )
		{
			pomProperties = new Properties();
			InputStream is = null;
			try
			{
				try
				{
					is = pomProperties.getClass().getResourceAsStream("/pom.properties");
					pomProperties.load(is);
				}
				finally
				{
					if ( is != null )
						is.close();
				}
			}
			catch ( IOException ioe)
			{
				// Return empty properties.
			}
		}
		return pomProperties;
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:

