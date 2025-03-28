/** 格式化消息内容 */
export function formatMessage(message, selectedUsers) {
  const words = message.split(' ');
  const formattedMessage = [];
  words.forEach((word) => {

  if (word.startsWith('@')) {
      const user = selectedUsers.find((u) => `@${u.roleName.toLowerCase()}` === word.toLowerCase());
      if (user) {
          formattedMessage.push({ type: 'mention', username: user.roleName , id: user.id });
      } else {
          formattedMessage.push({ type: 'text', text: word });
      }
    } else if (word.startsWith('#')) {
      const orderId = word.slice(1);
      formattedMessage.push({ type: 'business', businessId: orderId });
    } else {
      formattedMessage.push({ type: 'text', text: word });
    }

  });
  return formattedMessage;
}

/** 显示图片 */
export function imagePath(row){

  let icon = row.icon 

  // 判断是否是base64
  if (typeof icon === 'string' && (/^data:image\/jpeg;base64,/).test(icon)) {
    // 如果是Base64编码的JPEG图像，直接返回
    return icon;
  }

  if (typeof icon === 'string' && (/^\/9j\//).test(icon)) {
    // 如果是Base64编码的JPEG图像，添加data:image/jpeg;base64, 前缀
    return `data:image/jpeg;base64,${icon}`;
  }

  // 如果是http前缀则直接返回
  if(icon && icon.indexOf('http') === 0){
    return icon ; 
  }

  let roleAvatar = '1746435800232665090' ; 
  if(icon && icon){
    roleAvatar = icon; 
  }
  return import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/displayImage/" + roleAvatar ; 
}

/** 显示图片 */
export function imagePathByPath(roleAvatar){
  // 判断是否是base64
  if (typeof roleAvatar === 'string' && (/^\/9j\//).test(roleAvatar)) {
    // 如果是Base64编码的JPEG图像，添加data:image/jpeg;base64, 前缀
    return `data:image/jpeg;base64,${roleAvatar}`;
  }
  return import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/displayImage/" + roleAvatar ; 
}

/** 切换主题 */
export function toggleTheme(themeType) {
  localStorage.setItem('themeType', themeType)
  updateHtmlClass(themeType)
}

/** 更新html class */
export function updateHtmlClass(themeType) {
  if (themeType === 'dark') {
    document.documentElement.classList.add('dark')
  } else {
    document.documentElement.classList.remove('dark')
  }
}