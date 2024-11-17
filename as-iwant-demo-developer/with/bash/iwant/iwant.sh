#!/bin/bash
set -eu

COMMIT=df4e853c91e7473a3c06915c326f2b7fc223d459
GITHUBUSER=wipu
URL=https://raw.githubusercontent.com/$GITHUBUSER/iwant/$COMMIT

HERE=$(dirname "$0")
cd "$HERE/../../.."

fetch() {
    local RELPATH=$1
    rm -f "$RELPATH"
    local PARENT=$(dirname "$RELPATH")
    mkdir -p "$PARENT"
    cd "$PARENT"
    wget "$URL/essential/iwant-entry/as-some-developer/$RELPATH"
    cd -
}

fetch with/ant/iw/build.xml
fetch with/bash/iwant/help.sh
chmod u+x with/bash/iwant/help.sh
fetch with/java/org/fluentjava/iwant/entry/Iwant.java

CONF=i-have/conf
mkdir -p "$CONF"
echo "iwant-from=https://github.com/$GITHUBUSER/iwant/archive/$COMMIT.zip" > "$CONF/iwant-from"
