/**
 * 文件下载工具类
 */
export const fileUtil = {
  /**
   * 下载Blob文件
   * @param {Blob} blob 文件数据
   * @param {string} fileName 文件名
   */
  downloadBlob(blob, fileName) {
    // 创建临时URL
    const url = window.URL.createObjectURL(new Blob([blob]))
    
    // 创建下载链接
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', fileName)
    
    // 触发下载
    document.body.appendChild(link)
    link.click()
    
    // 清理
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  },
  
  /**
   * 根据响应头获取文件名
   * @param {Object} response Axios响应对象
   * @returns {string} 文件名
   */
  getFileNameFromResponse(response) {
    const disposition = response.headers['content-disposition']
    if (disposition && disposition.indexOf('attachment') !== -1) {
      const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/
      const matches = filenameRegex.exec(disposition)
      if (matches != null && matches[1]) {
        return matches[1].replace(/['"]/g, '')
      }
    }
    return 'download_' + new Date().getTime()
  }
}