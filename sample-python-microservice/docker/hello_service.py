
import urllib.request, json, base64
import os
from datetime import datetime, date, time
from random import randint
from urllib.request import Request
from urllib.request import urlopen

# values provided into environment by cumulocity platform during deployment
C8Y_BASEURL = os.getenv('C8Y_BASEURL')
C8Y_BOOTSTRAP_USER = os.getenv('C8Y_BOOTSTRAP_USER')
C8Y_BOOTSTRAP_TENANT = os.getenv('C8Y_BOOTSTRAP_TENANT')
C8Y_BOOTSTRAP_PASSWORD = os.getenv('C8Y_BOOTSTRAP_PASSWORD')

DEVICE_NAME = "hello-device"
DEVICE_TYPE = "hello-type"


# result is Base64 encoded "tenant/user:password"
def base64_credentials(tenant, user, password):
    str_credentials = tenant + "/" + user + ":" + password
    return 'Basic ' + base64.b64encode(str_credentials.encode()).decode()


def generate_sample_device():
    managed_object_data = {
        'name': DEVICE_NAME,
        'type': DEVICE_TYPE,
        'c8y_IsDevice': {}
    }
    return managed_object_data


def create_managed_object(data, credentials):
    req = Request(C8Y_BASEURL + '/inventory/managedObjects')
    req.add_header('Authorization', credentials)
    req.add_header('Content-Type', 'application/json')
    req.add_header('Accept', 'application/vnd.com.nsn.cumulocity.managedObject+json')
    req.data = json.dumps(data).encode()
    response = urlopen(req)
    return json.loads(response.read().decode())


def get_devices_by_type(device_type, credentials):
    req = Request(C8Y_BASEURL + '/inventory/managedObjects?type=' + str(device_type))
    req.add_header('Authorization', credentials)
    req.add_header('Content-Type', 'application/json')
    response = urlopen(req)
    return json.loads(response.read().decode())


def get_sample_devices(credentials):
    return get_devices_by_type(DEVICE_TYPE, credentials)


def create_measurement(data, credentials):
    req = Request(C8Y_BASEURL + '/measurement/measurements')
    req.add_header('Content-Type', 'application/json')
    req.add_header('Authorization', credentials)
    req.add_header('Accept', 'application/vnd.com.nsn.cumulocity.measurement+json')
    req.data = json.dumps(data).encode()
    response = urlopen(req)
    return json.loads(response.read().decode())


def random_measurement_for(device_id):
    measurement_data = {
        'source': {'id': str(device_id)},
        'type': 'c8y_TestMeasurement',
        'time': datetime.utcnow().strftime('%Y-%m-%dT%H:%M:%S.%f')[:-3] + 'Z',
        'c8y_TestMeasurement': {
            'T': {
                'value': randint(0, 1000)
            }
        }
    }
    return measurement_data


# subscriber has form of dictionary with 3 keys {tenant, user, password}
def get_subscriber_for(tenant_id):
    req = Request(C8Y_BASEURL + '/application/currentApplication/subscriptions')
    req.add_header('Accept', 'application/vnd.com.nsn.cumulocity.applicationUserCollection+json')
    req.add_header('Authorization', base64_credentials(C8Y_BOOTSTRAP_TENANT, C8Y_BOOTSTRAP_USER, C8Y_BOOTSTRAP_PASSWORD))
    response = urlopen(req)
    subscribers = json.loads(response.read().decode())["users"]
    return [s for s in subscribers if s["tenant"] == tenant_id][0]

