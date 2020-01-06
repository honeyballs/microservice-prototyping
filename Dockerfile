FROM postgres
COPY /schema_script/schema.sql /docker-entrypoint-initdb.d/schema.sql