curl -X POST -H "Content-Type: application/json" \
	-d '{ "query": "mutation { deleteCharacter(name: \"koo\") }" }' \
http://localhost:8080/graph
