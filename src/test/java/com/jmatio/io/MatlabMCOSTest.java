/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.io;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLChar;
import com.jmatio.types.MLDouble;
import com.jmatio.types.MLInt8;
import com.jmatio.types.MLObject;

/**
 * This test verifies that ReducedHeader generated mat files work correctly.
 *
 * @author Matthew Dawson <matthew@mjdsystems.ca>
 */
@RunWith(JUnit4.class)
public class MatlabMCOSTest {
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testParsingSimpleEmptyMCOS() throws IOException {
		File file = fileFromStream("/mcos/simpleempty.mat");
		MatFileReader reader = new MatFileReader(file);
		Map<String, MLArray> content = reader.getContent();

		assertThat(content.size(), is(1));

		MLObject obj = (MLObject) content.get("obj");
		assertThat(obj, is(notNullValue()));

		assertThat(obj.getName(), is("obj"));
		assertThat(obj.getClassName(), is("SimpleEmpty"));
		assertThat(obj.getFields(0).size(), is(0));
	}

	@Test
	public void testParsingMultipleSimpleEmptyMCOS() throws IOException {
		File file = fileFromStream("/mcos/simpleempty_multiple.mat");
		MatFileReader reader = new MatFileReader(file);
		Map<String, MLArray> content = reader.getContent();

		assertThat(content.size(), is(2));

		MLObject obj = (MLObject) content.get("obj1");
		assertThat(obj, is(notNullValue()));

		assertThat(obj.getName(), is("obj1"));
		assertThat(obj.getClassName(), is("SimpleEmpty"));
		assertThat(obj.getFields(0).size(), is(0));

		obj = (MLObject) content.get("obj2");
		assertThat(obj, is(notNullValue()));

		assertThat(obj.getName(), is("obj2"));
		assertThat(obj.getClassName(), is("SimpleEmpty"));
		assertThat(obj.getFields(0).size(), is(0));
	}

	@Test
	public void testParsingSimpleSingleTextUnmodifiedMCOS() throws IOException {
		File file = fileFromStream("/mcos/simplesingletext_unmodified.mat");
		MatFileReader reader = new MatFileReader(file);
		Map<String, MLArray> content = reader.getContent();

		assertThat(content.size(), is(1));

		MLObject obj = (MLObject) content.get("obj");
		assertThat(obj, is(notNullValue()));

		assertThat(obj.getName(), is("obj"));
		assertThat(obj.getClassName(), is("SimpleSingleText"));
		Map<String, MLArray> fields = obj.getFields(0);
		assertThat(fields.size(), is(1));

		MLChar field = (MLChar) fields.get("test_text");
		assertThat(field.getString(0), is("Default text"));
	}

	@Test
	public void testParsingSimpleSingleTextMultipleMCOS() throws IOException {
		File file = fileFromStream("/mcos/simplesingletext_multiple.mat");
		MatFileReader reader = new MatFileReader(file);
		Map<String, MLArray> content = reader.getContent();

		assertThat(content.size(), is(3));

		MLObject obj = (MLObject) content.get("obj1");
		assertThat(obj, is(notNullValue()));

		assertThat(obj.getName(), is("obj1"));
		assertThat(obj.getClassName(), is("SimpleSingleText"));
		Map<String, MLArray> fields = obj.getFields(0);
		assertThat(fields.size(), is(1));

		MLChar field = (MLChar) fields.get("test_text");
		assertThat(field.getString(0), is("other text 1"));

		obj = (MLObject) content.get("obj2");
		assertThat(obj, is(notNullValue()));

		assertThat(obj.getName(), is("obj2"));
		assertThat(obj.getClassName(), is("SimpleSingleText"));
		fields = obj.getFields(0);
		assertThat(fields.size(), is(1));

		field = (MLChar) fields.get("test_text");
		assertThat(field.getString(0), is("Default text"));

		obj = (MLObject) content.get("obj3");
		assertThat(obj, is(notNullValue()));

		assertThat(obj.getName(), is("obj3"));
		assertThat(obj.getClassName(), is("SimpleSingleText"));
		fields = obj.getFields(0);
		assertThat(fields.size(), is(1));

		field = (MLChar) fields.get("test_text");
		assertThat(field.getString(0), is("other text 3"));
	}

	@Test
	public void testParsingHandleSinglePropertyMultipleMCOS() throws IOException {
		File file = fileFromStream("/mcos/handlesingle_multiple.mat");
		MatFileReader reader = new MatFileReader(file);
		Map<String, MLArray> content = reader.getContent();

		assertThat(content.size(), is(4));

		MLObject obj = (MLObject) content.get("obj1");
		assertThat(obj, is(notNullValue()));

		assertThat(obj.getName(), is("obj1"));
		assertThat(obj.getClassName(), is("HandleSingle"));
		assertThat(((MLObject) content.get("obj3")).getFields(0), is(sameInstance(obj.getFields(0))));
		Map<String, MLArray> fields = obj.getFields(0);
		assertThat(fields.size(), is(1));

		MLInt8 intField = (MLInt8) fields.get("myelement");
		assertThat(intField.getSize(), is(1));
		assertThat(intField.get(0).byteValue(), is((byte) 25));

		obj = (MLObject) content.get("obj3");
		assertThat(obj, is(notNullValue()));

		assertThat(obj.getName(), is("obj3"));
		assertThat(obj.getClassName(), is("HandleSingle"));
		assertThat(((MLObject) content.get("obj1")).getFields(0), is(sameInstance(obj.getFields(0))));
		fields = obj.getFields(0);
		assertThat(fields.size(), is(1));

		intField = (MLInt8) fields.get("myelement");
		assertThat(intField.getSize(), is(1));
		assertThat(intField.get(0).byteValue(), is((byte) 25));

		obj = (MLObject) content.get("obj2");
		assertThat(obj, is(notNullValue()));

		assertThat(obj.getName(), is("obj2"));
		assertThat(obj.getClassName(), is("HandleSingle"));
		assertThat(((MLObject) content.get("obj4")).getFields(0), is(sameInstance(obj.getFields(0))));
		fields = obj.getFields(0);
		assertThat(fields.size(), is(1));

		MLChar charField = (MLChar) fields.get("myelement");
		assertThat(charField.getString(0), is("testing"));

		obj = (MLObject) content.get("obj4");
		assertThat(obj, is(notNullValue()));

		assertThat(obj.getName(), is("obj4"));
		assertThat(obj.getClassName(), is("HandleSingle"));
		assertThat(((MLObject) content.get("obj2")).getFields(0), is(sameInstance(obj.getFields(0))));
		fields = obj.getFields(0);
		assertThat(fields.size(), is(1));

		charField = (MLChar) fields.get("myelement");
		assertThat(charField.getString(0), is("testing"));
	}

	@Test
	public void testMultipleMCOSInArray() throws IOException {
		File file = fileFromStream("/mcos/simplesingletext_multiplearray.mat");
		MatFileReader reader = new MatFileReader(file);
		Map<String, MLArray> content = reader.getContent();

		assertThat(content.size(), is(1));

		MLObject obj = (MLObject) content.get("a");
		assertThat(obj, is(notNullValue()));

		assertThat(obj.getName(), is("a"));
		assertThat(obj.getClassName(), is("SimpleSingleText"));
		assertThat(((MLDouble) obj.getFields(0).get("test_text")).get(0), is(1.0));
		assertThat(((MLDouble) obj.getFields(1).get("test_text")).get(0), is(2.0));
		assertThat(((MLDouble) obj.getFields(2).get("test_text")).get(0), is(3.0));
		assertThat(((MLDouble) obj.getFields(3).get("test_text")).get(0), is(4.0));
	}

	private File fileFromStream(String location) throws IOException {
		String outname = location.replace("/", "_");
		File f = folder.newFile(outname);
		InputStream stream = MatlabMCOSTest.class.getResourceAsStream(location);
		OutputStream fos = new BufferedOutputStream(new FileOutputStream(f));
		byte[] buffer = new byte[1024];
		int read = 0;
		while ((read = stream.read(buffer)) != -1) {
			fos.write(buffer, 0, read);
		}
		fos.flush();
		fos.close();
		return f;
	}
}