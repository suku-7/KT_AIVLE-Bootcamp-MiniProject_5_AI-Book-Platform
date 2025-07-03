#!/bin/sh

echo "✅ Starting frontend using http-server"
echo "📂 Serving: /opt/www"
echo "🌐 Port: 8080"
echo "🔁 SPA mode: Enabled"

http-server /opt/www -p 8080 -s