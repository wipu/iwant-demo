#!/bin/bash

REV=891

set -eu

HERE=$(dirname "$0")
cd "$HERE/../../.."

URL=https://svn.code.sf.net/p/iwant/code/trunk

svn export --force -r "$REV" "$URL/essential/iwant-entry/as-some-developer/with"

CONF=i-have/conf
mkdir -p "$CONF"
echo "iwant-from=$URL@$REV" > "$CONF/iwant-from"
