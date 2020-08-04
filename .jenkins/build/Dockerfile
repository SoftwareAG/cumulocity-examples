FROM centos:centos7
ENV SDKMAN_DIR /usr/local/sdkman
RUN yum install -y sudo libltdl-dev libtool-ltdl rpm rpm-build redhat-rpm-config openssh-clients git curl zip unzip which procmail bind-utils
RUN git config --global user.email "jenkins@cumulocity.com" && git config --global user.name "jenkins"
RUN curl -s "https://get.sdkman.io" | bash \
      && bash -c 'source "$SDKMAN_DIR/bin/sdkman-init.sh" &&  sdk install java $(sdk list java | grep -e 8\.0\..*-zulu[^fx] | sed -r s/^.*8/8/)'
ENV MAVEN_OPTS="-Xms1024m -Xmx4096m -XX:MaxMetaspaceSize=1024m -XX:+TieredCompilation -XX:TieredStopAtLevel=1 -Dhttps.protocols=TLSv1.2"
ENV JAVA_HOME="$SDKMAN_DIR/candidates/java/current"
ENV LANG en_US.UTF-8