# !/bin/sh

while getopts 'r:s:a:v:' OPT; do
    case $OPT in
        r) docker_repostory=${OPTARG};;
        s) docker_registry_name=${OPTARG};;
        a) project_artifactid=${OPTARG};;
        v) project_version="${OPTARG}";;
    esac
done

echo $docker_repostory
echo $docker_registry_name
echo $project_artifactid
echo $project_version

# 构建镜像
docker build -t ${docker_repostory}/${docker_registry_name}/${project_artifactid}:${project_version} ./docker

# 发布镜像
docker push ${docker_repostory}/${docker_registry_name}/${project_artifactid}:${project_version}
