#!/usr/bin/env bash

function update-property {
    echo "update property ${1} to value ${2}"
    find . -name 'pom.xml' | xargs sed -i "s/<${1}>.*<\/${1}>/<${1}>${2}<\/${1}>/g"
}

function update-dependencies {
    echo "Update properties in POMs to new version"
    PROPERTIES=(cumulocity.root.version cumulocity.dependencies.version cumulocity.model.version cumulocity.shared-components.version cumulocity.core.version c8y.core.version c8y.microservice-new.version c8y.clients.version c8y.dependencies.version c8y.model.version c8y.shared-components.version)

    for property in "${PROPERTIES[@]}"
    do
        update-property $property ${1}
    done
    if [ "$2" != "no-commit" ]; then
        hg commit -m "Update dependencies to new version"
    fi

}

update-dependencies $1 $2
