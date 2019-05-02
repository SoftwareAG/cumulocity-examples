FROM buildpack-deps:stretch-scm
ENV SDKMAN_DIR /usr/local/sdkman
RUN apt-get update \
      && apt-get upgrade -y \
      && apt-get install -y sudo libltdl-dev rpm openssh-client openssl gnupg mercurial curl zip unzip\
      && curl -s "https://get.sdkman.io" | bash \
      && bash -c 'source "$SDKMAN_DIR/bin/sdkman-init.sh" && sdk install java $(sdk list java | grep 7.0 | awk "{print $3}" )'\
      && rm -rf /var/lib/apt/lists/* \
      && apt-get clean
ENV MAVEN_OPTS="-Xms1024m -Xmx4096m -XX:PermSize=1024m -XX:+TieredCompilation -XX:TieredStopAtLevel=1 -Dhttps.protocols=TLSv1.2"
ENV JAVA_HOME="$SDKMAN_DIR/candidates/java/current"