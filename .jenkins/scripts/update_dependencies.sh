#!/usr/bin/env bash

function update-property {
    echo "update property ${1} to value ${2}"
    find . -name 'pom.xml' | xargs sed -i "s/<${1}>.*<\/${1}>/<${1}>${2}<\/${1}>/g"
}

function update-dependencies {
    echo "Update properties in POMs to new version"
    PROPERTIES=(c8y.version)

    for property in "${PROPERTIES[@]}"
    do
        update-property $property ${1}
    done
    if [ "$2" != "no-commit" ]; then
        git commit --allow-empty -am "Update dependencies to new version"
    fi

}

update-dependencies $1 $2
