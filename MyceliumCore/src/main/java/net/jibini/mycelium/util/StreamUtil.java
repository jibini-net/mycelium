package net.jibini.mycelium.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class StreamUtil
{
	public static String readTextFile(InputStream stream) throws IOException
	{
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String line;
		while ((line = reader.readLine()) != null)
			builder.append(line).append('\n');
		reader.close();
		return builder.toString();
	}
	
	public static void writeTextFile(OutputStream stream, String contents) throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
		writer.write(contents);
		writer.close();
	}
}
