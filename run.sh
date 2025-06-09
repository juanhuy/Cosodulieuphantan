#!/bin/bash

echo "🚀 Bắt đầu build và chạy Kafka-Storm Topology..."

# Bước 1: Build project bằng Maven
echo "🔨 Đang build project..."
mvn clean package || { echo "❌ Build thất bại! Kiểm tra lỗi."; exit 1; }

# Bước 2: Tạo file classpath
echo "📦 Đang tạo classpath..."
mvn dependency:build-classpath -Dmdep.outputFile=cp.txt || { echo "❌ Không thể tạo classpath!"; exit 1; }

# Bước 3: Kiểm tra file classpath
if [ ! -f cp.txt ]; then
    echo "❌ File cp.txt không tồn tại! Kiểm tra lại Maven."
    exit 1
fi

# Bước 4: Đọc classpath và kiểm tra
CLASSPATH="target/classes:$(cat cp.txt)"
echo "📂 Classpath: $CLASSPATH"

# Bước 5: Kiểm tra class KafkaStormTopology có tồn tại không
CLASS_NAME="org.example.KafkaStormTopology"
if ! find target/classes -wholename "target/classes/$(echo $CLASS_NAME | tr '.' '/')".class | grep -q .; then
    echo "❌ Class $CLASS_NAME không tồn tại! Kiểm tra lại package."
    exit 1
fi

# Bước 6: Chạy ứng dụng
echo "🚀 Đang chạy ứng dụng Kafka-Storm Topology..."
java -cp "$CLASSPATH" "$CLASS_NAME" || { echo "❌ Lỗi khi chạy ứng dụng!"; exit 1; }

echo "✅ Ứng dụng chạy thành công!"
