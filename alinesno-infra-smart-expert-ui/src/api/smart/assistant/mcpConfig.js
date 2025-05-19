import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// MCP配置接口
const prefix = '/api/infra/smart/assistant/mcpConfig/';

export default {
  // 获取当前组织的MCP配置
  getByOrgId(orgId) {
    return request({
      url: prefix + 'getByOrgId',
      method: 'get',
      params: { orgId }
    })
  },

  // 保存或更新MCP配置
  saveOrUpdate(data) {
    return request({
      url: prefix + 'saveOrUpdate',
      method: 'post',
      data: data
    })
  },

  // 验证MCP地址
  validateUrl(mcpUrl) {
    return request({
      url: prefix + 'validateUrl',
      method: 'post',
      params: { mcpUrl }
    })
  }
}    