# CompraBien — Architecture

## Product principle

Every feature must improve a purchase decision. AI interprets and explains; deterministic services calculate; price data is the source of truth.

## Initial layers

- `presentation`: Jetpack Compose UI and state holders.
- `domain`: entities, use cases, scoring and optimization contracts.
- `data`: repositories and data sources.
- `core`: shared design, errors, time, money and common utilities.

The v0.1 repository starts as a single Android module to keep the first build simple. As Price Core and Catalog arrive, these boundaries will be extracted into Gradle modules without changing domain contracts.

## Planned engines

1. Price Intelligence Engine
2. Product Matching Engine
3. Basket Optimizer
4. Alert Engine

## First milestone

A reproducible Android build with CI and a minimal Compose shell. No fake retailer or price data will be presented as real data.
