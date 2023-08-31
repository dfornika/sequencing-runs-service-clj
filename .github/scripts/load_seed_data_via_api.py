#!/usr/bin/env python3

import argparse
import glob
import json
import os

import requests

def main(args):
    illumina_instruments = []
    illumina_instruments_glob = os.path.join(args.seed_data, 'illumina_instruments', '*.json')
    illumina_instrument_paths = glob.glob(illumina_instruments_glob)
    for illumina_instrument_path in illumina_instrument_paths:
        with open(illumina_instrument_path, 'r') as f:
            illumina_instrument = json.load(f)
            try:
                post_instrument_response = requests.post(args.base_url + 'sequencing-instruments/illumina', json=illumina_instrument)
                try:
                    print(json.dumps(post_instrument_response.status_code, indent=2))
                    print(json.dumps(dict(post_instrument_response.headers), indent=2))
                    print(json.dumps(post_instrument_response.json(), indent=2))
                except requests.exceptions.JSONDecodeError as e:
                    print(post_instrument_response.text)
            except requests.exceptions.ConnectionError as e:
                print('connection failed')
            
          

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--seed-data')
    parser.add_argument('--base-url', default="http://localhost:8080/")
    args = parser.parse_args()
    main(args)
