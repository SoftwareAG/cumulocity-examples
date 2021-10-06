#!/usr/bin/env bash
set -e

function update-property {
    echo "update property ${1} to value ${2}"
    find . -name 'pom.xml' -exec sed -i "s/<${1}>.*<\/${1}>/<${1}>${2}<\/${1}>/g" {} \;
}

version=$(echo $1 | grep -Eo '[0-9]+\.[0-9]+\.[0-9]+')

update-property revision $version