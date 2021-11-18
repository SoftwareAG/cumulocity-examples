#!/usr/bin/env bash
set -e

function update-property {
    echo "update property ${1} to value ${2}"
    find . -name 'pom.xml' -exec sed -i "s/<${1}>.*<\/${1}>/<${1}>${2}<\/${1}>/g" {} \;
}

version=$(echo $1 | grep -Eo '[0-9]+\.[0-9]+\.[0-9]+')

if [[ "$version" == "$1" ]]; then
  update-property changelist ""
else 
  changelist=$(echo $1 | sed -e "s/^$version//")
  update-property changelist $changelist
fi

update-property revision $version