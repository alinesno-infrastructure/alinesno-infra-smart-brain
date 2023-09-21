# 使用 OpenResty 作为基础镜像
FROM openresty/openresty:latest

# 将构建好的静态文件复制到 OpenResty 的默认静态文件目录
RUN cp -r dist/* /usr/local/openresty/nginx/html

# 复制 OpenResty 配置文件
COPY docker/nginx.conf /usr/local/openresty/nginx/conf/nginx.conf

# 暴露端口
EXPOSE 80

# 启动 OpenResty
CMD ["/usr/local/openresty/bin/openresty", "-g", "daemon off;"]
