#!/bin/sh
# wait-for.sh

# exit if any state != 0 occurs in any following command
set -e

# setting host to first argument passed to the script (wait for $1 whatever it is)
host="$1"
# shifting all cl-arguments to the left
shift

# waiting for host to be available using netcat with -z to not send data but just querying for listening daemons
echo "Waiting for $host..."
until nc -z -v -w30 $host; do
  sleep 1
done

echo "$host is ready."

/bin/sh -c ./docker-entrypoint.sh && nginx -g 'daemon off;'