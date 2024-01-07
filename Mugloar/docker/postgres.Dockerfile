# Use the official PostgreSQL image as the base image
FROM postgres:latest

# Set environment variables for PostgreSQL
#ENV POSTGRES_DB mugloar
#ENV POSTGRES_USER mugloar
#ENV POSTGRES_PASSWORD 123

# here we can add a script to init database into the container
#COPY init.sql /docker-entrypoint-initdb.d/

# Set a label with the name of the image
LABEL name="mugloar-postgres"