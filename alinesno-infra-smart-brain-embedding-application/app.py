import os
import shutil
import requests
from zipfile import ZipFile
import flask
from flask import Flask
import logging
from sentence_transformers import SentenceTransformer

app = Flask(__name__)

# 配置日志级别和输出格式
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

MODEL_CACHE_PATH = os.path.expanduser("~/cache/model/")
MODEL_NAME = 'm3e-base'
MODEL_ZIP_URL = 'http://s4j8c97ta.hn-bkt.clouddn.com/moka-ai/m3e-base.zip'
MODEL_LOCAL_PATH = os.path.join(MODEL_CACHE_PATH, MODEL_NAME)

def download_and_extract_model():
    # 检查模型是否存在
    if not os.path.exists(MODEL_LOCAL_PATH):
        os.makedirs(MODEL_LOCAL_PATH, exist_ok=True)
        model_zip_path = os.path.join(MODEL_CACHE_PATH, f"{MODEL_NAME}.zip")
        # 下载模型ZIP文件
        response = requests.get(MODEL_ZIP_URL)
        with open(model_zip_path, 'wb') as f:
            f.write(response.content)
        # 解压模型ZIP文件
        with ZipFile(model_zip_path, 'r') as zip_ref:
            zip_ref.extractall(MODEL_LOCAL_PATH)
        # 删除模型ZIP文件
        os.remove(model_zip_path)
        logging.info("模型已下载并解压到本地")

@app.route('/embeddings', methods=['post'])
def embeddings():
    sentences = flask.request.form['text']

    # 检查并下载模型
    download_and_extract_model()

    # 加载本地模型
    model = SentenceTransformer(os.path.join(MODEL_LOCAL_PATH, MODEL_NAME))
    embeddings = model.encode(sentences)

    print(embeddings)

    return embeddings.tolist()

if __name__ == '__main__':
    app.debug = False
    handler = logging.FileHandler('flask.log')
    app.logger.addHandler(handler)

    # 检查并下载模型
    download_and_extract_model()

    app.run(port=5001, debug=False, host='0.0.0.0')

