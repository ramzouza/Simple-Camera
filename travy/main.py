import requests
import pprint
import json
import flask 
from config import token
dump = pprint.PrettyPrinter(indent=4).pprint
def file_dump(obj):
    with open('data.json', 'w') as outfile:
        json.dump(obj, outfile)


def travy_get(url):
    headers = {
    "Travis-API-Version": "3",
    "User-Agent" : "User-Agent: API Explorer",
    "Authorization": "token " + token
    }
    base = "https://api.travis-ci.com"
    request_url = base+url
    dump(request_url)
    return requests.get(request_url, headers=headers).json()

latest = True

if latest == True:
    url = "/repo/7494371/builds?limit=1"
    build = travy_get(url)['builds'][0]

else:
    # ref to failing build
    url = "/build/105866560"
    build = travy_get(url)


dump(build)
stages = build['stages']
jobs = build['jobs']
logs = {}
for job in jobs:
    full_job = travy_get(job['@href'])
    stage = full_job['stage']
    if stage['state'] == "failed":
        log = travy_get(job['@href']+"/log")['content']
        logs[stage['name']] = log
    elif stage['state'] == "canceled":
        log = {"@type":'error', "error_message":"Stage cancelled so job was not run"}
        logs[stage['name']] = log


from flask import Flask
from flask import jsonify
app = Flask(__name__)

@app.route('/')
def main():
    return jsonify(build)


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5003, debug=True)

file_dump(logs)





