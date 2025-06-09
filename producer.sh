#!/bin/bash

echo "Nhập tin nhắn để gửi vào Kafka (bấm Ctrl+D để kết thúc):"

# Đọc tin nhắn từ bàn phím và gửi vào Kafka
docker exec -i kafka kafka-console-producer --bootstrap-server kafka:9092 --topic test-topic
