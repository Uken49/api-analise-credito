local-env-create:
	docker-compose -f stack.yml up -d
	sleep 3
	docker cp data.sql/data.sql.sql postgres:/var/lib/postgresql/data.sql
	docker exec postgres psql -h localhost -U admin -d postgres -a -f ./var/lib/postgresql/data.sql/ddl.sql

local-env-destroy:
	docker-compose -f stack.yaml down