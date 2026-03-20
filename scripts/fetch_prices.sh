#!/bin/bash

SYMBOLS=(
  "GC%3DF"
  "BTC-EUR"
  "SXR8.DE"
  "BRK-B"
  "GOOGL"
  "FLUT"
  "AAPL"
  "AMZN"
  "META"
  "MSFT"
  "NVDA"
  "TSLA"
)

for symbol in "${SYMBOLS[@]}"; do
  encoded=$(printf '%s' "$symbol" | sed 's/=/%3D/g')
  response=$(curl -s -H "User-Agent: Mozilla/5.0" \
    "https://query1.finance.yahoo.com/v8/finance/chart/${encoded}?interval=1d&range=1d")
  price=$(echo "$response" | jq -r '.chart.result[0].meta.regularMarketPrice')
  currency=$(echo "$response" | jq -r '.chart.result[0].meta.currency')
  echo "${symbol}: ${price} ${currency}"
done
