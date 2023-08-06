<script setup lang='ts'>
import { onMounted, ref } from 'vue'
import { NAvatar } from 'naive-ui'
import defaultAvatar from '@/assets/avatar.jpg'
import { isString } from '@/utils/is'
import type { UserInfo } from '@/store/modules/user/helper'
import { fetchChatUserInfo } from '@/api'

const loading = ref(false)

const userInfo = ref<UserInfo>({
  username: '',
  avatar: '',
  description: '',
})

async function fetchUserInfo() {
  try {
    loading.value = true
    const { data } = await fetchChatUserInfo<UserInfo>()

    userInfo.value = data

    // eslint-disable-next-line no-console
    console.log(`data = ${data.username}`)
  }
  finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<template>
  <div class="flex items-center overflow-hidden">
    <div class="w-10 h-10 overflow-hidden rounded-full shrink-0">
      <template v-if="isString(userInfo.avatar) && userInfo.avatar.length > 0">
        <NAvatar
          size="large"
          round
          :src="userInfo.avatar"
          :fallback-src="defaultAvatar"
        />
      </template>
      <template v-else>
        <NAvatar size="large" round :src="defaultAvatar" />
      </template>
    </div>
    <div class="flex-1 min-w-0 ml-2">
      <h2 class="overflow-hidden font-bold text-md text-ellipsis whitespace-nowrap">
        {{ userInfo.username ?? '新账户' }}
      </h2>
      <p class="overflow-hidden text-xs text-gray-500 text-ellipsis whitespace-nowrap">
        <span
          v-if="isString(userInfo.description) && userInfo.description !== ''"
          v-html="userInfo.description"
        />
      </p>
    </div>
  </div>
</template>
