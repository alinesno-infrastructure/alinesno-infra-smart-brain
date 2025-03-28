import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 接口配置项
var prefix = '/api/infra/smart/assistant/scene/' ;
// var chapterPrefix = '/api/infra/smart/assistant/sceneChapter/' ;
// var leaderPrefix = '/api/infra/smart/assistant/sceneLeader/' ;

var managerUrl = {
  // 场景
  createScene: prefix + "saveOrUpdate" , 
  // updateChapterEditor: prefix + "updateChapterEditor" , 
  getScene: prefix +"getScene",
  sceneList: prefix +"sceneList",
  uploadOss: prefix +"uploadOss",
  updateLeaderRole: prefix +"updateLeaderRole",
  supportScene: prefix +"supportScene",

  // 大文本
  // saveChapter: chapterPrefix +"saveChapters",
  // processParagraph: chapterPrefix +"processParagraph",
  // chatRole: chapterPrefix +"chatRole",
  // chatRoleSync: chapterPrefix +"chatRoleSync",
  // : chapterPrefix +"updateChapterContentEditor",
  // getChapterByRole: chapterPrefix +"getChapterByRole",
  // getChapterContent: chapterPrefix +"getChapterContent",
  // updateChapterContent: chapterPrefix +"updateChapterContent",

  // // 管理者
  // leaderPlan: leaderPrefix +"leaderPlan",
  // executeSceneTask: leaderPrefix +"executeSceneTask",
}

// 获取到支持场景
export function supportScene() {
  return request({
    url: managerUrl.supportScene,
    method: 'get'
  })
}

// // 运行任务
// export function executeSceneTask(data,uId) {
//   return request({
//     url: managerUrl.executeSceneTask + "?uId=" + parseStrEmpty(uId) , 
//     method: 'post',
//     data: data
//   })
// }

// // 运行场景计划
// export function leaderPlan(data) {
//   return request({
//     url: managerUrl.leaderPlan , 
//     method: 'post',
//     data: data
//   })
// }

// 更新leader角色
export function updateLeaderRole(data) {
  return request({
    url: managerUrl.updateLeaderRole,
    method: 'post',
    data: data
  })
}

// 上传文件到OSS
export function uploadOss(sceneId) {
  return request({
    url: managerUrl.uploadOss + '?sceneId=' + parseStrEmpty(sceneId),
    method: 'get',
  })
}

// 场景列表
export function sceneList(query) {
  return request({
    url: managerUrl.sceneList,
    method: 'get',
    params: query
  })
}

// 更新章节内容
export function updateChapterContent(data) {
  return request({
    url: managerUrl.updateChapterContent, 
    method: 'post',
    data: data
  })
}

// 内容片断修改
export function processParagraph(data) {
  return request({
    url: managerUrl.processParagraph, 
    headers: {
      isEncrypt: true
    },
    method: 'post',
    data: data
  })
}

// 查询章节内容
export function getChapterContent(id) {
  return request({
    url: chapterPrefix + 'getChapterContent?chapterId=' + parseStrEmpty(id),
    method: 'get'
  })
}

// 查询章节详细
export function getChapterByRole(roleId , sceneId) {
  return request({
    url: managerUrl.getChapterByRole + '?roleId=' + parseStrEmpty(roleId) + "&sceneId="+ parseStrEmpty(sceneId),
    method: 'get'
  })
}

// 更新编辑人员
// export function updateChapterContentEditor(data) {
//   return request({
//     url: managerUrl.updateChapterContentEditor,
//     method: 'post',
//     data: data
//   })
// }

// export function chatRoleSync(data) {
//   return request({
//     url: managerUrl.chatRoleSync , 
//     method: 'post',
//     data: data
//   })
// }

// 与Role聊天
// export function chatRole(data) {
//   return request({
//     url: managerUrl.chatRole , 
//     method: 'post',
//     data: data
//   })
// }

// 保存章节
// export function saveChapter(data , sceneId) {
//   return request({
//     url: managerUrl.saveChapter + "?sceneId=" + parseStrEmpty(sceneId) ,
//     method: 'post',
//     data: data
//   })
// }


// 查询场景详细
export function getScene(id) {
  return request({
    url: managerUrl.getScene + '?id=' + parseStrEmpty(id),
    method: 'get'
  })
}

// 更新章节编辑人员
// export function updateChapterEditor(id , editors , type) {

//   let data = {
//     id: parseStrEmpty(id),
//     editors: editors,
//     type: type
//   }

//   return request({
//     url: managerUrl.updateChapterEditor , 
//     headers: {
//       isEncrypt: true 
//     },
//     method: 'post',
//     data: data
//   })

// }

// 创建场景
export function createScene(data) {
  return request({
    url: managerUrl.createScene,
    headers: {
      isEncrypt: true
    },
    method: 'post',
    data: data
  })
}
