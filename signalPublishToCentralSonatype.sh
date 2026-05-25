#!/usr/bin/env bash
# Not sure if this is really necessary and working
#set -x
token=$1
echo "Posting new deployment to ossrh-staging-api.central"
curl -X 'POST' \
			  'https://ossrh-staging-api.central.sonatype.com/manual/upload/defaultRepository/net.jqwik' \
			  -H 'accept: application/json' \
			  -H "Authorization: Bearer $token"