#!/bin/bash

# test Java 8
if ! $(java -version 2>&1 | head -1 | grep '\"1\.8' > /dev/null); then
    echo "Not a JAVA 8 version: '$(java -version 2>&1 | head -1)'"
    exit 1
fi

# configure variables
. "$(cd $(dirname "$0") && pwd)"/utils.sh

ARGS=$*

PATHSEP=':'

VLIBTOUR=${HOME}/.m2/repository/eu/telecomsudparis/vlibtour/

CLASSPATH=${CLASSPATH}${PATHSEP}${TOURMANAGEMENTENTITY}${PATHSEP}${TOURMANAGEMENTAPI}${PATHSEP}${TOURMANAGEMENTADMINCLIENT}${PATHSEP}${PATHSEP}${CLIENTAPI}

CLASS=vlibtour.vlibtour_tour_management_admin_client.VlibTourTourManagementAdminClient

# Start the client
CMD="java -cp ${CLASSPATH} ${CLASS} ${ARGS}"

$CMD
