#!/bin/bash

if [ $# -lt 3 ]
then
  echo "usage: poll_db.sh <host> <username> <password>"
  exit 0
fi

INIT_SCRIPT_PATH='/scripts/schema.sql'
SQLCMD_PATH='/opt/mssql-tools/bin/sqlcmd'
HOST=$1
USER=$2
PASSWORD=$3
SLEEP_SECS=10
MAX_ATTEMPTS=10

echo "using $HOST $USER $PASSWORD"
poll_db() {
  $SQLCMD_PATH -S $HOST -U $USER -P $PASSWORD > /dev/null
  return $?
}

counter=0
while ! poll_db 
do
  if [ $counter -eq $MAX_ATTEMPTS ]
  then
    echo "DB Unavailable after $MAX_ATTEMPTS attempts.  Exiting."
    exit 1
  fi

  echo "DB not available.  Sleeping.."
  counter=$((counter + 1))
  sleep $SLEEP_SECS
done

$SQLCMD_PATH -S $HOST -U $USER -P $PASSWORD -i $INIT_SCRIPT_PATH
exit 0
