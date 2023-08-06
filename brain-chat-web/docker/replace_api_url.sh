#!/usr/bin/env sh
find '/usr/share/nginx/html' -name 'config.js' -exec sed -i -e 's,SERVER_URL,'"$API_BASE_URL"',g' {} \;

# config cdn path
find '/usr/share/nginx/html' -type f \( -name "*.js" -o -name "*.html" \) -exec sed -i -e 's,SERVER_CDN_URL,'"$SERVER_CDN_URL"',g' {} \;

# config storage path
find '/usr/share/nginx/html' -type f \( -name "*.js" -o -name "*.html" \) -exec sed -i -e 's,SERVER_STORAGE_UPLOAD_URL,'"$SERVER_STORAGE_UPLOAD_URL"',g' {} \;
find '/usr/share/nginx/html' -type f \( -name "*.js" -o -name "*.html" \) -exec sed -i -e 's,SERVER_STORAGE_DISPLAY_URL,'"$SERVER_STORAGE_DISPLAY_URL"',g' {} \;

# 修改nginx后台地址
find '/etc/nginx' -type f \( -name "*.conf" -o -name "*.conf" \) -exec sed -i -e 's,UI_NGINX_BACKEND_URL,'"$UI_NGINX_BACKEND_URL"',g' {} \;

nginx -g "daemon off;"
