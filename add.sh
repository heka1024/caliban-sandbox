curl -X POST -H "Content-Type: application/json" \
	-d '{ "query": "mutation { addCoffee(name: \"India\", supID: 150, price: 8.50, sales: 0, total: 0) }" }' \
http://localhost:8080/graph
