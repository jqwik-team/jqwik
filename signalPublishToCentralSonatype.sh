#!/usr/bin/env bash
#set -x
token=$1
echo "Posting new deployment to ossrh-staging-api.central"
curl -X 'POST' \
			  'https://ossrh-staging-api.central.sonatype.com/manual/upload/defaultRepository/net.jqwik' \
			  -H 'accept: application/json' \
			  -H "Authorization: Bearer $token"