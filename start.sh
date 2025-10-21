#!/bin/bash

echo "Starting BookNest Online Bookstore System..."
echo ""
echo "Building project..."
./mvnw clean compile

if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

echo ""
echo "Starting application..."
echo "Server will be available at: http://localhost:4567"
echo "Default admin login: admin / admin123"
echo ""
echo "Press Ctrl+C to stop the server"
echo ""

./mvnw exec:java -Dexec.mainClass="com.example.booknest.Main"
