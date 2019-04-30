#!/bin/bash
set +eu
[[ -s "$SDKMAN_DIR/bin/sdkman-init.sh" ]] && source "$SDKMAN_DIR/bin/sdkman-init.sh"
set -e

export resources=hudson@yum.cumulocity.com
export release_args="-DskipTests -Dmaven.javadoc.skip=true -Dskip.microservice.package=false -Dskip.agent.package.container=false"

function tag-version {
    tag=$1
    hg commit -m "[maven-release-plugin] prepare release ${tag}" || echo ""
    hg tag -f -m "copy for tag ${tag}" "${tag}"
}

function update-property {
    echo "update property ${1} to value ${2}"
    find . -name 'pom.xml' | xargs sed -i "s/<${1}>.*<\/${1}>/<${1}>${2}<\/${1}>/g"
}