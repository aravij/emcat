// This code is intended to be used as input to PMD Java parser
// and not used as a Java source file in this project

package com.github.emcat.metric_calculators;

public class JavaSourceCodeExample {
    /*
     * Simple example of java class methods to test NCSS and Cyclomatic complexity calculation.
     * Expected metrics values:
     *  - NCSS = 4
     *  - Cyclomatic complexity = 2
     */
    public void methodExample() {             // NCSS: +1, Cyclomatic: +0
        int x = 0;                            // NCSS: +1, Cyclomatic: +0

        for (int i = 0; i < 10; i++) {        // NCSS: +1, Cyclomatic: +1
            x += i;                           // NCSS: +1, Cyclomatic: +1
        }
    }
}
