FROM gitpod/workspace-full

USER gitpod

RUN bash -c ". /home/gitpod/.sdkman/bin/sdkman-init.sh && \
    sdk install java 17.0.9-tem && \
    sdk default java 17.0.9-tem && \
    sdk install maven 3.9.5 && \
    sdk default maven 3.9.5"