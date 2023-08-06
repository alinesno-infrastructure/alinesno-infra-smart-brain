FROM registry.cn-shenzhen.aliyuncs.com/alinesno-base/nginx-alpha:1.1.4

WORKDIR /usr/share/nginx/html
COPY ./dist ./

ENV API_BASE_URL SERVER_URL
RUN echo 'API_BASE_URL = ' $API_BASE_URL

# 调整nginx后台地址
COPY ./nginx.conf /etc/nginx/

ADD replace_api_url.sh ./

CMD ["sh", "replace_api_url.sh"]
