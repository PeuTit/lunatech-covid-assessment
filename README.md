# Lunatech COVID Assessment

You can find instructions for the assignment in the [`INSTRUCTIONS.md`](INSTRUCTIONS.md) file.

## How to:

run the application (need to be run from `/application`):
```sh
sbt run
```

Initialize & launch the database (need to be run in the root folder):
```sh
docker-compose up
```

Generate the Scala documentation with Scaladoc:
```sh
sbt doc
```

To view it in a browser (again from `/application`):
```sh
cd target/{your_scala_version}/api

python3 -m http.server {server_port}
```
## Endpoints documentation

Query:

The `name` parameter is mandatory
The `date` parameter is optional
```
/query?name={country}&date={date}
```

Report:

The `limit`, `order_by`, and `threshold` (only for country endpoint) are optionals
```
/report/infections?limit={limit}&order_by={DESC|ASC}
/report/vaccinations?limit={limit}&order_by={DESC|ASC}
/report/country?limit={limit}&order_by={DESC|ASC}&threshold={threshold}
```

Other generic endpoints, those are used to query the raw data from the database:

```
/countries
/countries/:name
/countries/:continent
```

```
/infectionCases/raw
/infectionCases
```

```
/vaccinations/raw
/vaccinations
```
