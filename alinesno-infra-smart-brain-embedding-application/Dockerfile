# 使用官方提供的 Python 运行时作为基础镜像
FROM python:3.8

# 设置工作目录
WORKDIR /app

# 将当前目录中的所有文件复制到工作目录中
COPY . /app

# 安装所需的依赖项
COPY requirements.txt requirements.txt
# RUN pip3 install -i https://mirrors.aliyun.com/pypi/simple/ -r requirements.txt
RUN pip3 install -r requirements.txt

# 暴露端口
EXPOSE 5000

# 定义环境变量
ENV NAME World

# 运行应用
CMD ["python", "app.py"]
