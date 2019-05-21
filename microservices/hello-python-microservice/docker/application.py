#!flask/bin/python
from flask import Flask, jsonify, request
import os

app = Flask(__name__)


@app.route('/health')
def health():
    return '{"status":"UP"}'


@app.route('/hello')
def hello():
    # returns details about environment
    environment_data = {
        'platformUrl': os.getenv('C8Y_BASEURL'),
        'mqttPlatformUrl': os.getenv('C8Y_BASEURL_MQTT'),
        'tenant': os.getenv('C8Y_BOOTSTRAP_TENANT'),
        'user': os.getenv('C8Y_BOOTSTRAP_USER'),
        'password': os.getenv('C8Y_BOOTSTRAP_PASSWORD'),
        'microserviceIsolation': os.getenv('C8Y_MICROSERVICE_ISOLATION')
    }
    return jsonify(environment_data)


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=80, debug=True)

