package org.oikarinen.iwantdemo.javabeangenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class JavaBeanGeneratorTest {

	@Test
	public void beanA() throws IOException {
		StringBuilder b = new StringBuilder();
		b.append("beans.a\n");
		b.append("BeanA\n");
		b.append("String s\n");
		b.append("int i\n");

		JavaBeanGenerator gen = JavaBeanGenerator.fromSource(b.toString());

		assertEquals("beans/a", gen.parentDirPath());
		assertEquals("BeanA.java", gen.basename());
		assertEquals("package beans.a;\n" + "public class BeanA {\n" + "\n"
				+ "  private String s;\n"
				+ "  public String getS() { return s; }\n"
				+ "  public void setS(String s) { this.s = s; }\n" + "\n"
				+ "  private int i;\n" + "  public int getI() { return i; }\n"
				+ "  public void setI(int i) { this.i = i; }\n" + "\n" + "}\n"
				+ "", gen.content());
	}

	@Test
	public void beanB() throws IOException {
		StringBuilder b = new StringBuilder();
		b.append("beans.b\n");
		b.append("BeanB\n");
		b.append("double dublo\n");

		JavaBeanGenerator gen = JavaBeanGenerator.fromSource(b.toString());

		assertEquals("beans/b", gen.parentDirPath());
		assertEquals("BeanB.java", gen.basename());
		assertEquals("package beans.b;\n" + "public class BeanB {\n" + "\n"
				+ "  private double dublo;\n"
				+ "  public double getDublo() { return dublo; }\n"
				+ "  public void setDublo(double dublo) { this.dublo = dublo; }\n"
				+ "\n" + "}\n" + "", gen.content());
	}

}
