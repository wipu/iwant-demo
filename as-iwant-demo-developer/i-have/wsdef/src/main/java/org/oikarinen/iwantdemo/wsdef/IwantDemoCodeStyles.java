package org.oikarinen.iwantdemo.wsdef;

import net.sf.iwant.api.javamodules.CodeFormatterPolicy;
import net.sf.iwant.api.javamodules.CodeFormatterPolicy.TabulationCharValue;
import net.sf.iwant.api.javamodules.CodeStyle;
import net.sf.iwant.api.javamodules.CodeStylePolicy;
import net.sf.iwant.api.javamodules.CodeStylePolicy.CodeStylePolicySpex;

class IwantDemoCodeStyles {

	final static CodeStylePolicy COMMON = commonPolicyWith().end();

	final static CodeStylePolicy LEGACY = commonPolicyWith()
			.ignore(CodeStyle.DEPRECATION).ignore(CodeStyle.DEAD_CODE).end();

	static CodeFormatterPolicy legacyEclipseFormatting() {
		CodeFormatterPolicy codeFormatterPolicy = new CodeFormatterPolicy();
		codeFormatterPolicy.tabulationChar = TabulationCharValue.SPACE;
		codeFormatterPolicy.lineSplit = 250;
		return codeFormatterPolicy;
	}

	private static CodeStylePolicySpex commonPolicyWith() {
		return CodeStylePolicy.defaultsExcept()
				// don't complain about PMD suppressions:
				.ignore(CodeStyle.UNHANDLED_WARNING_TOKEN);
	}

}
