<script setup lang='ts'>
import { computed, ref } from 'vue'
import { NButton, NInput, NModal, useMessage } from 'naive-ui'
import { fetchGetVerify, fetchVerify } from '@/api'
import { useAuthStore } from '@/store'
import Icon403 from '@/icons/403.vue'

interface Props {
  visible: boolean
}

defineProps<Props>()

const authStore = useAuthStore()

const ms = useMessage()

const loading = ref(false)
const loadingVer = ref(false)

const token = ref('')
const email = ref('')

const disabled = computed(() => !token.value.trim() || loading.value)
const disabledVer = ref(false)

/** 验证邮件验证码 */
async function handleGetVerify() {
  try {
    loadingVer.value = true
    await fetchGetVerify(email.value.trim())
    ms.success('success')
  }
  catch (error: any) {
    ms.error(error.message ?? 'error')
  }
  finally {
    loadingVer.value = false
  }
}

async function handleVerify() {
  const secretKey = token.value.trim()
  const emailStr = email.value.trim()

  if (!secretKey)
    return

  try {
    loading.value = true
    await fetchVerify(emailStr, secretKey)
    authStore.setToken(secretKey)
    ms.success('success')
    window.location.reload()
  }
  catch (error: any) {
    ms.error(error.message ?? 'error')
    authStore.removeToken()
    token.value = ''
  }
  finally {
    loading.value = false
  }
}

function handlePress(event: KeyboardEvent) {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    handleVerify()
  }
}
</script>

<template>
  <NModal :show="visible" style="width: 100%; max-width: 640px">
    <div class="p-10 bg-white rounded dark:bg-slate-800">
      <div class="space-y-4">
        <header class="space-y-2">
          <h2 class="text-2xl font-bold text-center text-slate-800 dark:text-neutral-200">
            403
          </h2>
          <p class="text-base text-center text-slate-500 dark:text-slate-500">
            {{ $t('common.unauthorizedTips') }}
          </p>
          <Icon403 class="w-[200px] m-auto" />
        </header>
        <NInput v-model:value="email" style="width:70%" type="text" placeholder="请输入邮箱获取验证码" @keypress="handlePress" />
        <NButton
          style="width:25%;margin-left:1%;position: absolute;"
          :disabled="disabledVer"
          :loading="loadingVer"
          type="primary"
          @click="handleGetVerify"
        >
          获取授权码
        </NButton>
        <NInput v-model:value="token" type="password" placeholder="请输入授权码" @keypress="handlePress" />
        <NButton
          block
          type="primary"
          :disabled="disabled"
          :loading="loading"
          @click="handleVerify"
        >
          {{ $t('common.verify') }}
        </NButton>
      </div>
    </div>
  </NModal>
</template>
