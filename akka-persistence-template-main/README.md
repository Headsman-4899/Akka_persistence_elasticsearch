## Running the sample code

1. Start a Cassandra server by running:

```bash
sbt "runMain sample.cqrs.Main cassandra"
```

2. Start a node that runs the write model:

```bash
sbt -Dakka.cluster.roles.0=write-model "runMain sample.cqrs.Main 2551"
```
