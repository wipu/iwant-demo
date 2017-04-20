# iwant-demo
A project that demonstrates features of the [iwant](http://iwant.sourceforge.net) build system

## Quick start: Studying the code with Eclipse

After cloning this repository, cd to it and type your first wish to shell (remember that tab is your friend - oh, and you are expected to have bash and other standard unix tools in your path):

    as-iwant-demo-developer/with/bash/iwant/help.sh

_Note how you don't need to install or configure anything. A small amount of bootstrapping code will ensure you have the correct version of iwant serving your build wishes._

As this is the first wish, it forces the iwant bootstrapper to bootstrap iwant and generate more wishes. Use tab again and issue the next wish:

    as-iwant-demo-developer/with/bash/iwant/side-effect/eclipse-settings/effective

Now that you have the Eclipse settings generated, you can import the projects to Eclipse. (Don't copy them to the workspace, just import.)

Modules `iwant-demo-wsdef` and `iwant-demo-wsdefdef` define the build, and the rest of the modules are production modules. Your first entrypoint to the build is `IwantDemoWorkspaceModuleProvider.java`. (Use shift-ctrl-T to open it with Eclipse.) It defines the actual build module. The entrypoint to the build, defined as string here, is `IwantDemoWorkspaceFactory.java`. From that you can navigate your way with ctrl-click.

_Note how you can study how the build is defined by utilizing the Java type system and Eclipse navigation features instead of reading documentation and following loosely typed references and conventions._

## Studying test coverage (aka "running the tests")

When you have finished your initial study of the code you can make your next wish to get a test coverage report and pipe it to a browser:

    as-iwant-demo-developer/with/bash/iwant/target/jacoco-report-all/as-path | xargs -r -Ixxx chrome xxx/index.html

_Note how you didn't tell iwant to run tests. In fact, if you run the command again, it __wont' run them__ because you didn't change the code coverage by touching something._

## Building and running the application

To see the contents of the application cli distribution and to run it issue the following commands:

    as-iwant-demo-developer/with/bash/iwant/target/cli-distro/as-path | xargs -r find
    as-iwant-demo-developer/with/bash/iwant/target/cli-distro/as-path && as-iwant-demo-developer/.i-cached/target/cli-distro/run.sh

_Note how easily `CliDistro.java` includes all runtime dependencies of the application in the lib directory of the distribution._

Follow the error message of the application. For example, to print the first 4 prime numbers, type:

    as-iwant-demo-developer/with/bash/iwant/target/cli-distro/as-path && as-iwant-demo-developer/.i-cached/target/cli-distro/run.sh --primes 4

## Static code analysis with findbugs

You can get a findbugs report by running:

    as-iwant-demo-developer/with/bash/iwant/target/findbugs-report/as-path | xargs -r -Ixxx chrome xxx/findbugs-report/findbugs-report.html

