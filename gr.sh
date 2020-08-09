curl -X POST -H "Content-Type: application/json" \
	-d '{ "query": "{ coffees { name price }}" }' \
http://localhost:8080/graph
