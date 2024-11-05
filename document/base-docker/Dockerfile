FROM registry.cn-shenzhen.aliyuncs.com/alinesno-infra-base/openjdk:17-slim

# 设置为中国国内源
RUN sed -i 's/deb.debian.org/mirrors.ustc.edu.cn/g' /etc/apt/sources.list
RUN sed -i 's/security.debian.org/mirrors.ustc.edu.cn/g' /etc/apt/sources.list

# 更新apt源并安装Python3和pip
RUN apt-get update && \
    apt-get install -y pandoc

# 验证是否安装成功
RUN pandoc --version
