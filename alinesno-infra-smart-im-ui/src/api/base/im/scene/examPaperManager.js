import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 接口基础路径
const BASE_URL = '/api/infra/smart/assistant/scene/examPagerManager';

/**
 * 获取考试的考生列表
 */
export function getExamineeList(examId) {
  return request({
    url: `${BASE_URL}/examineeList?examId=${parseStrEmpty(examId)}`,
    method: 'get'
  })
}

/**
 * 获取到考试试卷信息
 */
export function getPaperInfo(pagerId) {
  return request({
    url: `${BASE_URL}/paperInfo?pagerId=${parseStrEmpty(pagerId)}`,
    method: 'get'
  })
}


/**
 * 导出试卷为Word文档
 * @param {number} pagerId 试卷ID
 * @param {boolean} showAnswer 是否显示答案
 * @returns {Promise} 返回文件下载流
 */
export function exportPaper(pagerId, showAnswer = false) {
  return request({
    url: `${BASE_URL}/export/${parseStrEmpty(pagerId)}`,
    method: 'get',
    params: { showAnswer },
    responseType: 'blob' // 重要：指定响应类型为blob
  })
};

/**
 * 获取试卷导出URL
 * @param {number} pagerId 试卷ID
 * @param {boolean} showAnswer 是否显示答案
 * @returns {Promise} 返回包含URL的响应
 */
export function getExportUrl(pagerId, showAnswer = false) {
  return request({
    url: `${BASE_URL}/export/url/${parseStrEmpty(pagerId)}`,
    method: 'get',
    params: { showAnswer }
  })
}