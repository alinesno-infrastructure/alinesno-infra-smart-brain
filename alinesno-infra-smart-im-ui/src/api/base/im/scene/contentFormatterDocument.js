import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 接口配置项
var prefix = '/api/infra/smart/assistant/scene/contentFormatterDocument/' ;

var managerUrl = { 
  datatables : prefix +"datatables" ,
  createNewDocument: prefix +"createNewDocument",
  detail: prefix +"detail",
  saveDocument: prefix +"saveDocument",
  uploadImage: prefix +"uploadImage",
}

// 上传文件
export function uploadFile(file, progressCallback) {
  const formData = new FormData();
  formData.append("file", file);
  
  return request({
    url: managerUrl.uploadImage,
    method: 'post',
    headers: {
      "Content-Type": "multipart/form-data",
    },
    onUploadProgress: (progressEvent) => {
      if (progressCallback) {
        progressCallback(
          Math.round((progressEvent.loaded / progressEvent.total) * 100)
        );
      }
    },
    data: formData
  });
}

// 查询频道列表
export function listDocument(query) {
  return request({
    url: managerUrl.datatables ,
    method: 'post',
    params: query
  })
}

// 文档详情
export function detail(documentId) {
  return request({
    url: managerUrl.detail + '/' + documentId,
    method: 'get'
  })
}

// 保存文档
export function saveDocument(data) {
  return request({
    url: managerUrl.saveDocument,
    method: 'post',
    data: data
  })
}

// 文档排版格式化
export function createNewDocument(sceneId) {
  return request({
    url: managerUrl.createNewDocument + '?sceneId=' + sceneId,
    method: 'get'
  })
}