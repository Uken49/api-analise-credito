local-env-create:
	docker-compose -f stack.yml up -d
	sleep 3
	docker cp data/data.sql postgres_credit_analysis:/var/lib/postgresql/data.sql
	docker exec postgres_credit_analysis psql -h localhost -U admin -d postgres -a -f ./var/lib/postgresql/data.sql

local-env-destroy:
	docker-compose -f stack.yml down