# Etapa de build
FROM ghcr.io/graalvm/graalvm-community:21 AS builder
WORKDIR /app
COPY . .
RUN ./gradlew nativeCompile

# Etapa final: só o binário
FROM ubuntu:22.04
WORKDIR /app
COPY --from=builder /app/build/native/nativeCompile/products-catalog .
EXPOSE 8080
CMD ["./products-catalog"]
