#!/bin/env /usr/bin/bash
mvn versions:display-dependency-updates -Dmaven.version.ignore=.*-M.*,.*-beta.*,.*-alpha.*,.*\..alpha.*,.*-rc.* | grep -e "->" - | sort | uniq
