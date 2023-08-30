#!/usr/bin/env python3

import argparse
import json
import requests
import time


def get_access_token(url, client_id='admin-cli', username='admin', password='admin', max_retries=5, retry_interval_seconds=5):
    access_token = None
    headers = {
        'Content-Type': 'application/x-www-form-urlencoded',
    }
    payload = {
        'grant_type': 'password',
        'client_id': 'admin-cli',
        'username': username,
        'password': password,
    }
    for retry_num in range(max_retries):
        try:
            token_response = requests.post(args.base_url + 'realms/master/protocol/openid-connect/token', headers=headers, data=payload)
            break
        except requests.exceptions.ConnectionError as e:
            retry_interval_seconds += 2
            time.sleep(retry_interval_seconds)
            print('retrying...')
        
    if token_response.status_code == requests.codes.ok:
        access_token = token_response.json()['access_token']

    return access_token


def main(args):
    access_token = get_access_token(args.base_url + 'realms/master/protocol/openid-connect/token')
    if access_token:
        headers = {
            'Authorization': 'Bearer ' + access_token,
            'Content-Type': 'application/json',
        }
        openid_config_response = requests.get(args.base_url + 'realms/master/.well-known/openid-configuration', headers=headers)
        print(json.dumps(openid_config_response.json(), indent=2))

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--base-url', default="http://localhost:8080/")
    parser.add_argument('--username', default="admin")
    parser.add_argument('--password', default="admin")
    parser.add_argument('--client-id', default="admin-cli")
    args = parser.parse_args()
    main(args)
