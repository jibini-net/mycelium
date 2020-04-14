package net.jibini.cliff;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jibini.cliff.util.StreamUtil;

public class ConfigFile extends JSONObject
{
	private static Logger log = LoggerFactory.getLogger(ConfigFile.class);

	private File file;

	private ConfigFile(String contents)
	{
		super(contents);
	}

	private ConfigFile()
	{
		super();
	}

	public static ConfigFile create(String path) throws IOException
	{
		ConfigFile result = new ConfigFile();
		File file = new File(path);

		if (file.exists())
		{
			log.debug("Found existing config file '" + path + "', loading . . .");
			log.debug("(" + file.getAbsolutePath() + ")");
			FileInputStream input = new FileInputStream(file);
			String configContents = StreamUtil.readTextFile(input);
			result = new ConfigFile(configContents);
		} else
		{
			result = new ConfigFile();
			log.debug("No existing config file '" + path + "', created empty");
		}

		result.file = file;
		return result;
	}

	public boolean setDefault(JSONObject node, String key, Object value)
	{
		if (node.has(key))
			return true;
		else
		{
			node.put(key, value);
			return false;
		}
	}

	public boolean setDefault(String key, Object value)
	{
		return setDefault(this, key, value);
	}

	public void writeConfig() throws IOException
	{
		log.debug("Writing configuration to file . . .");

		if (!file.exists())
		{
			log.debug("File doesn't exist, creating . . .");
			if (file.getParentFile() != null)
				file.getParentFile().mkdirs();
			file.createNewFile();
		}

		FileOutputStream stream = new FileOutputStream(file);
		StreamUtil.writeTextFile(stream, toString(2));
	}

	public void delete()
	{
		log.debug("Deleting config file . . .");
		file.delete();
	}
}
