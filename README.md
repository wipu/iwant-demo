# iwant-demo
A project that demonstrates features of the [iwant](http://iwant.sourceforge.net) build system

## Quick start: Studying the code with Eclipse

After cloning this, use tab in your shell to type the first wish:

    as-iwant-demo-developer/with/bash/iwant/help.sh

As this is the first wish, it forces the iwant bootstrapper to bootstrap iwant and generate more wishes. Use tab again and issue the next wish:

    as-iwant-demo-developer/with/bash/iwant/side-effect/eclipse-settings/effective

Now that you have the Eclipse settings generated, you can import the projects to Eclipse (don't copy them to the workspace, just import).

Modules `iwant-demo-wsdef` and `iwant-demo-wsdefdef` define the build, and the rest of the modules are production modules. Your first entrypoint to the build is `IwantDemoWorkspaceModuleProvider` (Use shift-ctrl-T to open it with Eclipse.) It defines the actual build module. The entrypoint to the build, defined as string here, is `IwantDemoWorkspaceFactory`. From that you can navigate your way with ctrl-click.

## Studying test coverage (aka "running the tests")

When you have finished your initial study of the code you can make your next wish to get a test coverage report and pipe it to a browser:

    as-iwant-demo-developer/with/bash/iwant/target/jacoco-report-all/as-path | xargs -r -Ixxx chrome xxx/index.html

## Building and running the application

To see the contents of the application cli distribution and to run it issue the following commands:

    as-iwant-demo-developer/with/bash/iwant/target/cli-distro/as-path | xargs -r find
    as-iwant-demo-developer/with/bash/iwant/target/cli-distro/as-path && as-iwant-demo-developer/.i-cached/target/cli-distro/run.sh

Follow the error message of the application. For example, to print the first 4 prime numbers, type:

    as-iwant-demo-developer/with/bash/iwant/target/cli-distro/as-path && as-iwant-demo-developer/.i-cached/target/cli-distro/run.sh --primes 4

