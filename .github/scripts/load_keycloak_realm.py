#!/usr/bin/env python3

import argparse
import json
import requests


def get_access_token(url, username='admin', password='admin'):
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
    token_response = requests.post(args.base_url + 'realms/master/protocol/openid-connect/token', headers=headers, data=payload)
    if token_response.status_code == requests.codes.ok:
        access_token = token_response.json()['access_token']

    return access_token


def main(args):
    realm = None
    with open(args.realm_json, 'r') as f:
        realm = json.load(f)

    if not realm:
        exit(-1)

    access_token = get_access_token(args.base_url + 'realms/master/protocol/openid-connect/token')
    if access_token:
        headers = {
            'Authorization': 'Bearer ' + access_token,
            'Content-Type': 'application/json',
        }
        realms_response = requests.post(args.base_url + 'admin/realms', headers=headers, data=json.dumps(realm))
        if realms_response.status_code == 201:
            print("Realm '" + realm['realm'] + " created with id '" + realm['id'] + "'")
            exit(0)
        else:
            print("Failed to create realm '" + realm['realm'] + "'")
            print(realms_response.text)
            exit(-1)

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--base-url', default="http://localhost:8080/")
    parser.add_argument('--username', default="admin")
    parser.add_argument('--password', default="admin")
    parser.add_argument('--realm-json', required=True)
    args = parser.parse_args()
    main(args)
