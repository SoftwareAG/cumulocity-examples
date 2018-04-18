echo "[INFO] Build image"
docker build -t python-hello-microservice .

echo "[INFO] Export image"
docker save python-hello-microservice > "image.tar"

echo "[INFO] Zip microservice"
zip hello-microservice "cumulocity.json" "image.tar"

echo "[INFO] Remove exported image"
rm "image.tar"