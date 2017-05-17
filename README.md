# iwant-demo
A multi-module project that demonstrates features of the [`iwant`](http://iwant.sourceforge.net) build system

## Quick start: Studying the code with Eclipse

After cloning this repository, cd to it and type your first wish to shell (remember that tab is your friend - oh, and you are expected to have bash and other standard unix tools in your path, as well as a Java SDK):

    as-iwant-demo-developer/with/bash/iwant/help.sh

_Note how you don't need to install or configure anything. A small amount of bootstrapping code will ensure you have the correct version of [`iwant`](http://iwant.sourceforge.net) serving your build wishes._

As this is the first wish, it forces the [`iwant`](http://iwant.sourceforge.net) bootstrapper to bootstrap [`iwant`](http://iwant.sourceforge.net) and generate more wishes. Use tab again and issue the next wish:

    as-iwant-demo-developer/with/bash/iwant/side-effect/eclipse-settings/effective

_Note that we have to start with an exceptional way of using [`iwant`](http://iwant.sourceforge.net): we wished for a side-effect, a __mutation to the system__. Most of the time we wish for targets instead, but since Eclipse dictates the location of its settings files and owns them, we have to be imperative here._

Now that you have the Eclipse settings generated, you can import the projects to Eclipse. (Don't copy them to the workspace, just import.)

Modules `iwant-demo-wsdef` and `iwant-demo-wsdefdef` define the build, and the rest of the modules are production modules. Your first entrypoint to the build is [`IwantDemoWorkspaceModuleProvider.java`](https://github.com/wipu/iwant-demo/blob/master/as-iwant-demo-developer/i-have/wsdefdef/src/main/java/org/oikarinen/iwantdemo/wsdefdef/IwantDemoWorkspaceModuleProvider.java). (Use shift-ctrl-T to open it with Eclipse.) It defines the actual build module. The entrypoint to the build, defined as string here, is [`IwantDemoWorkspaceFactory.java`](https://github.com/wipu/iwant-demo/blob/master/as-iwant-demo-developer/i-have/wsdef/src/main/java/org/oikarinen/iwantdemo/wsdef/IwantDemoWorkspaceFactory.java). From that you can navigate your way with ctrl-click.

_Note how you can study how the build is defined by utilizing the Java type system and Eclipse navigation features instead of reading documentation and following loosely typed references and conventions._

## Studying test coverage (aka "running the tests")

When you have finished your initial study of the code you can make your next wish to get a test coverage report and pipe it to a browser:

    as-iwant-demo-developer/with/bash/iwant/target/jacoco-report-all/as-path | xargs -r chrome

Or, if you prefer fancier use of `xargs`, open the browser directly to the correct file:

    as-iwant-demo-developer/with/bash/iwant/target/jacoco-report-all/as-path | xargs -r -Ixxx chrome xxx/index.html

_Note how you didn't tell [`iwant`](http://iwant.sourceforge.net) to run tests. In fact, if you run the command again, it __wont' run them__ because you didn't change the code coverage by touching anything. Even the methods of [`IwantDemoWorkspace.java`](https://github.com/wipu/iwant-demo/blob/master/as-iwant-demo-developer/i-have/wsdef/src/main/java/org/oikarinen/iwantdemo/wsdef/IwantDemoWorkspace.java) that define the target, `jacocoReportAll` and  `jacocoReport`, don't mention anything imperative like running tests but just define nouns related to the report you are after._

_Also note how all progress output of refreshing a target is printed to stderr, not stdout, so the output of the wish, the __path__ of the target, is the only thing that goes to the next process in the pipeline - xargs and chrome in this case._

## (Building and) running the application

To see the contents of the application cli distribution and to run it issue the following commands:

    as-iwant-demo-developer/with/bash/iwant/target/cli-distro/as-path | xargs -r find
    as-iwant-demo-developer/with/bash/iwant/target/cli-distro/as-path && as-iwant-demo-developer/.i-cached/target/cli-distro/run.sh

_Note how easily [`CliDistro.java`](https://github.com/wipu/iwant-demo/blob/master/as-iwant-demo-developer/i-have/wsdef/src/main/java/org/oikarinen/iwantdemo/wsdef/CliDistro.java) includes all runtime dependencies of the application in the lib directory of the distribution._

Follow the error message of the application. For example, to print the first 4 prime numbers, type:

    as-iwant-demo-developer/with/bash/iwant/target/cli-distro/as-path && as-iwant-demo-developer/.i-cached/target/cli-distro/run.sh --primes 4

## Using code to build during build: code generation

The cli demonstrates usage of a generated javabean, `GeneratedBean`,  with the following command:

    as-iwant-demo-developer/with/bash/iwant/target/cli-distro/as-path && as-iwant-demo-developer/.i-cached/target/cli-distro/run.sh --generatedbean "new value"

See how [`IwantDemoModules.java`](https://github.com/wipu/iwant-demo/blob/master/as-iwant-demo-developer/i-have/wsdef/src/main/java/org/oikarinen/iwantdemo/wsdef/IwantDemoModules.java) defines `generatedJavaBeansJava.jar` as a jar of classes compiled from java files generated by a custom target, [`GeneratedJavaBean.java`](https://github.com/wipu/iwant-demo/blob/master/as-iwant-demo-developer/i-have/wsdef/src/main/java/org/oikarinen/iwantdemo/wsdef/GeneratedJavaBean.java).

The code generator [`JavaBeanGenerator.java`](https://github.com/wipu/iwant-demo/blob/master/iwant-demo-javabean-generator/src/main/java/org/oikarinen/iwantdemo/javabeangenerator/JavaBeanGenerator.java) is defined in a module that is not only built by the build system but also used by it during build. That's why it is defined very early, by [`IwantDemoWorkspaceModuleProvider.java`](https://github.com/wipu/iwant-demo/blob/master/as-iwant-demo-developer/i-have/wsdefdef/src/main/java/org/oikarinen/iwantdemo/wsdefdef/IwantDemoWorkspaceModuleProvider.java) and included as a dependency to the `iwant-demo-wsdef` module that defines most of the build.

## Cache validity of a custom target

Note how [`GeneratedJavaBean.java`](https://github.com/wipu/iwant-demo/blob/master/as-iwant-demo-developer/i-have/wsdef/src/main/java/org/oikarinen/iwantdemo/wsdef/GeneratedJavaBean.java) declares the ingredients it needs for generating the java files. It not only declares the source file ([`generated-javabeans/beans.txt`](https://github.com/wipu/iwant-demo/blob/master/generated-javabeans/beans.txt)) it consumes as an ingredient, but also its own java file and the classpath location that contains the code generator, `JavaBeanGenerator.class`.

This makes sure the target will be refreshed if you touch any of the declared ingredients.

Try this by how touching [`generated-javabeans/beans.txt`](https://github.com/wipu/iwant-demo/blob/master/generated-javabeans/beans.txt), [`GeneratedJavaBean.java`](https://github.com/wipu/iwant-demo/blob/master/as-iwant-demo-developer/i-have/wsdef/src/main/java/org/oikarinen/iwantdemo/wsdef/GeneratedJavaBean.java) or [`JavaBeanGenerator.java`](https://github.com/wipu/iwant-demo/blob/master/iwant-demo-javabean-generator/src/main/java/org/oikarinen/iwantdemo/javabeangenerator/JavaBeanGenerator.java) causes a refresh of the target `generatedJavaBeansJava.classes` when requesting for example for target `cli-distro`.

_Also note how the target is *not* refreshed if none of its ingredients has changed._

## Static code analysis with findbugs

You can get a findbugs report by running:

    as-iwant-demo-developer/with/bash/iwant/target/findbugs-report/as-path | xargs -r -Ixxx chrome xxx/findbugs-report/findbugs-report.html

## Code style and formatting

The project demonstrates customizing the code style policy for its modules in
[`IwantDemoModules.java`](https://github.com/wipu/iwant-demo/blob/master/as-iwant-demo-developer/i-have/wsdef/src/main/java/org/oikarinen/iwantdemo/wsdef/IwantDemoModules.java) method `commonSettings`. The policies are defined in [`IwantDemoCodeStyles.java`](https://github.com/wipu/iwant-demo/blob/master/as-iwant-demo-developer/i-have/wsdef/src/main/java/org/oikarinen/iwantdemo/wsdef/IwantDemoCodeStyles.java).

The module `iwant-demo-sloppy-legacy` uses exceptional code formatting and style settings.

_Note how easily you can enforce a zero tolerance of warnings even in a project with below-standard legacy code by relaxing opinionated default settings where needed._

## Simple module compiled from sources from github

[`joulu unsigned-byte`](https://github.com/wipu/joulu/tree/master/unsigned-byte) is a simple depencencyless module. The method `jouluUnsignedByte` of [`IwantDemoModules.java`](https://github.com/wipu/iwant-demo/blob/master/as-iwant-demo-developer/i-have/wsdef/src/main/java/org/oikarinen/iwantdemo/wsdef/IwantDemoModules.java) defines it as a java binary module compiled from sources downloaded from github.

To use it from the cli run

    as-iwant-demo-developer/with/bash/iwant/target/cli-distro/as-path && as-iwant-demo-developer/.i-cached/target/cli-distro/run.sh -u 3

_Note how easy and natural it is to reuse code that hasn't even been "officially" published to a [(maven)](https://mvnrepository.com/) binary repository._

## Case: gcc, static and dynamic linking, code generation (Anteru's build-systems example)

Anteru compares several build systems in his [Build systems blog](https://anteru.net/blog/2017/build-systems-intro/index.html). He hosts the [example project at bitbucket](https://bitbucket.org/Anteru/build-systems).

To be honest [`iwant`](http://iwant.sourceforge.net) is heavy for very simple (non-java) projects, because it requires some bootstrapping overhead (the wsdefdef and wsdef java modules) and, in practice, a heavy IDE for editing the build.

But as [`AnteruBuildSystemsExample.java`](https://github.com/wipu/iwant-demo/blob/master/as-iwant-demo-developer/i-have/wsdef/src/main/java/org/oikarinen/iwantdemo/wsdef/AnteruBuildSystemsExample.java) demonstrates, the actual target definitions of Anteru's example project are reasonably dry. (And the bigger the project, the drier it can be made with the power of Java and object oriented programming.)

To try out the example executable run

    as-iwant-demo-developer/with/bash/iwant/target/anteru-build-systems-example/as-path && as-iwant-demo-developer/.i-cached/target/anteru-build-systems-example/run.sh

_Note how the target is refreshed even when you make a trivial change to any shell command that defines a target. Even GNU make that otherwise is a very fine example of a declarative lazy build system falls for [cache invalidation](https://martinfowler.com/bliki/TwoHardThings.html) here._

_Also note how easy it is to not only define parts of the application but also a full distribution directory with a run script._

## More?

There is a lot more you can do with [`iwant`](http://iwant.sourceforge.net). Why don't you tell me what you want demonstrated next? Maybe challenge [`iwant`](http://iwant.sourceforge.net) with something that is especially difficult for other build systems. Or especially easy for them, for comparison. Or fork this project and do it yourself! Don't hesitate to ask for help.

_- Ville Oikarinen (firstname at lastname dot org)_

_(author of [`iwant`](http://iwant.sourceforge.net))_
