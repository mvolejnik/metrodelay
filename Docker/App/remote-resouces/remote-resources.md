# Build

```
docker build -t metrodelay/remote-resources:0.0.1 .
```

# Run

```
docker run -ti metrodelay/remote-resources:0.0.1
```

# Run with Grafa Lokig log driver

```
docker run \
    --log-driver=loki \
    --log-opt loki-url="https://test:test@localhost/loki/api/v1/push" \
    --log-opt loki-retries=5 \
    --log-opt loki-batch-size=400 \
    -ti metrodelay/remote-resources:0.0.1
```