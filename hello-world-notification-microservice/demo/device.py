import base64
import json
import sys
from datetime import datetime
from time import sleep

from requests import Session
from requests.adapters import HTTPAdapter

_tenant_id = ''
_username = ''
_password = ''
_platform_url = ''


def get_session(tenant_id=_tenant_id, username=_username, password=_password):
    auth_string = f'{tenant_id}/{username}:{password}'
    session = Session()
    session.verify = True
    session.headers.update({
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Authorization': 'Basic ' + base64.b64encode(auth_string.encode()).decode()
    })
    http_adapter = HTTPAdapter()
    session.mount('https://', http_adapter)
    session.mount('http://', http_adapter)
    return session


def post_create_device(session, url=_platform_url):
    device_request = {
        "name": "testMeasurementDevice",
        "c8y_IsDevice": {},
        "c8y_SupportedMeasurements": ["c8y_Speed"]
    }
    json_body_str = json.dumps(device_request)
    return check_status_code(session.post(f'{url}/inventory/managedObjects', data=json_body_str), 201)


def post_delete_device(session, device_id, url=_platform_url, ):
    return check_status_code(session.delete(f'{url}/inventory/managedObjects/{device_id}'), 204)


def post_measurement(measurement_str, session, url=_platform_url):
    return check_status_code(session.post(f'{url}/measurement/measurements', data=measurement_str), 201)


def create_measurement_str(device_id):
    measurement = {
        'c8y_SpeedMeasurement': {
            'T': {
                'value': 100,
                'unit': 'km/h'
            }
        },
        'time': datetime.now().strftime('%Y-%m-%dT%H:%M:%S.000+00:00'),
        'source': {'id': str(device_id)},
        'type': 'c8y_Speed'
    }
    return json.dumps(measurement)


def check_status_code(_response, expected_code):
    if _response.status_code != expected_code:
        raise RuntimeError(f'Response code: {_response.status_code}', str(_response.json()))
    else:
        return _response


if __name__ == '__main__':
    args = sys.argv[1:]
    if len(args) > 0:
        command = args[0]
        if command in ['device', 'delete', 'send']:
            if command == 'device':
                print('Creating new device with c8y_Speed measurement support')
                request_session = get_session()
                device_id = post_create_device(request_session).json()['id']
                print(f'Device id: {device_id}')
            if command == 'delete':
                try:
                    request_session = get_session()
                    post_delete_device(request_session, args[1])
                    print(f'Device id: {args[1]} deleted')
                except IndexError:
                    print('Usage script.py delete [deviceId]')
            if command == 'send':
                try:
                    print(f'Attempting to send c8y_Speed measurements for {args[2]} seconds')
                    request_session = get_session()
                    duration = int(args[2])
                    count = 0
                    while count < duration:
                        response = post_measurement(create_measurement_str(args[1]), request_session)
                        print(f'Measurement created [{response.status_code}]')
                        count += 1
                        sleep(1)
                except (IndexError, ValueError):
                    print('Usage script.py send [deviceId] [duration in seconds]')

