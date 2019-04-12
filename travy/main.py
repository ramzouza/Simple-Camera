
from flask import Flask
from flask import jsonify
app = Flask(__name__)

from travy import file_dump, analyse_build, latest_build, travy_get, dump, get_build


@app.route('/')
def main():
    
    return '''
    <html>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
    <body>
    <a style="margin: 1em;" class="btn btn-success" href="/api/build/latest">Latest Build</a><br>
    <a style="margin: 1em;" class="btn btn-danger" href="/api/build/107300346">Build: 107300346</a><br>
    <a style="margin: 1em;" class="btn btn-danger" href="/api/build/105784318">Build: 105784318</a><br>
    </body>
    </html>
    '''

@app.route('/api/build/')
@app.route('/api/build/<buildNo>')
def build(buildNo="latest"):
    if buildNo == "latest":
        build = latest_build()
    else:
        build = get_build(buildNo)
    logs = analyse_build(build)
    return jsonify(logs)




if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5003, debug=True)






