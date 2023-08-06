import type { AxiosProgressEvent, GenericAbortSignal } from 'axios'
import { post } from '@/utils/request'
import { useSettingStore } from '@/store'

export function fetchChatAPI<T = any>(
  prompt: string,
  options?: { conversationId?: string; parentMessageId?: string },
  signal?: GenericAbortSignal,
) {
  return post<T>({
    url: '/api/chat',
    data: { prompt, options },
    signal,
  })
}

export function fetchChatUserInfo<T = any>() {
  return post<T>({
    url: '/api/userInfo',
  })
}

export function fetchChatConfig<T = any>() {
  return post<T>({
    url: '/api/config',
  })
}

export function fetchChatAPIProcess<T = any>(
  params: {
    prompt: string
    options?: { conversationId?: string; parentMessageId?: string }
    signal?: GenericAbortSignal
    onDownloadProgress?: (progressEvent: AxiosProgressEvent) => void },
) {
  const settingStore = useSettingStore()

  return post<T>({
    url: '/api/chat-process',
    data: { prompt: params.prompt, options: params.options, systemMessage: settingStore.systemMessage },
    signal: params.signal,
    onDownloadProgress: params.onDownloadProgress,
  })
}

export function fetchSession<T>() {
  return post<T>({
    url: '/api/session',
  })
}

export function fetchVerify<T>(email: string, token: string) {
  return post<T>({
    url: '/api/verify',
    data: { email, token },
  })
}

export function fetchGetVerify<T>(email: string) {
  return post<T>({
    url: '/api/getVerifyCode',
    data: { email },
  })
}
