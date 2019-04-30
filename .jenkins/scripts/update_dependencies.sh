#!/usr/bin/env bash
set -e
source ${BASH_SOURCE%/*}/common.sh

function update-dependencies {
    echo "Update properties in POMs to new version"
    PROPERTIES=(cumulocity.root.version cumulocity.dependencies.version cumulocity.model.version cumulocity.shared-components.version cumulocity.core.version c8y.core.version c8y.microservice-new.version c8y.clients.version c8y.dependencies.version c8y.model.version c8y.shared-components.version)

    for property in "${PROPERTIES[@]}"
    do
        update-property $property ${1}
    done
    hg commit -m "Update dependencies to new version"

}

update-dependencies $1