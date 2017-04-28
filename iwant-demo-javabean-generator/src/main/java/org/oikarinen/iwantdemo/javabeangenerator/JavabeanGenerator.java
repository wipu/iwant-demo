package org.oikarinen.iwantdemo.javabeangenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class JavabeanGenerator {

	private final String pak;
	private final String classname;
	private final List<String> props = new ArrayList<>();

	public JavabeanGenerator(String src) throws IOException {
		try (BufferedReader b = new BufferedReader(new StringReader(src))) {
			pak = b.readLine();
			classname = b.readLine();
			while (true) {
				String prop = b.readLine();
				if (prop == null) {
					return;
				}
				props.add(prop);
			}
		}
	}

	public static JavabeanGenerator fromSource(String src) throws IOException {
		return new JavabeanGenerator(src);
	}

	public String parentDirPath() {
		return pak.replace(".", "/");
	}

	public String basename() {
		return classname + ".java";
	}

	public String content() {
		StringBuilder b = new StringBuilder();
		b.append("package " + pak + ";\n");
		b.append("public class " + classname + " {\n\n");
		for (String prop : props) {
			String[] parts = prop.split(" ");
			String type = parts[0];
			String name = parts[1];

			b.append("  private " + type + " " + name + ";\n");
			b.append("  public " + type + " get" + capitalized(name)
					+ "() { return " + name + "; }\n");
			b.append("  public void set" + capitalized(name) + "(" + type + " "
					+ name + ") { this." + name + " = " + name + "; }\n\n");
		}
		b.append("}\n");
		return b.toString();
	}

	private static String capitalized(String s) {
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

}
