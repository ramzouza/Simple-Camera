import requests
import pprint
import json
import flask 
from config import token
import re
# pretty prints json to terminal
dump = pprint.PrettyPrinter(indent=4).pprint

# dump obj to data.json
def file_dump(obj):
    with open('data.json', 'w') as outfile:
        json.dump(obj, outfile)

# make a get request to travis api
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


def latest_build():
    
    url = "/repo/7494371/builds?limit=1"
    build = travy_get(url)['builds'][0] # get build of latest commit
    return build

def get_build(build_no):
    # ref to failing build
    url = "/build/" + build_no # eg. 107300346 
    build = travy_get(url) # get specific build
    return build


def analyse_build(build):
    dump(build)
    jobs = build['jobs'] # get the jobs of the build
    logs = {"logs":{}, "commit":{}} # log object
    for job in jobs:
        full_job = travy_get(job['@href'])
        stage = full_job['stage']
        if stage['state'] == "failed":
            stage_name = stage['name'] # lint, build, test
            log = travy_get(job['@href']+"/log")['content'] # get log of the build
            logs['logs'][stage_name] = log # Assign it the log
            if stage_name == "Test":
                logs['logs'][stage_name] = []
                ex = re.compile("KtUnitTests > \w* FAILED\\r\\n.+?(?=\\r\\n)")
                x = ex.findall(log)
                for match in x:
                    arr = match.split("\r\n")
                    print(arr)
                    dictionary = { "test":arr[0] , "reason":arr[1].strip("    ") }
                    logs['logs'][stage_name].append(dictionary)
        elif stage['state'] == "canceled":
            log = {"@type":'error', "error_message":"Stage cancelled so job was not run"}
            logs['logs'][stage['name']] = log
        elif stage['state'] == "created":
            log = {"@type":'message', "message":"Stage created."}
            logs['logs'][stage['name']] = log
        else: # stage['state'] == "passed":
            print(stage['state'])
            log = {"@type":'message', "message":"Stage passed."}
            logs['logs'][stage['name']] = log
    logs['commit'] = build['commit']
    logs['@href'] = build['@href']
    logs['created_by'] = build['created_by']
    file_dump(logs)
    return logs

