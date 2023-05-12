#!/usr/bin/env python3

import argparse
import requests
import json

def main(args):
    instruments = []
    
    with open(args.input, 'r') as f:
        instruments = json.load(f)

    url = "http://localhost:8080/instruments/" + args.instrument_type
    for instrument in instruments:
        headers = {"Content-Type": "application/json",
                   "Accept": "application/json"}
        response = requests.post(url, json=instrument, headers=headers)
        print(json.dumps(json.loads(response.text), indent=2))

        
        

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('input')
    parser.add_argument('--instrument-type', default="illumina")
    args = parser.parse_args()
    main(args)
