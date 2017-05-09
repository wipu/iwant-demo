package org.oikarinen.iwantdemo.sloppylegacy;

import java.io.File;
import java.net.URLDecoder;

public class SloppyLegacy {

    /**
     * The default eclipse code formatter would insert line breaks here.
     */
    public String methodAbusingTheOverlongLinePolicyOfThisProject(int value) {
        if ((0 < value && value < 10) || (20 < value && value < 30) || (40 < value && value < 50) || (60 < value && value < 70) || (80 < value && value < 90)) {
            return "Value passed a check defined on an overline line: " + value;
        } else {
            return "Value failed a check defined on an overline line: " + value;
        }
    }

    /**
     * The default eclipse settings would warn about this.
     */
    public String methodUsingDeprecatedCode(String s) {
        return URLDecoder.decode(s);
    }

    /**
     * The default eclipse settings would warn about this.
     */
    public void methodWithDeadCode() {
        if (true) {
            System.out.println("true");
        } else {
            System.out.println("dead false");
        }
    }

    /**
     * This triggers a Findbugs warning
     */
    static void findbugsFodder(File f) {
        f.mkdirs();
    }

}
