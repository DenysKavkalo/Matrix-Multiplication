#!/bin/sh
export HZ_JAVA_OPTS="-cp /opt/hazelcast/libs/app.jar:/opt/hazelcast/libs/*"
exec hz start
