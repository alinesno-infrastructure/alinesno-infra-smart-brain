import numpy as np
from flask import Flask, jsonify, request
from sentence_transformers import SentenceTransformer

# 使用预训练的 SentenceTransformer 模型
model = SentenceTransformer('moka-ai/m3e-base')

app = Flask(__name__)

# 模型字典，暂时只有一个模型
models = {
    'text-embedding-ada-002': None  # 'text-embedding-ada-002': EmbeddingModel()
    # 原来这里一直用openai的ada，现在可以用m3e替换
}

@app.route('/api/ai/embeddings', methods=['post'])
def embed_text():
    data = request.get_json()
    texts = data.get('input', [])

    if not isinstance(texts, list):
        return jsonify({'error': 'inputs must be a list'}), 400

    # 使用默认的 m3e-base 模型
    result = {
        'data': [],
        'model': 'm3e-base',
        'object': 'list',
        'usage': {
            'prompt_tokens': 0,
            'total_tokens': 0
        }
    }

    embeddings = model.encode(texts)
    for i, embedding in enumerate(embeddings):
        normalized_embedding = embedding / np.linalg.norm(embedding)

        print(normalized_embedding.tolist())

        result['data'].append({
            'embedding': normalized_embedding.tolist(),  # 转换为列表
            'index': i,
            'object': 'embedding'
        })

    return jsonify(result)

# 结果就在 result，你后边自己用哪个向量库，就用哪个，有兴趣的小伙伴可以考虑将这个向量化流程整合到 dify，这样大家都可以使用了。
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5001)

