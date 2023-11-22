# coding:utf-8

import flask
from flask import Flask
import logging
from sentence_transformers import SentenceTransformer

app = Flask(__name__)

# 配置日志级别和输出格式
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

@app.route('/embeddings',methods=['post'])
def embeddings():
    sentences = flask.request.form['text']
    model = SentenceTransformer('moka-ai/m3e-base')
    embeddings = model.encode(sentences)

    print(embeddings)

    return embeddings.tolist()

if __name__ == '__main__':
    app.debug = False
    handler = logging.FileHandler('flask.log')
    app.logger.addHandler(handler)
    app.run(port=5001, debug=False, host='0.0.0.0')
