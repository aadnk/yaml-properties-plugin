package com.comphenix.maven.yamlplugin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.yaml.snakeyaml.Yaml;

import com.google.common.base.Preconditions;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

@Mojo(name = "read-yaml-properties", defaultPhase = LifecyclePhase.VALIDATE)
public class YamlPropertiesPlugin extends AbstractMojo {
	private static final String INITIAL_PREFIX = "yaml.";
	
	@Parameter(defaultValue = "${project}", required  = true, readonly = true)
	private MavenProject project;
	
    /**
     * The YAML files that will be used when reading properties.
     */
	@Parameter(required = true)
    private Map<String, String> files;
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		int loaded = 0;
		
		// Retrieve the properties from each YAML file
		for (Map.Entry<String, String> entry : files.entrySet()) {
			File file = new File(entry.getValue());
			
			if (file.exists()) {
				try {
					getLog().debug("Loading YAML file " + entry);
					
					processYaml(entry.getKey(), file);
					loaded++;
					
				} catch (IOException e) {
					// Let the user know
					throw new MojoFailureException("Corrupt YAML file " + file.getAbsolutePath(), e);
				}
			} else {
				getLog().error("Unable to find YAML file " + entry.getValue());
			}
		}
		
		// That can't be right ...
		if (loaded == 0) {
			getLog().warn("No YAML files loaded.");
		}
	}
	
	private void processYaml(String name, File file) throws IOException {
		Preconditions.checkNotNull(name, "name cannot be NULL.");
		Preconditions.checkNotNull(file, "file cannot be NULL.");
	
		try (Reader reader = Files.newBufferedReader(file.toPath(), Charset.forName("UTF-8"))) {
			Yaml yaml = new Yaml();
			addProperties(INITIAL_PREFIX + name, yaml.load(reader));
		}
	}
	
	@SuppressWarnings("unchecked")
	private void addProperties(String prefix, Object value) {
		// We skip NULL fields
		if (value instanceof Map) {
			for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) value).entrySet()) {
				addProperties(prefix + "." + entry.getKey().toString(), entry.getValue());
			}
		} else if (value instanceof List) {
			List<Object> list = (List<Object>) value;
			
			// Index as an array
			for (int i = 0; i < list.size(); i++) {
				addProperties(prefix + "[" + i + "]", list.get(i));
			}
		} else if (value != null) {
			// Just associate the value
			project.getProperties().setProperty(prefix, value.toString());			
		}
	}
}
